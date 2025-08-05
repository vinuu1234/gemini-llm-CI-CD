package com.example.demo.dto;

public class Candidate {
    private Content content;
    private String finishReason;

    // Getters and setters
    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public String getFinishReason() {
        return finishReason;
    }

    public void setFinishReason(String finishReason) {
        this.finishReason = finishReason;
    }
}