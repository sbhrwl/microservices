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
```uml
@startuml
title Spring Boot + MQTT (POC Architecture)

actor User as U

rectangle "Spring Boot App" as SB {
  component "REST Controller" as RC
  component "Publisher Service" as PUB
  component "Subscriber Service" as SUB
}

node "Mosquitto Broker (Docker)" as MB {
  [MQTT Broker]
}

node "Sensors (Simulated Clients)" as S {
  [Sensor 1]
  [Sensor 2]
  ...
  [Sensor 10]
}

U --> RC : HTTP POST /sensor/{id}/on
RC --> PUB : Publish "sensor/{id}/control"\nQoS 1
PUB --> MB : MQTT Publish
S --> MB : Publish "sensor/{id}/data"\nQoS 0
MB --> SUB : Forward "sensor/{id}/data"
SUB --> SB : Log or store data

note right of MB
Topics used:
- sensor/+/data
- sensor/+/control
QoS:
- Control = 1 (reliable)
- Data = 0 (fast)
end note
@enduml
```

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
