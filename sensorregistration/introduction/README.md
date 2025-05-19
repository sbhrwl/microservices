# Introduction
- Sensor registration
  -  **Direct UI input:** 
     - A user logs in through the UI and enters their sensor details and email address directly. The send button on UI calls **Sensor Service**.
  -  **Secured API endpoint** 
     - An external client sends a POST request to a secured endpoint of the **Sensor Service**. 
- **Flow**
  - **Sensor Service** enqueues message to Kafka topic
  - **Registration Service** consumes message from kafka, saves the data to MongoDB, and then calls the **Notification Service**.
- This provides flexibility and allows for different integration points into our system. 
  - The first method is for direct user interaction
  - The second method is for automated registrations or integrations with other platforms.
```
sensor-registration/
├── sensor-service/      (Handles direct UI submissions and API endpoint, publishes to Kafka)
├── registration-service/ (Consumes Kafka messages and processes registrations to mongodb)
├── notification-service/ (Sends email notifications)
└── ui-service/           (Provides the user interface)
```