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
- [notification-service-hpa.yaml](orchestrate-sensor-services-with-hpa/templates/notification-service-hpa.yaml)
- [registration-service-hpa.yaml](orchestrate-sensor-services-with-hpa/templates/registration-service-hpa.yaml)
- [sensor-service-hpa.yaml](orchestrate-sensor-services-with-hpa/templates/sensor-service-hpa.yaml)
- [ui-service-hpa.yaml](orchestrate-sensor-services-with-hpa/templates/ui-service-hpa.yaml)
- [values.yaml](orchestrate-sensor-services-with-hpa/values.yaml)
## Installation
- Go to Helm chart folder (e.g., [orchestrate-sensor-services-with-hpa](orchestrate-sensor-services-with-hpa)), run this command:
  ```bash
  helm install orchestrate-sensor-services-with-hpa-release .
  ```
- To install with a specific environment configuration:
  ```bash
  helm install orchestrate-sensor-services-with-hpa-services . -f values-prod.yaml
  ```
