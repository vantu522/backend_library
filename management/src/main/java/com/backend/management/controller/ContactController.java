package com.backend.management.controller;


import com.backend.management.model.Contact;
import com.backend.management.model.ContactRequest;
import com.backend.management.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/contacts")
@CrossOrigin(origins = "*")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping
    public ResponseEntity<Contact> createContact(@Valid @RequestBody ContactRequest contactRequest) {
        Contact savedContact = contactService.createContact(contactRequest);
        return ResponseEntity.ok(savedContact);
    }

    @GetMapping
    public ResponseEntity<List<Contact>> getAllContacts() {
        return ResponseEntity.ok(contactService.getAllContacts());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Contact>> getContactsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(contactService.getContactsByStatus(status));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<List<Contact>> getContactsByEmail(@PathVariable String email) {
        return ResponseEntity.ok(contactService.getContactsByEmail(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contact> getContactById(@PathVariable String id) {
        return contactService.getContactById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Contact> updateStatus(
            @PathVariable String id,
            @RequestParam String status
    ) {
        return ResponseEntity.ok(contactService.updateContactStatus(id, status));
    }

    @PostMapping("/{id}/respond")
    public ResponseEntity<Contact> respondToContact(
            @PathVariable String id,
            @RequestBody String response
    ) {
        return ResponseEntity.ok(contactService.respondToContact(id, response));
    }
}