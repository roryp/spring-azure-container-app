![UML](uml.png)

# Spring Boot Azure Container App with Entra ID

This project demonstrates a Spring Boot application deployed to Azure Container Apps with Azure Entra ID integration for authentication.

## Features

- Spring Boot 3 with Spring Security
- Azure Entra ID integration for authentication
- Docker containerization for Azure Container Apps deployment
- Thymeleaf templates for frontend rendering

## Prerequisites

- Java 21+
- Maven
- Docker
- Azure CLI
- Azure subscription and Entra ID tenant

## Authentication Flow

1. User clicks "Login with Azure Entra ID" 
2. Spring Security redirects to Azure Entra ID
3. User authenticates with Microsoft
4. Azure issues tokens and redirects back to the app
5. Spring Security creates user session
6. User accesses protected resources

## Demo Code Examples

### Security Configuration
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Set public and protected routes
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/index.html", "/error/**").permitAll()
                .anyRequest().authenticated()
            )
            // Azure Entra ID integration
            .oauth2Login(oauth2 -> oauth2
                .defaultSuccessUrl("/profile", true)
            );
        return http.build();
    }
}
```

### User Authentication Controller
```java
@Controller
public class HomeController {
    @GetMapping("/profile")
    public String getProfile(Model model, @AuthenticationPrincipal OidcUser principal) {
        // Extract OIDC claims from authenticated user
        Map<String, Object> userInfo = principal.getClaims();
        model.addAttribute("userInfo", userInfo);
        return "profile";
    }
}
```

### Application Properties
```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          azure:
            client-id: ${AZURE_CLIENT_ID}
            client-secret: ${AZURE_CLIENT_SECRET}
            scope: openid, profile, email
        provider:
          azure:
            issuer-uri: https://login.microsoftonline.com/${AZURE_TENANT_ID}/v2.0
```

## Setup Azure Entra ID

### Automated Setup (Recommended)

#### For Windows:
```powershell
./Create-AzureEntraAppRegistration.ps1 -AppName "spring-azure-container-app"
```

#### For Linux/macOS:
```bash
chmod +x create-azure-entra-app-registration.sh
./create-azure-entra-app-registration.sh --app-name "spring-azure-container-app"
```

### Manual Setup
1. Register a new application in Azure Entra ID
2. Create a client secret
3. Configure API permissions (`User.Read`, `profile`, `email`, `openid`)
4. Enable ID tokens

## Local Development

1. Copy template files and remove `.template` extension:
   ```
   copy application.yml.template application.yml
   copy set-azure-env.template.bat set-azure-env.bat
   ```

2. Edit files with your Azure Entra ID credentials

3. Run the application:
   ```
   set-azure-env.bat
   # OR
   mvn spring-boot:run
   ```

## Troubleshooting Authentication

If you encounter "No reply address is registered" errors:
```
.\fix-redirect-uri.bat
```

## Deployment to Azure

### Using Azure Container App YAML

1. Update `azure-container-app.yaml` with your values

2. Deploy:
   ```
   az containerapp create --resource-group <group> --name spring-azure-container-app --yaml azure-container-app.yaml
   ```

### Using Azure CLI

```bash
# Build and push Docker image
docker build -t <acr-name>.azurecr.io/spring-azure-container-app:latest .
docker push <acr-name>.azurecr.io/spring-azure-container-app:latest

# Deploy to Azure Container Apps
az containerapp create \
  --name spring-azure-container-app \
  --resource-group <group> \
  --environment <environment> \
  --image <acr-name>.azurecr.io/spring-azure-container-app:latest \
  --target-port 8080 \
  --ingress external \
  --env-vars AZURE_CLIENT_ID=<client-id> AZURE_CLIENT_SECRET=<client-secret> AZURE_TENANT_ID=<tenant-id>
```
