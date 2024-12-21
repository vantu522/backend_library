package com.backend.management.service;


import com.backend.management.model.Contact;
import com.backend.management.model.ContactRequest;
import com.backend.management.repository.ContactRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ContactService {

    @Autowired
    private ContactRepo contactRepo;

    public Contact createContact(ContactRequest contactRequest) {
        Contact contact = new Contact();
        contact.setName(contactRequest.getName());
        contact.setEmail(contactRequest.getEmail());
        contact.setMessage(contactRequest.getMessage());
        contact.setPhoneNumber(contactRequest.getPhoneNumber());
        return contactRepo.save(contact);
    }

    public List<Contact> getAllContacts() {
        return contactRepo.findAll();
    }

    public List<Contact> getContactsByStatus(String status) {
        return contactRepo.findByStatusOrderByCreatedAtDesc(status);
    }

    public List<Contact> getContactsByEmail(String email) {
        return contactRepo.findByEmailOrderByCreatedAtDesc(email);
    }

    public Optional<Contact> getContactById(String id) {
        return contactRepo.findById(id);
    }

    public Contact updateContactStatus(String id, String status) {
        return contactRepo.findById(id)
                .map(contact -> {
                    contact.setStatus(status);
                    return contactRepo.save(contact);
                })
                .orElseThrow(() -> new RuntimeException("Contact not found"));
    }

    public Contact respondToContact(String id, String response) {
        return contactRepo.findById(id)
                .map(contact -> {
                    contact.setResponse(response);
                    contact.setStatus("RESPONDED");
                    contact.setRespondedAt(LocalDateTime.now());
                    return contactRepo.save(contact);
                })
                .orElseThrow(() -> new RuntimeException("Contact not found"));
    }
}