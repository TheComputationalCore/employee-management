package com.example.employeemanagement.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    /* -------------------------------
       API ERROR RESPONSE (JSON)
    -------------------------------- */

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleNotFound(EntityNotFoundException ex) {
        return buildJsonError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleInvalid(IllegalArgumentException ex) {
        return buildJsonError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneral(Exception ex) {
        return buildJsonError(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong.");
    }

    private ResponseEntity<?> buildJsonError(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(
                Map.of(
                        "timestamp", LocalDateTime.now().toString(),
                        "status", status.value(),
                        "error", status.getReasonPhrase(),
                        "message", message
                )
        );
    }


    /* -------------------------------
       WEB ERROR PAGES (THYMELEAF)
    -------------------------------- */

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handle404(Model model) {
        model.addAttribute("errorCode", "404");
        model.addAttribute("message", "Page Not Found");
        return "error/404";
    }

    @ExceptionHandler(RuntimeException.class)
    public String handle500(Model model, RuntimeException ex) {
        model.addAttribute("errorCode", "500");
        model.addAttribute("message", ex.getMessage());
        return "error/500";
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public String handle400(Model model, IllegalArgumentException ex) {
        model.addAttribute("errorCode", "400");
        model.addAttribute("message", ex.getMessage());
        return "error/400";
    }
}
