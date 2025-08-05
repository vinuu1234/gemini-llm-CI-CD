package com.example.demo.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.Candidate; // ADD THIS IMPORT
import com.example.demo.dto.Content;
import com.example.demo.dto.GeminiRequest;
import com.example.demo.dto.GeminiResponse;
import com.example.demo.dto.InlineData;
import com.example.demo.dto.Part;
import com.example.demo.exceptions.FileProcessingException;
import com.example.demo.exceptions.GeminiApiException;
import com.example.demo.exceptions.GeminiContentException; // ADD THIS IMPORT
import com.example.demo.exceptions.GeminiSafetyException; // ADD THIS IMPORT
import com.example.demo.exceptions.InvalidInputException;

@Service
public class GeminiService {

	private static final Logger log = LoggerFactory.getLogger(GeminiService.class);

	private final String apiUrl;
	private final RestTemplate restTemplate;

	public GeminiService(@Value("${gemini.api.base-url}") String baseUrl, @Value("${gemini.api.key}") String apiKey,
			RestTemplateBuilder builder) {

		this.apiUrl = baseUrl + apiKey;
		this.restTemplate = builder.build();
		log.info("Initialized GeminiService with URL: {}", this.apiUrl);
	}

	public String getGeminiResponse(String prompt, MultipartFile file) {
		validateInput(prompt, file);
		GeminiRequest request = buildRequest(prompt, file);
		HttpEntity<GeminiRequest> entity = new HttpEntity<>(request);

		try {
			ResponseEntity<GeminiResponse> response = restTemplate.postForEntity(apiUrl, entity, GeminiResponse.class);
			validateApiResponse(response);
			return extractResponseText(response.getBody());
		} catch (RestClientException e) {
			String errorMsg = "API communication failed: " + e.getMessage();
			log.error(errorMsg, e);
			throw new GeminiApiException(errorMsg, e);
		}
	}

	private void validateInput(String prompt, MultipartFile file) {
		boolean isPromptEmpty = (prompt == null || prompt.isBlank());
		boolean isFileEmpty = (file == null || file.isEmpty());

		if (isPromptEmpty && isFileEmpty) {
			throw new InvalidInputException("Prompt or file must be provided");
		}
	}

	private GeminiRequest buildRequest(String prompt, MultipartFile file) {
	    GeminiRequest request = new GeminiRequest();
	    Content content = new Content();
	    content.setRole("user");

	    List<Part> parts = new ArrayList<>();

	    // If prompt is provided, include it as a Part
	    if (prompt != null && !prompt.isBlank()) {
	        Part promptPart = new Part();
	        promptPart.setText(prompt);
	        parts.add(promptPart);
	    }

	    // If file is provided, process and include its content
	    if (file != null && !file.isEmpty()) {
	        try {
	            String contentType = file.getContentType();
	            String fileName = file.getOriginalFilename();
	            String extractedText = "";

	            if ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(contentType)
	                    || "application/vnd.ms-excel".equals(contentType)
	                    || (fileName != null && (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")))) {
	                extractedText = ExcelTextExtractor.extractTextFromExcel(file);
	            } else if ("application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(contentType)) {
	                extractedText = DocxTextExtractor.extractTextFromDoc(file);
	            } else {
	                // For other supported file types (images, pdf, etc.)
	                InlineData inlineData = new InlineData();
	                inlineData.setMimeType(contentType);
	                inlineData.setData(Base64.getEncoder().encodeToString(file.getBytes()));
	                Part filePart = new Part();
	                filePart.setInlineData(inlineData);
	                parts.add(filePart);
	            }

	            // If file text was extracted, add as part
	            if (!extractedText.isBlank()) {
	                Part textPart = new Part();
	                textPart.setText(extractedText);
	                parts.add(textPart);
	            }

	        } catch (IOException e) {
	            throw new FileProcessingException("Failed to process file", e);
	        }
	    }

	    // Final check to avoid empty requests
	    if (parts.isEmpty()) {
	        throw new InvalidInputException("Prompt or file content must be provided");
	    }

	    content.setParts(parts);
	    request.setContents(Collections.singletonList(content));
	    return request;
	}

	// ADD THESE MISSING METHODS
	private void validateApiResponse(ResponseEntity<GeminiResponse> response) {
		if (!response.getStatusCode().is2xxSuccessful()) {
			String errorMsg = "API returned HTTP " + response.getStatusCode();
			System.out.println(errorMsg);
			log.error(errorMsg);
			throw new GeminiApiException(errorMsg);
		}
		if (response.getBody() == null) {
			String errorMsg = "No response body from API";
			log.error(errorMsg);
			throw new GeminiApiException(errorMsg);
		}
	}

	private String extractResponseText(GeminiResponse response) {
		if (response.getCandidates() == null || response.getCandidates().isEmpty()) {
			log.error("No response candidates. Full response: {}", response);
			throw new GeminiContentException("No response candidates");
		}

		Candidate candidate = response.getCandidates().get(0);
		if (candidate == null) {
			log.error("Empty candidate data. Full response: {}", response);
			throw new GeminiContentException("Empty candidate data");
		}

		// Handle safety filters
		if ("SAFETY".equals(candidate.getFinishReason())) {
			log.warn("Response blocked by safety filters. Finish reason: {}", candidate.getFinishReason());
			throw new GeminiSafetyException("Response blocked by safety filters");
		}

		Content content = candidate.getContent();
		if (content == null) {
			log.error("Missing content in candidate. Full candidate: {}", candidate);
			throw new GeminiContentException("Missing content in candidate");
		}

		List<Part> parts = content.getParts();
		if (parts == null || parts.isEmpty()) {
			log.error("No content parts available. Full content: {}", content);
			throw new GeminiContentException("No content parts available");
		}

		// Improved text extraction with better error handling
		for (int i = 0; i < parts.size(); i++) {
			Part part = parts.get(i);
			if (part == null) {
				log.warn("Part at index {} is null", i);
				continue;
			}

			String text = part.getText();
			if (text != null && !text.isBlank()) {
				return text;
			}

			// Log why we're skipping this part
			if (part.getInlineData() != null) {
				log.debug("Skipping inline data part at index {}", i);
			} else if (text == null) {
				log.warn("Part at index {} has null text", i);
			} else if (text.isBlank()) {
				log.warn("Part at index {} has empty text", i);
			}
		}

		// Final fallback with detailed error information
		String errorMsg = "No valid text response found. Parts count: " + parts.size();
		log.error("{}. Parts details: {}", errorMsg, parts);
		throw new GeminiContentException(errorMsg);
	}
}