package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for retrieving user information from Microsoft Graph API
 * Uses the configured RestTemplate and OAuth2AuthorizedClientManager
 */
@Service
public class UserService {

    private final RestTemplate restTemplate;
    private final OAuth2AuthorizedClientManager clientManager;

    @Autowired
    public UserService(RestTemplate restTemplate, OAuth2AuthorizedClientManager clientManager) {
        this.restTemplate = restTemplate;
        this.clientManager = clientManager;
    }

    /**
     * Retrieves authenticated user information from Microsoft Graph API
     * Uses OAuth2AuthorizedClientManager to obtain access tokens for the API call
     * @return Map containing user profile information or authentication status
     */
    public Map<String, Object> getUserInfo() {
        // Get current authentication from security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            // Create authorization request with the current user's client registration
            OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId(oauthToken.getAuthorizedClientRegistrationId())
                .principal(oauthToken)
                .build();
            // Get authorized client with valid access token
            OAuth2AuthorizedClient client = clientManager.authorize(authorizeRequest);
            
            if (client != null) {
                // Extract access token and create authenticated request headers
                String accessToken = client.getAccessToken().getTokenValue();
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(accessToken);
                HttpEntity<String> entity = new HttpEntity<>("", headers);
                
                try {
                    // Call Microsoft Graph API to get user info
                    return restTemplate.exchange(
                        "https://graph.microsoft.com/v1.0/me", 
                        HttpMethod.GET, 
                        entity, 
                        Map.class).getBody();
                } catch (Exception e) {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Failed to retrieve user information: " + e.getMessage());
                    return errorResponse;
                }
            }
        }
        
        // Fallback for non-OAuth authentication or unauthenticated users
        Map<String, Object> response = new HashMap<>();
        response.put("name", authentication != null ? authentication.getName() : "Anonymous");
        response.put("authenticated", authentication != null && authentication.isAuthenticated());
        
        return response;
    }
}