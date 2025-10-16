# Vercel Deployment Guide
# Library Management System Frontend

## Environment Variables Required:
- VITE_API_BASE_URL: Your Railway backend URL (set after backend deployment)

## Build Process:
1. Vercel will run `npm run build`
2. Static files will be generated in `dist` folder
3. Files will be served from Vercel's CDN

## Deployment Steps:
1. Connect your GitHub repository to Vercel
2. Select the frontend folder as root directory
3. Vercel will auto-detect Vite configuration
4. Set environment variables
5. Deploy!

## Post-Deployment:
1. Update CORS_ORIGINS in Railway with your Vercel URL
2. Test the connection between frontend and backend
