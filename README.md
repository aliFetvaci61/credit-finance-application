# Credit finance Application

This project is a credit application. Users can register and log in to the application and take out credits and pay the installments of these credits.

## General Features

- Users can register
- Users can log in.
- Users take out credits
- Users can view the credits they have taken out
- Users can view the installments of credits
- Users can pay the installments of credits

## Technologies Used
- Java 17: A modern, performant, and up-to-date language used in the application.
- Spring Boot: A framework for building Spring-based applications quickly and easily.
- Docker: A containerization platform for quick deployment and running of the application.
- Kafka: A distributed messaging system used for event processing and data streaming.
- Elasticsearch: An open-source search engine used for high-performance search, analytics, and data storage.
- MySQL: A relational database management system used for data storage and management.
- PostgreSQL: A relational database management system used for data storage and management.
- Redis: An in-memory, NoSQL key/value store database management system

## Design Patterns Used
- API Gateway pattern
  - Routing
  - Transformation
  - Security
- Command Query Responsibility Segregation pattern
- Database per service pattern
- Event-Driven Architecture Pattern
- Decomposition pattern
- Security - Sensitive Data Encapsulation

# Software architecture design for Credit Finance Application

![image](https://github.com/user-attachments/assets/d3b971a1-cfd3-4908-ac8d-6bc972e5e0bc)

## Installation
- make build: Builds the Docker image for the application.
- make up: Starts the application in detached mode.
- make down: Stops and removes the containers created by the application.
- make health: Builds the health-check Docker image.


## Contributing

- Fork the project.
- Create a new branch: git checkout -b new-feature
- Make your changes and commit them: git commit -am 'Add new feature'
- Push to the branch: git push origin new-feature
- Create a new Pull Request.


