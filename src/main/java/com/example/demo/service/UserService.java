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

@Service
public class UserService {

    private final RestTemplate restTemplate;
    private final OAuth2AuthorizedClientManager clientManager;

    @Autowired
    public UserService(RestTemplate restTemplate, OAuth2AuthorizedClientManager clientManager) {
        this.restTemplate = restTemplate;
        this.clientManager = clientManager;
    }

    public Map<String, Object> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId(oauthToken.getAuthorizedClientRegistrationId())
                .principal(oauthToken)
                .build();
            OAuth2AuthorizedClient client = clientManager.authorize(authorizeRequest);
            
            if (client != null) {
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
        
        Map<String, Object> response = new HashMap<>();
        response.put("name", authentication != null ? authentication.getName() : "Anonymous");
        response.put("authenticated", authentication != null && authentication.isAuthenticated());
        
        return response;
    }
}