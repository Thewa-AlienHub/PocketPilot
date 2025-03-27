# PocketPilot - Personal Financial Tracking System

## Overview
PocketPilot is a Spring Boot application designed for personal financial tracking. It offers a range of features to help users manage their finances, including tracking income and expenses, managing budgets, generating financial reports, setting goals, and more.

## Features
- **User-Specific Role Authentication**: Secure login with JWT and role-based access control.
    - Admin: Manage all user accounts, oversee transactions, and configure system settings.
    - Regular User: Add, edit, and delete personal transactions, manage budgets, and generate reports.
- **Expense and Income Tracking**: CRUD operations for tracking income and expenses, with categorization and custom tags.
- **Budget Management**: Set monthly or category-specific budgets with alerts for overspending.
- **Financial Reports**: Visualize spending trends and compare income vs. expenses with filters.
- **Notifications and Alerts**: Get notified about unusual spending, bill payments, and upcoming goals.
- **Goals and Savings Tracking**: Track financial goals and progress, with automatic savings allocation.
- **Multi-Currency Support**: Manage finances in multiple currencies with real-time exchange rate updates.
- **Role-Based Dashboard**: Personalized dashboards for Admins and Regular Users.

## Endpoints

### Auth Controllers
- **Sign Up**: `BaseUrl/auth/sign-up`
- **Sign In**: `BaseUrl/auth/sign-in`

### Admin
- **Get All Users**: `BaseUrl/admin/get-users`

### Transaction Controller Endpoints
- **POST `/api/transactions/add`**: 
  -    Add a new transaction
      - **Request Body**: (Contains transaction details)
- **GET `/api/transactions`**: 
  - Fetch transactions with optional filtering
      - **Request Parameters**: (Filters for transactions)
- **GET `/api/transactions/get/{id}`**: 
  - Fetch a transaction by its ID
      - **Path Variable**: `id` (The ID of the transaction, of type `ObjectId`)
- **PUT `/api/transactions/update/{id}`**: 
  - Update a transaction by its ID
      - **Path Variable**: `id` (The ID of the transaction, of type `ObjectId`)
      - **Request Body**: `TransactionRequestDTO` (Updated transaction details)
- **DELETE `/api/transactions/delete/{id}`**: 
  - Delete a transaction by its ID
      - **Path Variable**: `id` (The ID of the transaction, of type `ObjectId`)
### User Controller Endpoint
- ** GET `/users/profile` ** : 
  - Fetch the user's profile (Authenticated users only)
### Dashboard Controller Endpoint
- **GET `/api/transactions/get/user-transactions`** 
  - Fetch transactions related to the currently logged-in user


### Budget Controller Endpoints
- **POST `/api/budgets/set`** 
  - Set a new budget plan
      - **Request Body**: `BudgetRequestDTO` (Contains budget details)
- **GET `/api/budgets`** 
  - Fetch budget plans with optional filtering
      - **Request Parameters**: `BudgetFilterDTO` (Filters for budgets)


### Financial Report Controller Endpoints
- **GET `/api/financial-reports/spending-trends`**: 
  - Fetch spending trends based on the provided filters (Authenticated users only)
      - **Request Parameters**: (Filter criteria for spending trends)
- **GET `/api/financial-reports/income-vs-expense`**: 
  - Fetch a report comparing income vs. expenses (Authenticated users only)
      - **Request Parameters**:  (Filter criteria for income vs. expense report)
- **GET `/api/financial-reports/filtered-transactions`**: 
  - Fetch transactions based on the provided filters (Authenticated users only)
      - **Request Parameters**:  (Filter criteria for transactions)
### Financial Goal Controller Endpoints
- **POST `/api/financial-goals/add`**: 
  - Add a new financial goal
      - **Request Body**:(Contains goal details)
- **GET `/api/financial-goals/progress/{goalId}`**: 
  - Fetch progress of a financial goal
      - **Path Variable**: `goalId` (The ID of the financial goal, of type `ObjectId`)
### Configs Load Controller Endpoints (FOR Frontend to Load Configs Async)
- **GET `/config/roles`** → Fetch all user roles
- **GET `/config/categories`** → Fetch all transaction categories
## Security
- JWT Authentication with Spring Security
- Environment variables are externalized for sensitive configurations

## Database Design
- MongoDB as the database
- Entity classes used for better structure, maintaining foreign keys where necessary

## Architecture
- MVC Architecture
    - Separated Model classes, DTO classes, and Enum classes for better organization and maintainability

## Error Handling
- Proper exception handling and logging implemented

## Testing
- **Unit Testing**: JUnit and Mockito
- **Integration Testing ** : JUnit and Mockito
- **Security Testing**: OWASP ZAP
- **Performance Testing**: JMeter

## Technologies Used
- Spring Boot
- MongoDB
- JWT Authentication
- Spring Security
- JUnit, Mockito, OWASP ZAP, JMeter

## Setup
1. Clone the repository
2. Set up your environment variables
3. Run the application using `mvn spring-boot:run`
4. Access the application at `http://localhost:8080`

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
