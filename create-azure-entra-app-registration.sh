#!/bin/bash
# create-azure-entra-app-registration.sh
# Script to automate the creation of Azure Entra ID app registration for Spring Boot application

# Default values
APP_NAME="spring-azure-container-app"
REDIRECT_URI_LOCAL="http://localhost:8080/login/oauth2/code/azure"
REDIRECT_URI_PRODUCTION=""
CLIENT_SECRET_DESCRIPTION="Created by automation script"
CLIENT_SECRET_DURATION_YEARS=1

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    key="$1"
    case $key in
        --app-name)
        APP_NAME="$2"
        shift 2
        ;;
        --redirect-uri-local)
        REDIRECT_URI_LOCAL="$2"
        shift 2
        ;;
        --redirect-uri-production)
        REDIRECT_URI_PRODUCTION="$2"
        shift 2
        ;;
        --client-secret-description)
        CLIENT_SECRET_DESCRIPTION="$2"
        shift 2
        ;;
        --client-secret-duration)
        CLIENT_SECRET_DURATION_YEARS="$2"
        shift 2
        ;;
        *)
        echo "Unknown option: $1"
        exit 1
        ;;
    esac
done

# Check if Azure CLI is installed
if ! command -v az &> /dev/null; then
    echo "Azure CLI is not installed. Please install it from https://docs.microsoft.com/en-us/cli/azure/install-azure-cli"
    exit 1
fi

# Check if logged in to Azure
if ! az account show &> /dev/null; then
    echo "You need to login to Azure first..."
    az login
fi

# Get current tenant ID
TENANT_ID=$(az account show --query tenantId -o tsv)
echo "Using tenant ID: $TENANT_ID"

# Create app registration
echo "Creating app registration '$APP_NAME'..."
APP_JSON=$(az ad app create --display-name "$APP_NAME")
APP_ID=$(echo $APP_JSON | jq -r '.appId')

echo "App registration created with client ID: $APP_ID"

# Add redirect URIs
echo "Adding redirect URIs..."
REDIRECT_URIS="[\"$REDIRECT_URI_LOCAL\"]"
if [[ -n "$REDIRECT_URI_PRODUCTION" ]]; then
    REDIRECT_URIS=$(echo $REDIRECT_URIS | jq ". + [\"$REDIRECT_URI_PRODUCTION\"]")
fi

az ad app update --id "$APP_ID" --web-redirect-uris "$REDIRECT_URIS"

# Enable ID token issuance
echo "Enabling ID token issuance..."
az ad app update --id "$APP_ID" --set "web.implicitGrantSettings.enableIdTokenIssuance=true"

# Add API permissions for Microsoft Graph
echo "Adding API permissions..."
# User.Read permission
az ad app permission add --id "$APP_ID" --api 00000003-0000-0000-c000-000000000000 --api-permissions e1fe6dd8-ba31-4d61-89e7-88639da4683d=Scope

# Grant admin consent for API permissions
echo "Granting admin consent for API permissions..."
az ad app permission admin-consent --id "$APP_ID"

# Create client secret
echo "Creating client secret..."
END_DATE=$(date -j -v +${CLIENT_SECRET_DURATION_YEARS}y "+%Y-%m-%d" 2>/dev/null || date -d "+${CLIENT_SECRET_DURATION_YEARS} years" "+%Y-%m-%d")
CLIENT_SECRET_JSON=$(az ad app credential reset --id "$APP_ID" --display-name "$CLIENT_SECRET_DESCRIPTION" --end-date "$END_DATE")
CLIENT_SECRET=$(echo $CLIENT_SECRET_JSON | jq -r '.password')

# Output the details needed for the application
echo ""
echo "===== Application Registration Complete ====="
echo "Application Name:   $APP_NAME"
echo "Client ID:          $APP_ID"
echo "Client Secret:      $CLIENT_SECRET"
echo "Tenant ID:          $TENANT_ID"
echo "Redirect URIs:      $REDIRECT_URIS"
echo "================================================"
echo ""
echo "To configure your Spring Boot application, set these environment variables:"
echo "AZURE_CLIENT_ID=$APP_ID"
echo "AZURE_CLIENT_SECRET=$CLIENT_SECRET"
echo "AZURE_TENANT_ID=$TENANT_ID"
echo ""
echo "These credentials will expire on $END_DATE"