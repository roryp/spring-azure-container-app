Step-by-step guide for demonstrating the code in this Spring Boot Azure Container App with Entra ID integration:

## Step-by-Step Code Demo

1. **Start with the Application Structure**
   - Show the main Spring Boot application class

2. **Security Configuration**
   - Let's look at how the Azure Entra ID authentication is configured.

3. **Controllers and UI Flow**
   - The HomeController handles the web pages and user interactions.
   - Highlight the changes made to display the logged-in user's name on the home page using Thymeleaf.

4. **User Service**
   - The UserService extracts user information from authentication.

5. **Templates**
   - The application uses Thymeleaf templates for the UI.
   - Show the updated `index.html` file where the logged-in user's name is displayed dynamically.

6. **Application Configuration**
   - The application.yml file contains settings for Azure Entra ID integration.

7. **Docker Setup**
   - The Dockerfile shows how the application is containerized.

8. **Deployment Configuration**
   - Azure Container App YAML defines the cloud deployment.

9. **Authentication Flow Demo**
   - Start the application locally.
   - Navigate to the home page.
   - Click "Login" to trigger Azure Entra ID authentication.
   - After login, show the user profile with information from Azure Entra ID.
   - Demonstrate the logged-in user's name displayed on the home page.
   - Demonstrate logging out.

10. **Docker Execution**
    - Build and run with Docker showing how environment variables pass Azure credentials.
    - Access the containerized application to show identical behavior.

11. **Azure Deployment (if already deployed)**
    - Show the application running in Azure Container Apps.
    - Highlight the same authentication flow in the cloud environment.