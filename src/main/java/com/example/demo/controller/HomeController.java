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

@Controller
public class HomeController {

    private final UserService userService;

    @Autowired
    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String getHome(Model model, @AuthenticationPrincipal OidcUser principal) {
        model.addAttribute("message", "Welcome to the Azure Container App!");
        if (principal != null) {
            model.addAttribute("userName", principal.getName());
        }
        return "index";
    }

    @GetMapping("/profile")
    public String getProfile(Model model, @AuthenticationPrincipal OidcUser principal) {
        if (principal == null) {
            // If user is not authenticated, redirect to home
            return "redirect:/";
        }
        
        Map<String, Object> userInfo = principal.getClaims();
        model.addAttribute("userInfo", userInfo);
        model.addAttribute("message", "Your Profile");
        return "profile";
    }

    @GetMapping("/api/user")
    @ResponseBody
    public Map<String, Object> getUserInfo() {
        try {
            return userService.getUserInfo();
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to retrieve user information: " + e.getMessage());
            return errorResponse;
        }
    }
}