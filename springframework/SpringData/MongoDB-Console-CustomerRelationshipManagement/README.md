# Customer relationship management
- This project is a Customer Relationship Management (CRM) application built using Spring Boot and MongoDB. 
- It allows users to perform CRUD (Create, Read, Update, Delete) operations on customer data via a command-line interface.

## Features
- Create a new customer
- Retrieve all customers
- Update an existing customer
- Delete a customer
- Fetch customers by specific criteria (e.g., bill amount range, address)
## Setup MongoDB
- Create a [docker-compose](docker-compose.yml) file
- run docker compose: `docker-compose up -d`
- Verify MySQL Docker container is running: `docker ps`
- [Download MongoDB shell](https://www.mongodb.com/try/download/shell)
- Open MongoDB shell
  ```bash
  Please enter a MongoDB connection string (Default: mongodb://localhost/): mongodb://root:root123@localhost:27017/admin
  ```
- **Configure** `application.properties` or `application.yml`:
  ```properties
  spring.data.mongodb.host=localhost
  spring.data.mongodb.port=27017
  spring.data.mongodb.database=mydatabase
  spring.data.mongodb.username=root
  spring.data.mongodb.password=root123
  spring.data.mongodb.authentication-database=admin
  ```
## How to run
- Build 
```bash
mvn clean install

mvn clean install -DskipTests
```
- Run
```bash
mvn spring-boot:run
```

- CRUD operations
  - **Create** a Customer
    - Select option 1 from the menu.
    - Enter the following details when prompted:
      ```bash
      Customer ID
      Customer Name
      Customer Address
      Bill Amount
      Mobile Number
      ```
  - **Retrieve** All Customers
    - Select option 2 from the menu.
    - The application will display all customer records.
    - Content of mydatabase  
      ```
      use mydatabase

      show collections ->customer

      db.mycollection.find().pretty()
      ```
    - Database table
      - The collection for storing customer data is defined by the `@Document` annotation in the `Customer` class
        ```java
        @Document
        public class Customer {
            // Fields and methods...
        }
        ```
      - When the application saves a Customer object using the `ICustomerRepo` repository, MongoDB will automatically create the `Customer` collection in the `mydatabase` database if it does not already exist.
  - **Update** a Customer
    - Select option 3 from the menu.
    - The application will update the customer with the specified ID. 
    - Modify the `updateDocument` method in `MongoDbTestRunner` if needed.
  - **Delete** a Customer
    - Select option 4 from the menu.
    - The application will delete the customer with the specified ID. 
    - Modify the `deleteDocument` method in `MongoDbTestRunner` if needed.
  - Exit the Application
    - Select option 5 from the menu to exit the application.
  - Fetch customers by bill amount range
    - Modify and call the `findCustomersByBillRange` method in `MongoDbTestRunner`.
  - Fetch customers by address whose mobile number is not null
    - Modify and call the `findCustomersByAddressWhoseMobileNoIsNotNull` method in `MongoDbTestRunner`.
## MongoDbTestRunner
- The `MongoDbTestRunner.java` class is currently designed as a `command-line interface` (CLI) for interacting with the `Customer` data.
- It allows users to perform CRUD operations (Create, Read, Update, Delete) on the Customer collection via the console.
