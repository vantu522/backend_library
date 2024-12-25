package com.backend.management.controller;

import com.backend.management.model.Feedback;
import com.backend.management.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    public Feedback submitFeedback(@RequestBody Feedback feedback) {
        return feedbackService.submitFeedback(feedback);
    }

    @GetMapping
    public List<Feedback> getFeedbacksByStatus(@RequestParam(required = false) String status) {
        if (status == null) {
            return feedbackService.getFeedbacksByStatus("Pending");
        }
        return feedbackService.getFeedbacksByStatus(status);
    }

    @GetMapping("/search")
    public List<Feedback> searchFeedbacks(@RequestParam String keyword) {
        return feedbackService.searchFeedbacks(keyword);
    }

    @PostMapping("/{id}/respond")
    public Feedback respondToFeedback(@PathVariable String id, @RequestBody String response) {
        return feedbackService.respondToFeedback(id, response);
    }
    @DeleteMapping("/delete/{id}")
    public void deleteFeedback(@PathVariable String id) {
         feedbackService.deleteFeedback(id);
    }
}
