# E-Wallet api

- [User Guide](#user-guide)
- [Design Choices](#design-choices)
- [Areas for Improvement](#areas-for-improvement)

## User Guide

### Wallet Endpoints
#### POST /wallet

Description: Creates a new wallet for a user. 
\
Request Body:
{
"userId": userId (long)
}

#### GET /wallet/all 
Description: Retrieves all wallets belonging to a user.
\
Request Body: {
"userId": (long)
"userName": (String),
"userPassword": (String)
}

#### GET /wallet

Description: Retrieves a wallet by its ID.
\
Request Body: Request Body: {
"userId": (long)
"userName": (String),
"userPassword": (String)
}

#### DELETE /wallet

Description: Deletes a wallet.
\
Request Body: {
"walletId": (long)
}

### User Endpoints

#### POST /user

Description: Creates a user.
\
Request Body: {
"name": (String),
"password": (String)
}

#### GET /user/{userId}

Description: Creates a user.
\
Request Body: { }

#### PUT /user

Description: Updates a user.
\
Request Body: {
"userId": (long),
"name": (String)
}

#### PUT /user/reset

Description: Resets status for a blocked user.
\
Request Body: {
"userId": (long),
"name": (String),
"password": (String)
}

#### DELETE /user/{userId}

Description: Deletes a user.
\
Request Body: { }

### Transaction Endpoints

#### POST /transaction/deposit

Description: Deposits funds.
\
Request Body: { 
"walletId": (long),
"userId": (long),
"userName": (String),
"userPassword": (String),
"amount": (double)
}

#### POST /transaction/withdraw

Description: Withdraws funds.
\
Request Body: {
"walletId": (long),
"userId": (long),
"userName": (String),
"userPassword": (String),
"amount": (double)
}
## Design Choices

### Controller-Service-Entity Architecture:

The application follows the Controller-Service-Repository architecture pattern.
Controllers handle incoming requests, validate input, and delegate business logic to the service layer.
Services contain the business logic and perform operations on the entities.
The Repository layer, at the bottom of this picture, is responsible for storing and retrieving some set of data.

### Relational Database:

The application uses a relational database to store data.
This choice allows for structured data storage, efficient querying, and the ability to enforce data integrity through relationships and constraints.

### Spring Boot Framework:

The application is built using the Spring Boot framework.
Spring Boot provides a comprehensive set of tools and libraries for developing scalable Java applications.
It simplifies the configuration and setup process, promotes convention over configuration, and offers built-in support for RESTful APIs.

## Areas for Improvement

### Authentication and Authorization:

Implement a secure authentication mechanism (such as JWT or OAuth2) to protect sensitive endpoints and ensure only authorized users can access them.
Define roles and permissions to restrict access to certain operations based on user privileges.

### Input Validation and Error Handling:

Enhance input validation by implementing robust validation logic and handling edge cases.
Implement consistent error handling and return meaningful error responses to the API clients.

### Unit and Integration Testing:

Increase test coverage by writing more unit tests for individual components (controllers, services, and repositories) to ensure their correctness.
Write integration tests to validate the interactions between different components and ensure the system functions as expected as a whole.

### API Documentation:

Generate comprehensive API documentation using tools like Swagger to provide clear documentation and examples for API consumers.

### Performance Optimization:

Analyze and optimize database queries to improve the application's performance.
Implement caching mechanisms for frequently accessed data to reduce response times and improve scalability.

### Logging and Monitoring:

Implement a logging framework (such as Log4j or SLF4J) to capture important events and errors during runtime.

