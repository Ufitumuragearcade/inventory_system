# Deployment Guide

This project is prepared for deployment with Docker on platforms such as Railway.

## Recommended Platform

Use **Railway** because it can host the Spring Boot app and a MySQL database together more easily than most beginner-friendly platforms.

## Environment Variables

Set these in your hosting dashboard:

```text
DB_URL=jdbc:mysql://<host>:<port>/<database>?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
DB_USERNAME=<database_username>
DB_PASSWORD=<database_password>
PORT=8080
```

If the platform gives you `MYSQLHOST`, `MYSQLPORT`, `MYSQLDATABASE`, `MYSQLUSER`, and `MYSQLPASSWORD`, this project can also use those automatically.

## Railway Deployment Steps

1. Push this folder to a GitHub repository.
2. Create a new Railway project.
3. Add a MySQL service.
4. Add a service from your GitHub repository.
5. Railway will detect the `Dockerfile` and build the app.
6. Set the required environment variables if they are not filled automatically.
7. Open the generated public domain from Railway.

## Health Check

The application starts on the port provided by `PORT` and serves the login page at `/login`.
