# Introduction
- [Services and flow of information](#services-and-flow-of-information)
- [Key aspects](#key-aspects)
## Services and flow of information
1. **Flexibility Hub Simulator → Message Broker**
   * Publishes flexibility requests/events over **TLS-secured connections**.
2. **Message Broker → Flexibility Bridge**
   * Consumes requests over **TLS**.
   * **Creates request records in the database via Storage Service**.
   * Pushes requests back to the broker for protocol conversion.
3. **Message Broker → Protocol Adapter Service**
   * Consumes requests over **TLS**, converts them to the target protocol, and republishes to the broker.
4. **Message Broker → HES Simulator**
   * Consumes converted requests over **TLS**.
   * Simulates execution and sends a **response** (success/failure) back to the broker.
5. **Message Broker → Protocol Adapter Service**
   * Consumes simulated responses over **TLS**, parses them, and republishes to the broker.
6. **Message Broker → Flexibility Bridge**
   * Consumes parsed responses over **TLS**.
   * **Updates the final status of requests in the database via Storage Service**.
   * Publishes **final responses** to the broker for the Flexibility Hub Simulator.
7. **Message Broker → Flexibility Hub Simulator**
   * Consumes final responses over **TLS** to track the **status of its requests**.
8. **Data API Layer → User Interface**
   * Exposes APIs to fetch request statuses, telemetry, and results.
   * **UI and Data API Layer are secured via Keycloak**.
   * UI is exposed over **HTTPS**.
## Key aspects
* **Storage Service** handles all database operations.
* **HES Simulator** generates responses.
* **Message Broker** orchestrates async communication and is secured via **TLS**.
* **Flexibility Hub Simulator** tracks requests through broker responses.
* **UI and Data API Layer** secured with **Keycloak**, with HTTPS for encrypted client access.
