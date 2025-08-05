package com.example.demo.exceptions;

//Specific Exceptions
public class InvalidPromptException extends GeminiException {
 public InvalidPromptException(String message) {
     super(message);
 }
}
