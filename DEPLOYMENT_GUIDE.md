# 🚀 Complete Deployment Guide
# Library Management System

## 📋 Prerequisites
- GitHub repository with your code
- ✅ MongoDB Atlas account (you already have this!)
- Railway account
- Vercel account

## 🔄 Deployment Order: BACKEND → FRONTEND

### Step 1: Backend Deployment (Railway)

#### 1.1 MongoDB Atlas (Already Set Up! ✅)
Your MongoDB Atlas is already configured:
- **Database**: `LibraryDatabase`
- **Connection String**: `mongodb+srv://malika:Malika12345@cluster0.pu2hdjk.mongodb.net/`
- **Status**: Ready to use! 🎉

#### 1.2 Deploy to Railway
1. Go to [Railway](https://railway.app)
2. Login with GitHub
3. Click "New Project" → "Deploy from GitHub repo"
4. Select your repository
5. Set root directory to `backend`
6. Railway will auto-detect Dockerfile

#### 1.3 Configure Environment Variables
In Railway dashboard, add these environment variables:
```
MONGODB_URI=mongodb+srv://malika:Malika12345@cluster0.pu2hdjk.mongodb.net/
MONGODB_DATABASE=LibraryDatabase
SPRING_PROFILES_ACTIVE=prod
CORS_ORIGINS=https://your-frontend-url.vercel.app
ADMIN_USERNAME=admin
ADMIN_PASSWORD=admin123
EMAIL_USERNAME=ktharanga128@gmail.com
EMAIL_PASSWORD=cadz sweb uery flbv
```

#### 1.4 Deploy Backend
- Railway will build and deploy automatically
- Note down your Railway URL (e.g., `https://your-app.railway.app`)

### Step 2: Frontend Deployment (Vercel)

#### 2.1 Deploy to Vercel
1. Go to [Vercel](https://vercel.com)
2. Login with GitHub
3. Click "New Project" → Import Git Repository
4. Select your repository
5. Set root directory to `frontend`
6. Vercel will auto-detect Vite

#### 2.2 Configure Environment Variables
In Vercel dashboard, add:
```
VITE_API_BASE_URL=https://your-railway-backend-url.railway.app
```

#### 2.3 Deploy Frontend
- Vercel will build and deploy automatically
- Note down your Vercel URL (e.g., `https://your-app.vercel.app`)

### Step 3: Connect Frontend & Backend

#### 3.1 Update CORS in Railway
1. Go back to Railway dashboard
2. Update `CORS_ORIGINS` environment variable:
```
CORS_ORIGINS=https://your-vercel-frontend-url.vercel.app
```
3. Redeploy backend

#### 3.2 Test Connection
1. Visit your Vercel frontend URL
2. Try logging in or accessing features
3. Check browser console for any CORS errors

## ✅ Post-Deployment Checklist

- [ ] Backend deployed on Railway
- [ ] Frontend deployed on Vercel
- [ ] MongoDB Atlas connected
- [ ] Environment variables set
- [ ] CORS configured
- [ ] Frontend can communicate with backend
- [ ] All features working

## 🔧 Troubleshooting

### Common Issues:
1. **CORS Errors**: Update CORS_ORIGINS in Railway
2. **API Not Found**: Check VITE_API_BASE_URL in Vercel
3. **Database Connection**: Verify MONGODB_URI in Railway
4. **Build Failures**: Check logs in respective platforms

### Debug Steps:
1. Check Railway logs for backend errors
2. Check Vercel logs for frontend build errors
3. Test API endpoints directly
4. Check browser network tab for failed requests

## 🎉 Success!
Your Library Management System is now live and accessible worldwide!
