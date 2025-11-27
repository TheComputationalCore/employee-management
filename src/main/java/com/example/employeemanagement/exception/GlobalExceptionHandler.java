package com.example.employeemanagement.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    /* =====================================================
       HELPER — Detect if request is API or Web
    ====================================================== */
    private boolean isApiRequest(WebRequest request) {
        String path = request.getDescription(false);
        return path.contains("/api/");
    }

    /* =====================================================
       ENTITY NOT FOUND (404)
    ====================================================== */
    @ExceptionHandler(EntityNotFoundException.class)
    public Object handleNotFound(EntityNotFoundException ex, WebRequest request, Model model) {

        if (isApiRequest(request)) {
            return buildJsonError(HttpStatus.NOT_FOUND, ex.getMessage());
        }

        model.addAttribute("errorCode", "404");
        model.addAttribute("message", ex.getMessage());
        return "error/404";
    }

    /* =====================================================
       BAD REQUEST (400)
    ====================================================== */
    @ExceptionHandler(IllegalArgumentException.class)
    public Object handleBadRequest(IllegalArgumentException ex, WebRequest request, Model model) {

        if (isApiRequest(request)) {
            return buildJsonError(HttpStatus.BAD_REQUEST, ex.getMessage());
        }

        model.addAttribute("errorCode", "400");
        model.addAttribute("message", ex.getMessage());
        return "error/400";
    }

    /* =====================================================
       GENERAL SERVER ERROR (500)
    ====================================================== */
    @ExceptionHandler(Exception.class)
    public Object handleGeneral(Exception ex, WebRequest request, Model model) {

        if (isApiRequest(request)) {
            return buildJsonError(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong.");
        }

        model.addAttribute("errorCode", "500");
        model.addAttribute("message", "Something went wrong.");
        return "error/500";
    }

    /* =====================================================
       404 FOR INVALID ROUTES
    ====================================================== */
    @ExceptionHandler(NoHandlerFoundException.class)
    public String handle404(Model model) {
        model.addAttribute("errorCode", "404");
        model.addAttribute("message", "Page Not Found");
        return "error/404";
    }

    /* =====================================================
       JSON ERROR BUILDER
    ====================================================== */
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
}
