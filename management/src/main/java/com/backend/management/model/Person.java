package com.backend.management.model;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class Person {
    private String name;

    @Email(message = "email khong dung dinh dang")
    @NotBlank(message = "email khong duoc de trong")
    private String email;
    private String phoneNumber;
    private String address;

    public Person() {
    }

    public Person(String name, String email, String phoneNumber, String address) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
