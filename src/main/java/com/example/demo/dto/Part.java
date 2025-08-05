package com.example.demo.dto;

public class Part {
    private String text;
    private InlineData inlineData;  // Must match your file handling

    // Getters and setters
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public InlineData getInlineData() {
        return inlineData;
    }

    public void setInlineData(InlineData inlineData) {
        this.inlineData = inlineData;
    }
}