# Introduction
- Sensor registration
  -  **Direct UI Input:** 
     - A user logs in through the UI and enters their inverter details and email address directly. 
     - This data is then sent to the **Sensor Service**, which saves it and triggers the **Notification Service**.
  -  **Secured API Endpoint via Kafka:** 
     - An external client (potentially another system or a different user flow) sends a POST request to a secured endpoint of the **Sensor Service**. 
     - This triggers a message to be sent to Kafka. A separate **Registration Service** consumes this message, saves the data to its own MongoDB instance, and then calls the **Notification Service**.
- This provides flexibility and allows for different integration points into our system. 
  - The first method is likely for direct user interaction
  - The second could be for automated registrations or integrations with other platforms.
```
sensor-registration/
├── sensor-service/      (Handles direct UI submissions and API endpoint, publishes to Kafka)
├── registration-service/ (Consumes Kafka messages and processes registrations to mongodb)
├── notification-service/ (Sends email notifications)
└── ui-service/           (Provides the user interface)
```