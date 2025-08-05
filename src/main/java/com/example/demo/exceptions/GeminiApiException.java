package com.example.demo.exceptions;

public class GeminiApiException extends GeminiException {
	public GeminiApiException(String message) {
		super(message);
	}

	public GeminiApiException(String message, Throwable cause) {
		super(message, cause);
	}
}