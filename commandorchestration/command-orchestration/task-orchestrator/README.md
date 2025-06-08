# Task Service

- Generate the protos:
```
mvn clean compile
```
- Build the project:
```bash
mvn clean install
```
- Run the application:
```bash
mvn spring-boot:run
```

## Test
- Get Access token `http://localhost:8080/realms/master/protocol/openid-connect/token`
  - Modify `Body -> x-wwww-form-url-encoded`
     ```
     grant_type : password
     client_id  : sensor-service (created in Keycloak)
     username  : endpointaccessuser
     password  : password123
     ```

- Request **`POST`** `http://localhost:9081/tasks`
- with request body as JSON payload
  ```json
  {
    "taskId": "task-003",
    "commandType": "RESTART",
    "commandArgs": ["arg1", "arg2"],
    "sensorList": ["sensor1", "sensor2"]
  }
  ```
- `registration service` creates a document in MongoDB
  - Verify at **MongoDB**
  - Open MongoDB shell
    - Connect: `Please enter a MongoDB connection string (Default: mongodb://localhost/): mongodb://root:root123@localhost:27017/admin`
    - Check available Databses: `show dbs`
    - Swicth to DB: `use taskregistration`
    - Query documents in the collections: `db.tasks.find().pretty()`
      - Find a sensor: `db.tasks.findOne({ commandType: "START" })`    
    - Delete documents from the collections: `db.tasks.deleteMany({})`
      - Delete a sensor: `db.tasks.deleteOne({ commandType: "START" })`
