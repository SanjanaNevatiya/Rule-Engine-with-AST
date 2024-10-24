# Rule-Engine-with-AST

## Overview

The Rule Engine is a flexible and scalable application designed to create, evaluate, and combine rules for determining user eligibility based on various attributes. Built with Spring Boot, it provides a RESTful API that allows users to manage rules effectively.

## Features

# 1.Create Rules

Endpoint: /rules/create

Method: POST

Description: Users can create new rules by providing necessary attributes and conditions.

Request Body: JSON object with rule details (e.g., name, conditions). Response: Confirmation message with the created rule ID.

# 2.Evaluate Rules

Endpoint: /rules/evaluate

Method: POST

Description: This feature evaluates the provided rules against given user attributes to determine eligibility.

Request Body: JSON object with user attributes to evaluate against the existing rules.

Response: Indicates whether the user is eligible and lists matched rules.

# 3.Combine Rules

Endpoint: /rules/combine

Method: POST

Description: Users can combine multiple rules to create complex conditions for evaluation.

Request Body: JSON object with the rules to be combined and the logic for combination (e.g., AND, OR).

Response: Confirmation message with the combined rule ID.

# 4.Additional Features

Dynamic Rule Management: Modify existing rules without downtime.

Custom Logic Support: Implement complex logic using combinations of rules.

Logging: Comprehensive logging for actions taken within the application, aiding in debugging and auditing.

Exception Handling: The application includes a robust exception handling mechanism to gracefully manage errors and provide meaningful messages to users. This ensures a smoother user experience and better debugging.

# 5.Technologies Used

Java 21

Spring Boot 3.3.4

Spring Security

Maven

MySQL Database (for development)

Lombok (for reducing boilerplate code)

# 6.Prerequisites

Java 17

Maven

MySQL

## Setup and Installation

# Clone the repository:

git clone <repository-url>
cd rule-engine

Build the project:
mvn clean install

Run the application:
mvn spring-boot:run

Access the API: The application will be running at http://localhost:8080. You can use tools like Postman or cURL to test the endpoints.

## API Documentation

Create a Rule Request: http

POST /rules/create

Content-Type: application/json

{

    {
    "ruleString": "age > 30 AND salary > 50000"

}

}

Response: json

{

    "message": "Rule created successfully.",

    "ruleId": 1

}

Evaluate Rules Request: http

POST /rules/evaluate

Content-Type: application/json

{

    {
    "age": 35,
    "salary": 60000

}

}

Response: json

{

    "eligible": true,

    "matchedRules": ["AgeRule"]

}

Combine Rules Request: http

POST /rules/combine

Content-Type: application/json

{

    "rules": [1, 2],  // rule IDs

    "logic": "AND"

}

Response: json

{

    "message": "Rules combined successfully.",

    "combinedRuleId": 3

}

Logging The application maintains logs for each action performed, which can be useful for auditing purposes. Log files can be found in the logs directory.

Contributing Fork the repository.

Create a new branch (git checkout -b feature-branch).

Make your changes and commit them (git commit -m 'Add new feature').

Push to the branch (git push origin feature-branch).

Create a new Pull Request.
