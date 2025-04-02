package com.example.demo.controller;

import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.HashMap;

/**
 * Main controller handling web page requests and user profile information
 * Provides endpoints for home page, user profile, and user API
 */
@Controller
public class HomeController {

    private final UserService userService;

    @Autowired
    public HomeController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Home page endpoint
     * Accessible to all users (authenticated and anonymous)
     */
    @GetMapping("/")
    public String getHome(Model model, @AuthenticationPrincipal OidcUser principal) {
        // Set welcome message
        model.addAttribute("message", "Welcome to the Azure Container App!");
        // Add user name to model if authenticated
        if (principal != null) {
            model.addAttribute("userName", principal.getName());
        }
        // Render index template
        return "index";
    }

    /**
     * Profile page endpoint
     * Only accessible to authenticated users
     * Shows Azure Entra ID profile information
     */
    @GetMapping("/profile")
    public String getProfile(Model model, @AuthenticationPrincipal OidcUser principal) {
        if (principal == null) {
            // If user is not authenticated, redirect to home
            return "redirect:/";
        }
        
        // Extract OIDC claims from authenticated user's principal
        Map<String, Object> userInfo = principal.getClaims();
        // Populate model with user info and page title
        model.addAttribute("userInfo", userInfo);
        model.addAttribute("message", "Your Profile");
        // Render profile template
        return "profile";
    }

    /**
     * REST endpoint providing user information as JSON
     * Used for client-side API access to user data
     */
    @GetMapping("/api/user")
    @ResponseBody
    public Map<String, Object> getUserInfo() {
        try {
            // Delegate to userService to retrieve user details
            return userService.getUserInfo();
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to retrieve user information: " + e.getMessage());
            return errorResponse;
        }
    }
}