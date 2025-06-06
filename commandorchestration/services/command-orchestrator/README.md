# Command orchestrator 
- Copy [protos](src/main/proto/TaskMessage.proto) from Task creator service to this service
  - The java_package = "com.example.taskservice"; and java_outer_classname = "TaskMessageProto";
    - This means when the protobuf-maven-plugin (which you've already added to your pom.xml) compiles this .proto file in your new command-orchestration-service project, the generated Java classes (like TaskMessage and CommandType) will be placed under the package com.example.taskservice, and the main outer class will be TaskMessageProto.
    - Therefore, when you write code in your new service to consume and deserialize these messages, your import statements for the generated Protobuf classes will look like this:
      ```
      import com.example.taskservice.TaskMessageProto;
      // You'll then refer to messages as TaskMessageProto.TaskMessage etc.
      ```
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
- `task creator` creates a document in MongoDB
  - Verify at **MongoDB**
  - Open MongoDB shell
    - Connect: `Please enter a MongoDB connection string (Default: mongodb://localhost/): mongodb://root:root123@localhost:27017/admin`
    - Check available Databses: `show dbs`
    - Swicth to DB: `use taskregistration`
    - Query documents in the collections: `db.tasks.find().pretty()`
      - Find a sensor: `db.tasks.findOne({ commandType: "START" })`    
    - Delete documents from the collections: `db.tasks.deleteMany({})`
      - Delete a sensor: `db.tasks.deleteOne({ commandType: "START" })`
- `command orchestrator` creates a document in MongoDB
  - Verify at **MongoDB**
  - Open MongoDB shell
    - Connect: `Please enter a MongoDB connection string (Default: mongodb://localhost/): mongodb://root:root123@localhost:27017/admin`
    - Check available Databses: `show dbs`
    - Swicth to DB: `use commandorchestrationdb`
    - Query documents in the collections: `db.commands.find().pretty()`
      - Find a sensor: `db.commands.findOne({ commandType: "START" })`    
    - Delete documents from the collections: `db.commands.deleteMany({})`
      - Delete a sensor: `db.commands.deleteOne({ commandType: "START" })`
