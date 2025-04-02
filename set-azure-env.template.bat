@echo off
REM Template file for setting Azure environment variables
REM Copy this file to set-azure-env.bat and replace the placeholder values with your actual credentials

SET AZURE_CLIENT_ID=your-client-id-here
SET AZURE_CLIENT_SECRET=your-client-secret-here
SET AZURE_TENANT_ID=your-tenant-id-here

REM Database configuration (if needed)
SET DATABASE_URL=jdbc:mysql://localhost:3306/your_database
SET DATABASE_USERNAME=your_username
SET DATABASE_PASSWORD=your_password

echo Azure environment variables have been set.