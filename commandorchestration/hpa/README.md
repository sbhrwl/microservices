# Horizontal Pod Autoscalar
## Introduction 
- The Horizontal Pod Autoscaler (HPA) is a Kubernetes resource that **automatically scales** the number of pods in a deployment, replica set, or stateful set based on observed metrics like `CPU utilization`, `memory usage`, or `custom metrics`.
## Structure
- [orchestration-services](https://github.com/sbhrwl/microservices/tree/main/hpa/orchestration-services-with-hpa)
- This Helm chart deploys two services:
  - **Command Orchestration**
  - **Task Orchestration**
- Each service has its own Deployment, Service, and `optional` Horizontal Pod Autoscaler (HPA) configuration.
  ```
  orchestration-services/
  ├── templates/
  │   ├── deployment-command-orchestration.yaml
  │   ├── deployment-task-orchestration.yaml
  │   ├── service-command-orchestration.yaml
  │   ├── service-task-orchestration.yaml
  │   ├── hpa-command-orchestration.yaml
  │   └── hpa-task-orchestration.yaml
  ├── values.yaml
  ├── values-staging.yaml
  └── values-prod.yaml
  ```
## HPA configuration
- HPA is configured in the [`values.yaml`](https://github.com/sbhrwl/microservices/blob/main/hpa/orchestration-services-with-hpa/values.yaml) for both services
  - `commandOrchestration.autoscaling`
  - `taskOrchestration.autoscaling` 
- Example configuration:
  ```yaml
  commandOrchestration:
    autoscaling:
      enabled: true
      minReplicas: 2
      maxReplicas: 10
      targetCPUUtilizationPercentage: 75
  
  taskOrchestration:
    autoscaling:
      enabled: true
      minReplicas: 2
      maxReplicas: 10
      targetCPUUtilizationPercentage: 75
  ```
- To disable HPA, set `enabled: false` in the respective section.

## Installation
- Go to Helm chart folder (e.g., [orchestration-services](https://github.com/sbhrwl/microservices/tree/main/hpa/orchestration-services-with-hpa)), run this command:
  ```bash
  helm install orchestration-release .
  ```
- To install with a specific environment configuration:
  ```bash
  helm install orchestration-services . -f values-prod.yaml
  ```
- refer this [page](https://github.com/sbhrwl/microservices/blob/main/helmcharts/creatingenvs/README.md) for verification steps
