# Inventory Management System

This project is a ready-to-import **Spring Boot + Thymeleaf + MySQL** inventory system that follows the architecture you requested:

1. Presentation Layer: Spring MVC controllers and HTML pages for login, dashboard, asset registration, assignment, return, reports, and user management.
2. Service Layer: Business rules for asset availability, issue/return flow, condition handling, and audit logging.
3. Data Layer: DAO classes using plain JDBC with MySQL.

## Project Structure

```text
inventory_system/
├── lib/
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── ims/
        │           ├── dao/
        │           ├── main/
        │           ├── model/
        │           ├── service/
        │           ├── ui/
        │           └── util/
        └── resources/
            ├── application.properties
            ├── schema.sql
            ├── static/
            │   └── css/
            └── templates/
```

## Step 1: Start MySQL in XAMPP

1. Open **XAMPP Control Panel**.
2. Start **MySQL**.
3. Open [http://localhost/phpmyadmin](http://localhost/phpmyadmin).

## Step 2: Create the Database

Run this in phpMyAdmin SQL tab:

```sql
CREATE DATABASE inventory_system;
USE inventory_system;
```

You do not need to create the tables manually unless you want to. The project includes `schema.sql`, so Spring Boot will create the tables automatically when the application starts and connects successfully.

## Step 2B: Manual Table Creation (Optional)

If you want to create the tables yourself in phpMyAdmin instead of letting Spring Boot do it, use:

```sql
CREATE TABLE assets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    type VARCHAR(50),
    serial_number VARCHAR(100),
    condition_status VARCHAR(100),
    status VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    department VARCHAR(100)
);

CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    asset_id INT,
    user_id INT,
    issue_date DATETIME,
    return_date DATETIME,
    status VARCHAR(50),
    message TEXT,
    FOREIGN KEY (asset_id) REFERENCES assets(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

The application also creates an extra `audit_logs` table automatically for inventory action history.

## Step 3: Import Into Eclipse

1. Open Eclipse.
2. Go to **File > Import**.
3. Choose **Maven > Existing Maven Projects**.
4. Browse to:
   `C:\Users\ArcadeU\Desktop\invent\inventory_system`
5. Click **Finish**.
6. Wait for Eclipse to download dependencies.
7. If there are red errors, right-click the project and choose **Maven > Update Project**.

## Step 4: Configure MySQL Credentials

Open:

`src/main/resources/application.properties`

By default it uses:

```properties
spring.datasource.username=root
spring.datasource.password=
```

If your MySQL user/password is different, change them there.

## Step 5: Run the Application

1. In Eclipse, open:
   `src/main/java/com/ims/main/MainApp.java`
2. Right-click `MainApp.java`.
3. Choose **Run As > Java Application**.
4. Wait until you see that Tomcat started on port `8080`.
5. Open your browser and go to:
   [http://localhost:8080/login](http://localhost:8080/login)

## Default Login

```text
Username: admin
Password: admin123
```

## Main Features

- Login page with session-based access control
- Dashboard with quick summary cards
- Asset registration, update, delete, and status tracking
- User management page
- Asset assignment with availability validation
- Asset return with status update and audit log entry
- Search/filter reports by date, type, department, and status
- Simple blue-sky and white theme with no gradients

## Notes

- This version uses **Spring Boot web MVC**, which fits your request for Spring Boot and CSS better than Java Swing.
- The requested package names are preserved under `com.ims.*`.
- `lib/` is included only as a placeholder because the MySQL connector is managed by Maven in this Spring Boot version.
- Returned assets stay in `returned` status until an admin updates them back to `available` from the Asset page.

## If Eclipse Does Not Download Dependencies

Install Maven support in Eclipse (`m2e`) or use Spring Tools / Eclipse IDE for Enterprise Java and Web Developers.
