# Database Schema
## Document DB based
- Each document represents the latest status of a single command sent to a device:
```json
{
"command_type": "SET_TOU",
"command_params": {
    "tou_profile_id": "TOU-2025-TEST"
},
"device_ids": [
    "meter-001",
    "meter-002",
    "meter-003"
],
"requested_by": "userB"
}
```

## Configuration
- Check [`application.properties`](https://github.com/sbhrwl/microservices/blob/main/designingmicroservices/services/commandorchestratorservice/commandorchestrationservice/src/main/resources/application.properties) file.

## Troubleshooting
- The database `commandorchestrator` will not appear in the output of show dbs until it contains at least one document. 
- MongoDB only creates a database when data is inserted into it.
- How to Verify:
  - Ensure your application has successfully saved a CommandRequest document to the command_requests collection.
  - After inserting a document, run the following in the MongoDB shell:
    ```
    show dbs
    use commandorchestrator
    show collections
    ```  
  - You should see the `command_requests` collection listed.
  - To confirm the data, query the collection:
    ```
    db.command_requests.find().pretty()
    ```

## Creating a collection
- Use the `commandorchestrator` database (this will create it if it doesn't already exist):
  ```
  use commandorchestrator
  ```
- Insert a sample document into the `command_requests` collection to create it
  ```
  db.command_requests.insertOne({
      commandType: "SET_TOU",
      commandParams: {
          touProfileId: "TOU-2025-WINTER"
      },
      deviceIds: ["meter-001", "meter-002", "meter-003"],
      requestedBy: "scheduler-service",
      commandId: "681dce4358cba50614cee672",
  })
  ```
  - Verify the Database and Collection
    - `show dbs`
    - `use commandorchestrator`
    - `db.command_requests.find().pretty()`
- Drop databse
  ```
  use commandorchestrator
  db.dropDatabase()
  ```

## Create new user for database
- Create a new user for the `commandorchestrator` Database
  - Instead of using the root user, create a dedicated user for the commandorchestrator database.
### Steps
- Switch to the commandorchestrator database:
  ```
  use commandorchestrator
  ```
- Create a new user with readWrite permissions for the commandorchestrator database:
  ```
  db.createUser({
      user: "command_user",
      pwd: "command_pass",
      roles: [{ role: "readWrite", db: "commandorchestrator" }]
  })  
  ```
