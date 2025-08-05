package com.example.demo.dto;

import java.util.List;

public class Content {
    private String role;
    private List<Part> parts;  // Must match your usage

    // Getters and setters
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Part> getParts() {
        return parts;
    }

    public void setParts(List<Part> parts) {
        this.parts = parts;
    }
}