# Horizontal Pod Autoscalar
## Introduction 
- The Horizontal Pod Autoscaler (HPA) is a Kubernetes resource that **automatically scales** the number of pods in a deployment, replica set, or stateful set based on observed metrics like `CPU utilization`, `memory usage`, or `custom metrics`.
## Structure
- [orchestrate-sensor-services-with-hpa](orchestrate-sensor-services-with-hpa)
- This Helm chart deploys two services:
  - **Command Orchestration**
  - **Task Orchestration**
- Each service has its own Deployment, Service, and `optional` Horizontal Pod Autoscaler (HPA) configuration.
  ```
  orchestrate-sensor-services/
  ├── charts/
  ├── templates/
  │   ├── ui-deployment.yaml
  │   ├── ui-service.yaml
  │   ├── ui-service-hpa.yaml
  │   ├── sensor-deployment.yaml
  │   ├── sensor-service.yaml
  │   ├── sensor-service-hpa.yaml
  │   ├── registration-deployment.yaml
  │   ├── registration-service.yaml
  │   ├── registration-service-hpa.yaml
  │   ├── notification-deployment.yaml
  │   ├── notification-service.yaml
  │   ├── notification-service-hpa.yaml
  │   └── _helpers.tpl
  ├── Chart.yaml
  ├── values.yaml
  ├── values-staging.yaml
  ├── values-prod.yaml
  ```
## HPA configuration
- HPA is configured in the [`values.yaml`](orchestrate-sensor-services-with-hpa/values.yaml) for the sensor registration services
  - `uiService.autoscaling`
  - `sensorService.autoscaling` 
  - `registrationService.autoscaling` 
  - `notificationService.autoscaling` 
- Example configuration:
  ```yaml
  uiService:
    image: ui-service:latest
    port: 9081
    env:
      SERVER_PORT: "9081"
      KEYCLOAK_URL: "http://keycloak:8080/"
      KEYCLOAK_REALM: "master"
      KEYCLOAK_CLIENTID: "sensor-service"
      SENSOR_SERVICE_URL: "http://sensor-service:9082"
    autoscaling:
      enabled: true
      minReplicas: 2
      maxReplicas: 10
      targetCPUUtilizationPercentage: 75

  sensorService:
    image: sensor-service:latest
    port: 9082
    env:
      SERVER_PORT: "9082"
      KEYCLOAK_ISSUER_URI: "http://keycloak:8080/realms/master"
      KEYCLOAK_JWK_SET_URI: "http://keycloak:8080/realms/master/protocol/openid-connect/certs"
      KEYCLOAK_CLIENT_ID: "sensor-service"
      KEYCLOAK_PROVIDER: "keycloak"
      KAFKA_HOST: "kafka"
      KAFKA_PORT: "29092"
      KAFKA_SENSOR_REG_TOPIC: "sensor-registrations"
      CORS_ALLOWED_ORIGINS: "http://ui-service:9081"
    autoscaling:
      enabled: true
      minReplicas: 2
      maxReplicas: 10
      targetCPUUtilizationPercentage: 75

  registrationService:
    image: registration-service:latest
    port: 9083
    env:
      SERVER_PORT: "9083"
      MONGO_HOST: "mongo"
      MONGO_PORT: "27017"
      MONGO_USERNAME: "root"
      MONGO_PASSWORD: "root123"
      KAFKA_HOST: "kafka"
      KAFKA_PORT: "29092"
      SPRING_KAFKA_CONSUMER_BOOTSTRAP-SERVERS: "kafka:29092"
      NOTIFICATION_SERVICE_URL: "http://notification-service:9084"
    autoscaling:
      enabled: true
      minReplicas: 2
      maxReplicas: 10
      targetCPUUtilizationPercentage: 75

  notificationService:
    image: notification-service:latest
    port: 9084
    autoscaling:
      enabled: true
      minReplicas: 2
      maxReplicas: 10
      targetCPUUtilizationPercentage: 75
  ```
- To disable HPA, set `enabled: false` in the respective section.

## Installation
- Go to Helm chart folder (e.g., [orchestrate-sensor-services-with-hpa](orchestrate-sensor-services-with-hpa)), run this command:
  ```bash
  helm install orchestrate-sensor-services-with-hpa-release .
  ```
- To install with a specific environment configuration:
  ```bash
  helm install orchestrate-sensor-services-with-hpa-services . -f values-prod.yaml
  ```
- refer this [page](https://github.com/sbhrwl/microservices/blob/main/sensorregistration/helmcharts/creatingenvs/README.md) for verification steps
