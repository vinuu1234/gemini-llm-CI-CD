package com.example.demo.controller;

import com.example.demo.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class GeminiUIController {

    @Autowired
    private GeminiService geminiService;

    @GetMapping("/")
    public String showForm() {
        return "index";
    }

    @PostMapping("/submit")
    public String handleFormSubmission(@RequestParam(value = "prompt", required = false) String prompt,
                                       @RequestParam("file") MultipartFile file,
                                       Model model) {
        String response = geminiService.getGeminiResponse(prompt, file);
        model.addAttribute("response", response);
        return "index";
    }
}

