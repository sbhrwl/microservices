# [MQTT](https://github.com/sbhrwl/system_design/blob/main/docs/services/mqtt/README.md)
* [Architecture](#architecture)
* [Goal](#goal)
* [Next steps](#next-steps)
## Architecture
* Components:
  * **Spring Boot App:** REST controller, Publisher, Subscriber services
  * **Mosquitto Broker:** Docker container
  * **Simulated sensors:** 10 clients publishing data
* Topics and QoS:
  * `sensor/+/data` → QoS 0 (fast)
  * `sensor/+/control` → QoS 1 (reliable)
* Workflow:
  * REST API triggers control commands
  * Publisher sends MQTT messages to broker
  * Subscriber receives and logs sensor data
## Goal
* POC with 10 sensors to simulate:
  * Sensor data publishing
  * Receiving control commands (ON/OFF)
* Technology stack:
  * Mosquitto broker (Docker)
  * Spring Boot app with REST controller and Publisher/Subscriber services
## Next steps
* Set up Docker Mosquitto container (Windows)
* Initialize Spring Boot project
* Implement Publisher and Subscriber services
* Expose REST API for control commands
