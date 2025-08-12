# Employee Management System

## Overview
This Employee Management System is a backend-focused Java application that manages employee and project data using JDBC for database connectivity and SQL for data storage. The system provides CRUD (Create, Read, Update, Delete) operations for employees and projects, with functionality to assign employees to projects and manage these assignments.

## Technologies Used
- **Java** - Core programming language
- **JDBC** - For database connectivity and operations
- **SQL** - For database schema design and queries
- **MySQL** - Database management system

## Core Functionality

### Employee Management
- **Create**: Add new employees to the system with their details
- **Read**: View employee information individually or as a list
- **Update**: Edit employee details such as name, contact, position, etc.
- **Delete**: Remove employees from the system

### Project Management
- **Create**: Add new projects with relevant details
- **Read**: View project information and status
- **Update**: Edit project details, deadlines, or requirements
- **Delete**: Remove projects from the system

### Employee-Project Assignment
- Assign employees to specific projects
- View which employees are assigned to each project
- Remove employees from projects
- Edit employee roles within projects
- Track employee-project relationships

## Technical Implementation
- Java backend with JDBC for database operations
- SQL database for persistent storage
- Data Access Objects (DAOs) for each entity
- Service layer for business logic
- Proper exception handling for database operations

## Database Design
The system implements a relational database model with:
- Employee table for storing employee information
- Project table for maintaining project details
- Junction table for employee-project assignments

## Setup Requirements
- Java JDK 8 or higher
- MySQL 5.7 or higher
- JDBC driver for MySQL
- Database configured according to the provided scripts

## Getting Started
1. Clone the repository
2. Set up the MySQL database
3. Configure the database connection in the properties file
4. Compile and run the application

## Repository Information
- Last Updated: 2025-08-12 18:42:43 UTC
- Created by: Nagaraj0000b
