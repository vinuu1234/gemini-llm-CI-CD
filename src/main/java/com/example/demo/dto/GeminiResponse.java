package com.example.demo.dto;

import java.util.List;

public class GeminiResponse {
    private List<Candidate> candidates;

    // Getters and setters
    public List<Candidate> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<Candidate> candidates) {
        this.candidates = candidates;
    }
}