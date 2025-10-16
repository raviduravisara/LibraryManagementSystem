# Railway Deployment Guide
# Library Management System Backend

## Environment Variables Required:
- MONGODB_URI: Your MongoDB Atlas connection string
- PORT: Railway will set this automatically
- CORS_ORIGINS: Your Vercel frontend URL (set after frontend deployment)

## Build Process:
1. Railway will use the Dockerfile to build the Spring Boot application
2. Maven will compile and package the application
3. Application will run on the PORT provided by Railway

## Database Setup:
1. Create a MongoDB Atlas cluster
2. Get the connection string
3. Add it as MONGODB_URI environment variable in Railway

## Deployment Steps:
1. Connect your GitHub repository to Railway
2. Select the backend folder as root directory
3. Railway will automatically detect the Dockerfile
4. Set environment variables
5. Deploy!
