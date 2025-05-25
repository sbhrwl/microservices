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
  │   ├── sensor-deployment.yaml
  │   ├── sensor-service.yaml
  │   ├── registration-deployment.yaml
  │   ├── registration-service.yaml
  │   ├── notification-deployment.yaml
  │   ├── notification-service.yaml
  │   └── _helpers.tpl
  ├── Chart.yaml
  ├── values.yaml         # default (used for dev/test)
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
    autoscaling:
      enabled: true
      minReplicas: 2
      maxReplicas: 10
      targetCPUUtilizationPercentage: 75
  
  sensorService:
    autoscaling:
      enabled: true
      minReplicas: 2
      maxReplicas: 10
      targetCPUUtilizationPercentage: 75
  registrationService:
    autoscaling:
      enabled: true
      minReplicas: 2
      maxReplicas: 10
      targetCPUUtilizationPercentage: 75
  
  notificationService:
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
