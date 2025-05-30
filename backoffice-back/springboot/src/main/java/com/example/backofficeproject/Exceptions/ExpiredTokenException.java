package com.example.backofficeproject.Exceptions;

public class ExpiredTokenException extends RuntimeException {
        public ExpiredTokenException(String message) {
            super(message);
        }
    }
