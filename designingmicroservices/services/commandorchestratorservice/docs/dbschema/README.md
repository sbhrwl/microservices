# Database Schema
## Document DB based
- Each document represents the latest status of a single command sent to a device:
```json
{
  "deviceId": "meter-001",
  "commandId": "cmd-123456",
  "correlationId": "123456-abc",
  "commandType": "SET_TOU",
  "issuedAt": "2025-05-08T02:00:00Z",
  "status": "SUCCESS",
  "lastUpdated": "2025-05-08T02:01:10Z",
  "error": null
}
```

* `payloadSummary`: optional JSON blob (e.g., registers set, OBIS codes)
* `responseRaw`: optional decoded response string (for troubleshooting)

## 
- The database `mydatabase` will not appear in the output of show dbs until it contains at least one document. 
- MongoDB only creates a database when data is inserted into it.
- How to Verify:
  - Ensure your application has successfully saved a CommandRequest document to the command_requests collection.
  - After inserting a document, run the following in the MongoDB shell:
    ```
    use mydatabase
    show collections
    ```  
  - You should see the `command_requests` collection listed.
  - To confirm the data, query the collection:
    ```
    db.command_requests.find().pretty()
    ```
  - If no documents are present, ensure your application is running correctly and that the KafkaProducerService is saving the CommandRequest to MongoDB.