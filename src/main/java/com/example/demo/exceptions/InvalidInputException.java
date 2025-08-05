package com.example.demo.exceptions;

//Custom exception for invalid input
public class InvalidInputException extends RuntimeException {
	public InvalidInputException(String message) {
		super(message);
	}
}
