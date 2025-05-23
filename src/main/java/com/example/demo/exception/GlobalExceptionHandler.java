package com.example.demo.exception;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // バリデーションエラー用（@Valid）
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));

        logger.error("バリデーションエラー: {}", errorMessages, ex); // ← メソッド内でログ出力

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("バリデーションエラー: " + errorMessages);
    }

    // JSONパースエラー用（@RequestBody の型不一致など）
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleJsonParseException(HttpMessageNotReadableException ex) {
        String message = ex.getMostSpecificCause().getMessage();

        logger.error("JSONパースエラー: {}", message, ex); // ← メソッド内でログ出力

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("JSONパースエラー: " + message);
    }
}
