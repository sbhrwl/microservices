# Authentication with API keys
- [Introduction](#introduction)
- [Project](#project)
  - [Custom API key filter](#custom-api-key-filter)
- [API key in the header](#api-key-in-the-header)
- [Generating API keys](#generating-api-keys)
  - [Recommendations for generating API keys](#recommendations-for-generating-api-keys)
- [API keys for sensors](#api-keys-for-sensors)
- [Sharing API keys with client](#sharing-api-keys-with-client)
  - [Important considerations](#important-considerations)
- [Configuring API keys for sensors](#configuring-api-keys-for-sensors)
- [Rotating API keys](#rotating-api-keys)
  - [Example scenario](#example-scenario)
## Introduction
- API keys is an option to use for **authentication**
- Clients can send these keys in two most common ways are:
  * As a custom HTTP header: 
    - For example, a header named `X-API-KEY` with the API key as its value.
    - This is generally considered a bit **more secure** as it keeps the key out of the URL.
  * As a query parameter: 
    - For example, appending `?apiKey=your_api_key` to the API endpoint URL. 
    - This is simpler to implement but **less secure** as the key can be easily visible in server logs, browser history, etc.
## Project
- Create spring boot app with [spring initialiser](https://start.spring.io/)
  - Build: `mvn clean install`
  - Run: `mvn spring-boot:run`
- **Endpoints**
  - **Public**: `GET` `http://localhost:8081/public`
    - Expected result: `This is a public endpoint.`
  - **Secure**: `GET` `http://localhost:8081/hello` 
    - Expected result: `403 Forbidden`
```
src
└── main
    └── java
        └── com
            └── example
                └── apikeyauth
                    ├── config
                    │   ├── ApiKeyProperties.java
                    │   └── SecurityConfig.java
                    ├── controller
                    │   └── HelloController.java
                    └── security
                        └── ApiKeyAuthFilter.java
    └── resources
        └── application.yaml
```

### [Custom API key filter](src\main\java\com\example\apikeyauth\security\ApiKeyAuthFilter.java)
* Runs once for every incoming HTTP request (`OncePerRequestFilter`).
* Checks if the request includes the `X-API-KEY` header.
* Retrieves the expected API key from `application.yaml` via `ApiKeyProperties`.
* If the header matches the configured key:
  * Creates a `UsernamePasswordAuthenticationToken` with a predefined user and role.
  * Sets it in the `SecurityContextHolder` to mark the request as authenticated.
* If the key is missing or invalid:
  * Lets the request proceed, but the user remains unauthenticated (optional: you can explicitly return `401 Unauthorized` here).
* Proceeds with the filter chain (`filterChain.doFilter(...)`) regardless.

## API key in the header
* **Generate and store API keys**: 
  - Create unique API keys and store them securely, perhaps in a database, along with the clients they belong to.
* **Intercept incoming requests**: 
  - Inspect each incoming request for the presence and validity of the X-API-KEY header.
* **Validate the API key**: 
  - Check if the provided API key exists in your storage and is active.
* **Handle invalid keys**: 
  - If the key is missing or invalid, you should reject the request with an appropriate HTTP status code (like 401 Unauthorized).
## Generating API keys
- Generating API keys involves creating unique, often long and random strings. 
- Common approaches
  - **Universally Unique Identifier (UUID):**
    - UUIDs are 128-bit numbers designed to have a very low probability of collision.
    - They are easy to generate programmatically. In Java, you can use `java.util.UUID.randomUUID().toString()`.
    - This gives you a string like `a1b2c3d4-e5f6-7890-1234-567890abcdef`.
  - **Cryptographically Secure Random Number Generators:**
    - You can use libraries that provide `cryptographically strong random number generation` and `encode the result` (e.g., in Base64 or hexadecimal) to get a string.
    - This can offer more control over the length and character set of your keys.
    - In Java, you might use `java.security.SecureRandom` along with Base64 encoding from `java.util.Base64`.
### Recommendations for generating API keys
* **Long enough:** To make brute-force guessing practically impossible.
* **Random:** To avoid predictability.
* **Treated as secrets:** Just like passwords, they should be stored securely (e.g., hashed if you need to compare them, though often you store the exact key and compare).
## API keys for sensors 
- API keys for sensors should be long, random strings for security. 
- Keep the key **opaque** and map it to sensor details in your secure backend.
## Sharing API keys with client
- How you share API keys with your clients depends on several factors, including:
  * **The nature of your clients:** Are they other applications you control, third-party developers, or end-users?
  * **The level of security required:** Some methods are more secure than others.
  * **Your onboarding process:** How do new clients get access to your API?
- **Through a secure administrative interface:**
  - If you have a web portal or backend system for managing API access, you can generate and display the keys there for clients to retrieve after they've been authenticated and authorized to use your API.
  - This is generally a good approach for third-party developers.
- **During an onboarding process:**
  - When a new client signs up for access to your API, the key can be generated and presented to them as part of the setup.
  - This could be displayed on a confirmation page or sent via a secure communication channel like email (though email isn't inherently secure, so be cautious).
- **As part of a software development kit (SDK) or configuration file:**
  - If your API is intended to be used with a specific SDK or requires a configuration file, the API key might be embedded within that.
  - However, this can be less secure if the SDK or configuration file is not properly protected.
- **Through a dedicated API endpoint (less common for initial key distribution):**
  - You could have an API endpoint that clients call to request a key after authenticating through some other means (like username/password or OAuth).
  - This is more common for key rotation or generating temporary tokens.
### Important considerations
* **Secure Transmission:** When sharing keys, especially initially, use secure channels like HTTPS. Avoid sending keys over plain HTTP.
* **One-time Display (if possible):** For initial setup, consider displaying the key only once to the client, requiring them to store it securely on their end.
* **Client Responsibility:** Clearly communicate to your clients that the API key is a secret and they are responsible for keeping it confidential.
## Configuring API keys for sensors
- **Centralized Key Management System:**
  - You'll need a system to generate, store, and manage a large number of API keys.
  - This could be a dedicated database or a specialized key management service (KMS).
- **Key Generation per Sensor (or Group of Sensors):**
  - Ideally, each sensor or a logical group of sensors should have its own unique API key.
  - This allows for granular control and easier revocation if a sensor is compromised.
-  **Automated Key Provisioning:**
   - You'll need a process to automatically provision API keys to new sensors as they are deployed.
   - This could be part of the sensor's setup or registration process.
   - For example:
     * When a new sensor is registered with your platform, your backend system automatically generates a unique API key and associates it with that sensor's ID.
     * The API key could be provisioned to the sensor during its initial configuration, perhaps via a secure communication channel during setup.
- **Secure Key Storage on the Sensor (if applicable):**
  - If the sensors have the capability to securely store data, the API key should be stored there to prevent unauthorized access.
- **API Key Validation Middleware:**
  - On your API server, you'll need a robust middleware component that intercepts every incoming request, extracts the API key from the `X-API-KEY` header, and validates it against your central key management system.
  - This middleware should be highly efficient to handle the volume of requests from thousands of sensors.
- **Monitoring and Revocation:**
  - You'll need tools to monitor API key usage and the ability to quickly revoke keys if a sensor is compromised or needs to be decommissioned.
### Options
- **During Sensor Registration:**
  - When a sensor is registered with your system (perhaps by a unique ID or serial number), your backend service would:
    * Generate a unique API key (using UUID or a secure random generator).
    * Store the API key in your central key management system, associated with the sensor's ID.
    * Securely transmit the API key to the sensor (this step needs careful consideration based on the sensor's capabilities and security).
- **API Request Handling:**
  - When a sensor sends data to your `/message/generate` endpoint, it includes the `X-API-KEY` in the header.
  - Your API server would:
    * Extract the `X-API-KEY` from the request header.
    * Query your key management system to verify if the key exists and is associated with a valid sensor.
    * If the key is valid, process the data. Otherwise, reject the request with a 401 Unauthorized status.
## Rotating API keys
- **Establish a Rotation Policy:**
  - Decide on a frequency for key rotation (e.g., every month, every quarter).
  - The appropriate frequency depends on your security requirements and risk assessment.
- **Generate a New Key:**
  - When it's time to rotate a key for a sensor (or a group of sensors), your key management system generates a new, unique API key.
-  **Securely Distribute the New Key:**
  -  The new key needs to be securely transmitted to the affected sensor(s).
  -  This is a critical step and the method will depend on the capabilities of your sensors and your infrastructure.
  -  Some possibilities include:
     * **Over-the-air (OTA) Updates:** If your sensors support secure OTA updates, you can include the new API key as part of an update package. This requires a secure communication channel.
     * **Push Notifications/Commands:** You might have a mechanism to send secure commands or notifications to individual sensors to update their API key.
     * **During the Next Connection:** When a sensor connects to your platform, you could potentially provide the new key after verifying the old key (if it's still valid). However, this might be less reliable if a compromised key is being used.
- **Update Key Mapping in Your System:**
  - In your central key management system, you need to associate the sensor's ID with the new API key and potentially mark the old key as inactive (or keep it active for a short transition period).
- **Grace Period (Optional but Recommended):**
  - To avoid service disruptions, you can implement a grace period where both the old and the new API keys are valid for a short time.
  - This allows sensors that haven't yet received or applied the new key to continue sending data.
- **Key Revocation:**
  - After the grace period, the old API key should be fully revoked and rejected by your API.
### Example scenario
- Let's say you decide to rotate keys monthly.
  - On the first of each month, a process in your backend system identifies the keys due for rotation.
  - For each key, a new unique key is generated.
  - A secure communication channel (e.g., TLS/SSL connection initiated by the sensor) is used to send a command to the sensor containing the new API key.
    - The sensor securely stores this new key.
  - Your key management system updates the mapping for that sensor, associating it with the new key and marking the old key as valid until, say, the 5th of the month.
  - After the 5th, requests with the old key are rejected.
