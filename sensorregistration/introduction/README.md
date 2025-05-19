# Introduction
- Sensor registration
  -  **Direct UI Input:** 
     - A user logs in through the UI and enters their inverter details and email address directly. 
     - This data is then sent to the **Inverter Service**, which saves it and triggers the **Notification Service**.
  -  **Secured API Endpoint via Kafka:** 
     - An external client (potentially another system or a different user flow) sends a POST request to a secured endpoint of the **Inverter Service**. 
     - This triggers a message to be sent to Kafka. A separate **Registration Processing Service** consumes this message, saves the data to its own MongoDB instance, and then calls the **Notification Service**.
- This provides flexibility and allows for different integration points into our system. 
  - The first method is likely for direct user interaction
  - The second could be for automated registrations or integrations with other platforms.
```
sensor-registration/
├── sensor-service/      (Handles direct UI submissions and API endpoint, produces Kafka messages)
├── registration-service/ (Consumes Kafka messages and processes registrations)
├── notification-service/ (Sends email notifications)
└── ui-service/           (Provides the user interface)
```