package com.example.demo.dto;

import java.util.List;

public class GeminiRequest {
    private List<Content> contents;

    // Getters and setters
    public List<Content> getContents() {
        return contents;
    }

    public void setContents(List<Content> contents) {
        this.contents = contents;
    }
}