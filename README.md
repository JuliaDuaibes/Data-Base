# Restaurant Management System

JavaFX + MySQL desktop application for managing restaurant customers, menu items, orders, employees, managers, and salary records.

![Restaurant Preview](HomePage.jpg)

## Overview

This project is a modular JavaFX application that uses MySQL for persistence and is configured for VS Code and Eclipse-style project metadata.

### Main modules

- Dashboard
- Customer management
- Menu management
- Order management
- Employee management
- Manager view
- Monthly salary tracking

## Tech Stack

- Java: JDK 25
- JavaFX: SDK 26.0.1
- Database: MySQL
- Local server stack: XAMPP / phpMyAdmin
- JDBC driver: MySQL Connector/J 9.7.0

## Project Structure

```text
dataBaseProj/
|-- .vscode/
|   |-- launch.json
|   `-- settings.json
|-- bin/
|-- lib/
|   `-- mysql-connector-j.jar/
|       `-- mysql-connector-j-9.7.0.jar
|-- src/
|   |-- application/
|   `-- module-info.java
|-- .classpath
|-- database_setup.sql
`-- README.md
```

## Required Local Paths

This repository is currently configured to use:

- JDK: `C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot`
- JavaFX SDK: `D:\javafx-sdk-26.0.1\lib`

If your local setup uses a different JavaFX location, update these files before running:

- `.vscode/launch.json`
- `.vscode/settings.json`
- `.classpath`

## Database Setup

The app is configured for this MySQL connection:

- Database: `FinalPhase`
- Username: `root`
- Password: `root1234`

The SQL file already exists in the project root:

- [`database_setup.sql`](database_setup.sql)

### Import with phpMyAdmin / XAMPP

1. Start `Apache` and `MySQL` from XAMPP.
2. Open `http://localhost/phpmyadmin`.
3. Click `Import`.
4. Choose [`database_setup.sql`](database_setup.sql).
5. Click `Go`.

This will create the `FinalPhase` database and import the required tables and sample data.

## VS Code Configuration

The workspace is set up with:

- One Java launch configuration only
- Source folder: `src`
- Output folder: `bin`
- JavaFX jars from `D:\javafx-sdk-26.0.1\lib`
- MySQL connector jar from `lib\mysql-connector-j.jar\mysql-connector-j-9.7.0.jar`

### Launch Configuration To Run

Use this VS Code launch configuration:

- `Launch RestaurantManagementSystem`

It runs this main class:

```text
dataBaseProj/application.RestaurantManagementSystem
```

## How To Run

1. Install JDK 25 and make sure `C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot` exists.
2. Download JavaFX SDK 26.0.1 and place it at `D:\javafx-sdk-26.0.1`.
3. Open the project folder in VS Code.
4. Install the VS Code `Extension Pack for Java`.
5. Start `Apache` and `MySQL` in XAMPP.
6. Import [`database_setup.sql`](database_setup.sql) in phpMyAdmin.
7. Open the Run and Debug panel in VS Code.
8. Run `Launch RestaurantManagementSystem`.

## How to Use the Application

This section is for new users who want a simple way to get started.

### 1. Start the app

Before opening the application, make sure `MySQL` is running in `XAMPP`.
If the database is not running, features that load or save data will not work correctly.

To start the app:

1. Open the project in VS Code.
2. Start `Apache` and `MySQL` in XAMPP.
3. Run the `Launch RestaurantManagementSystem` configuration.
4. Wait for the main window to open.

### 2. Use the sidebar buttons

The main window includes a left sidebar for navigation.

- `Home`: Returns to the main welcome screen.
- `Customer`: Opens the customer module to manage customer details.
- `Order`: Opens the order module to create and manage restaurant orders.
- `Menu`: Opens the menu module to add, edit, search, and organize menu items.
- `Branch`: Used for branch-related information and branch management.
- `Salary`: Opens the salary module to view and manage monthly salary records.
- `Employee`: Used for employee-related records and staff management.
- `Manager`: Used for manager-related records and management information.
- `Exit`: Closes the application.

### 3. What each module is for

- `Home`: Main landing page of the system.
- `Customer`: Store customer names, phone numbers, payment methods, and dining preferences.
- `Order`: Create orders by selecting menu items, customers, quantities, and branch information.
- `Menu`: Manage food and drink items, categories, prices, and item images.
- `Branch`: Keep track of restaurant branches and branch-related data.
- `Salary`: View, add, update, and delete monthly salary records for branches.
- `Employee`: Manage employee information and staff-related records.
- `Manager`: Manage manager information and manager-related records.

### 4. Important database note

`MySQL` in `XAMPP` must be running before you use any feature that reads from or writes to the database.

This includes actions such as:

- loading customers
- loading menu items
- creating orders
- saving salary records
- searching stored data

### 5. Basic workflow for a new user

A simple way to use the system is:

1. Open the `Menu` module and add menu items first.
2. Open the `Customer` module and add customer records.
3. Open the `Order` module and create orders using the existing menu items and customers.
4. Use the `Branch` module to manage branch-related data.
5. Open the `Salary` module to view or manage branch salary records.

This order helps because orders usually depend on existing menu and customer data.

## Screenshots

The repository currently does not include real UI screenshots for each module.
To avoid showing icons as if they were application screens, this README uses only expressive project images that actually exist in the project root.

### Restaurant Preview

![Restaurant Preview](HomePage.jpg)

### Menu Preview

![Menu Preview](resurant.jpeg)

## Troubleshooting

### JavaFX errors

If VS Code cannot find JavaFX modules:

- Confirm `D:\javafx-sdk-26.0.1\lib` exists.
- Confirm `.vscode/launch.json`, `.vscode/settings.json`, and `.classpath` all point to that folder.

### MySQL connection errors

If the app cannot connect:

- Confirm XAMPP MySQL is running.
- Confirm the `FinalPhase` database was imported.
- Confirm the local MySQL username is `root`.
- Confirm the local MySQL password is `root1234`.

## Entry Point

```text
dataBaseProj/application.RestaurantManagementSystem
```
