package com.backend.management.service;

import com.backend.management.model.Feedback;
import com.backend.management.repository.FeedbackRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeedbackService {
    @Autowired
    private FeedbackRepo feedbackRepo;
    @Autowired
    private EmailService emailService;

    public Feedback submitFeedback(Feedback feedback) {
        feedback.setStatus("Pending");
        feedback.setCreatedAt(LocalDateTime.now());
        return feedbackRepo.save(feedback);
    }

    public List<Feedback> getFeedbacksByStatus(String status) {
        return feedbackRepo.findByStatus(status);
    }

    public List<Feedback> searchFeedbacks(String keyword) {
        return feedbackRepo.findByNameContainingOrEmailContainingOrContentContaining(keyword, keyword, keyword);
    }

    public Feedback respondToFeedback(String id, String response) {
        Feedback feedback = new Feedback(); // Khởi tạo phản hồi mặc định
        try {
            // Tìm phản hồi theo id
            feedback = feedbackRepo.findById(id).orElse(null);
            if (feedback == null) {
                System.err.println("Không tìm thấy phản hồi với id: " + id);
                feedback.setStatus("Error");
                feedback.setResponse("Không tìm thấy phản hồi");
                return feedback; // Trả về lỗi không tìm thấy
            }

            // Kiểm tra trạng thái phản hồi
            if ("Responded".equals(feedback.getStatus())) {
                System.err.println("Feedback đã được phản hồi với id: " + id);
                feedback.setStatus("Error");
                feedback.setResponse("Feedback đã được phản hồi");
                return feedback; // Trả về lỗi đã được phản hồi
            }

            // Cập nhật phản hồi
            feedback.setResponse(response);
            feedback.setStatus("Responded");
            feedback.setRespondedAt(LocalDateTime.now());
            feedbackRepo.save(feedback);

            // Gửi phản hồi qua email
            try {
                emailService.sendFeedBackResponseEmail(feedback.getName(), feedback.getEmail(), feedback.getContent(), response);
            } catch (Exception emailException) {
                System.err.println("Không thể gửi email phản hồi: " + emailException.getMessage());
                feedback.setStatus("Error");
                feedback.setResponse("Không thể gửi email phản hồi");
                return feedback; // Trả về lỗi gửi email
            }

        } catch (RuntimeException e) {
            System.err.println("Lỗi xử lý phản hồi: " + e.getMessage());
            feedback.setStatus("Error");
            feedback.setResponse("Lỗi trong quá trình xử lý phản hồi");
        } catch (Exception e) {
            System.err.println("Lỗi không xác định: " + e.getMessage());
            feedback.setStatus("Error");
            feedback.setResponse("Đã xảy ra lỗi không xác định");
        }

        return feedback;
    }
    public void deleteFeedback(String id) {
        feedbackRepo.deleteById(id);

    }
}


