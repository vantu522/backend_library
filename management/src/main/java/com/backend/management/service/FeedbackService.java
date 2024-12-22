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
        feedback.setStatus("New");
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
        try {
            Feedback feedback = feedbackRepo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy phản hồi "));
            if ("Responded".equals(feedback.getStatus())) {
                throw new RuntimeException("Feedback đã được phản hồi ");
            }
            feedback.setResponse(response);
            feedback.setStatus("Responded");
            feedback.setRespondedAt(LocalDateTime.now());
            feedbackRepo.save(feedback);

            // Gửi phản hồi qua email
            emailService.sendFeedBackResponseEmail(feedback.getEmail(), feedback.getName(), feedback.getContent(), response);

            return feedback;
        } catch (Exception e) {
            // Log lỗi chi tiết và trả về lỗi
            System.err.println("Error processing feedback: " + e.getMessage());
            throw new RuntimeException("Internal Server Error");
        }
    }

}
