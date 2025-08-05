package com.example.demo.exceptions;

//Custom exception for file errors
public class FileProcessingException extends RuntimeException {
	public FileProcessingException(String message, Throwable cause) {
		super(message, cause);
	}
}