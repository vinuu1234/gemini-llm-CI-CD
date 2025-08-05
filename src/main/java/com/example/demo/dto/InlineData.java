package com.example.demo.dto;

public class InlineData {
    private String mimeType;
    private String data;  // Base64 encoded string

    // Getters and setters
    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}