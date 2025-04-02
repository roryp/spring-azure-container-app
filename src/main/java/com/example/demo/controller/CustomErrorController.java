package com.example.demo.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        // Get error status
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR; // Default to 500
        
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            httpStatus = HttpStatus.valueOf(statusCode);
        }
        
        // Get error details
        String errorMessage = "An unexpected error occurred while processing your request.";
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        if (message != null && !message.toString().isEmpty()) {
            errorMessage = message.toString();
        }
        
        // Get exception details
        Throwable throwable = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        
        // Format current timestamp - using ZonedDateTime instead of LocalDateTime to include timezone
        ZonedDateTime now = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E MMM dd HH:mm:ss z yyyy");
        
        // Add attributes to model
        model.addAttribute("timestamp", now.format(formatter));
        model.addAttribute("status", httpStatus.value());
        model.addAttribute("error", httpStatus.getReasonPhrase());
        model.addAttribute("message", errorMessage);
        
        if (throwable != null) {
            model.addAttribute("exception", throwable);
        }
        
        return "error";
    }
}