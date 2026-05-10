package com.hyperativa.card.exception;

public record ErrorResponse(
        int status,
        String message,
        long timestamp
) {}
