package com.backend.management.service;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;


@Service
public class ValidationService {
    private static final String EMAIL_REGEX=
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(stu\\.ptit\\.edu\\.vn|[a-zA-Z0-9-]+\\.[a-zA-Z]{2,})$";

    public boolean isValidEmail(String email){
        return email != null &&
                Pattern.matches(EMAIL_REGEX, email) &&
                email.length() <= 255;
    }
    // phan loai email
    public String getEmailType(String email){
        if(email ==null){
            return "Valid";
        }
        if(email.endsWith("@stu.ptit.edu.vn")){
            return "PTIT Student Email";
        } else if (email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}$")) {
            return "General Email";
        }
        return "Invalid";

    }
}
