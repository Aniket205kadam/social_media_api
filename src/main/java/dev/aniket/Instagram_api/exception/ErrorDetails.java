package dev.aniket.Instagram_api.exception;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public class ErrorDetails {
    private String message;
    private String details;
    private LocalDateTime timestamp;
}
