package com.backend.management.controller;


import com.backend.management.model.MailStructure;
import com.backend.management.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
public class MailController {

    @Autowired
    private MailService mailService;

    @PostMapping("/send/{email}")
    public String sendEmail(@PathVariable String email, @RequestBody MailStructure mailStructure){
            mailService.sendMail(email,mailStructure);
            return "success";

    }

}
