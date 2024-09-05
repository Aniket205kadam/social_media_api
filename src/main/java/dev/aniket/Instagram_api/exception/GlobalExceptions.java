package dev.aniket.Instagram_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptions {
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorDetails> handleAuthenticationException(AuthenticationException authenticationException, WebRequest webRequest) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ErrorDetails
                                .builder()
                                .message(authenticationException.getMessage())
                                .details(webRequest.getDescription(false))
                                .timestamp(LocalDateTime.now())
                                .build()
                );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(FfmpegException.class)
    public ResponseEntity<ErrorDetails> ffmpegExceptionHandler(FfmpegException ffmpegException, WebRequest webRequest) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ErrorDetails
                                .builder()
                                .message(ffmpegException.getMessage())
                                .details(webRequest.getDescription(false))
                                .timestamp(LocalDateTime.now())
                                .build()
                );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorDetails> userExceptionHandler(UserException userException, WebRequest webRequest) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ErrorDetails
                                .builder()
                                .message(userException.getMessage())
                                .details(webRequest.getDescription(false))
                                .timestamp(LocalDateTime.now())
                                .build()
                );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(PostException.class)
    public ResponseEntity<ErrorDetails> postExceptionHandler(PostException postException, WebRequest webRequest) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ErrorDetails
                                .builder()
                                .message(postException.getMessage())
                                .details(webRequest.getDescription(false))
                                .timestamp(LocalDateTime.now())
                                .build()
                );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(CommentException.class)
    public ResponseEntity<ErrorDetails> commentExceptionHandler(CommentException commentException, WebRequest webRequest) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ErrorDetails
                                .builder()
                                .message(commentException.getMessage())
                                .details(webRequest.getDescription(false))
                                .timestamp(LocalDateTime.now())
                                .build()
                );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(StoryException.class)
    public ResponseEntity<ErrorDetails> storyExceptionHandler(StoryException storyException, WebRequest webRequest) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ErrorDetails
                                .builder()
                                .message(storyException.getMessage())
                                .details(webRequest.getDescription(false))
                                .timestamp(LocalDateTime.now())
                                .build()
                );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> methodArgumentNotValidException(
            MethodArgumentNotValidException methodArgumentNotValidException,
            WebRequest webRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ErrorDetails
                                .builder()
                                .message(methodArgumentNotValidException
                                        .getBindingResult()
                                        .getFieldError()
                                        .getDefaultMessage())
                                .details(webRequest.getDescription(false)) // "validation error"
                                .timestamp(LocalDateTime.now())
                                .build()
                );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> otherExceptionHandler(Exception exception, WebRequest webRequest) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ErrorDetails
                                .builder()
                                .message(exception.getMessage())
                                .details(webRequest.getDescription(false))
                                .timestamp(LocalDateTime.now())
                                .build()
                );
    }
}
