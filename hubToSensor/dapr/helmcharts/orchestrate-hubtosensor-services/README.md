# Configmap
* [Install prerequisites](#install-prerequisites)
* [Create dapr components namespace](#create-dapr-components-namespace)
* [Deploy rabbitmq](#deploy-rabbitmq)
* [Apply dapr pubsub configmap](#apply-dapr-pubsub-configmap)
* [Install helm chart](#install-helm-chart)
* [Validate dapr integration](#validate-dapr-integration)
## Install prerequisites
* Ensure kubectl, helm, and dapr CLI are installed.
## Create dapr components namespace
* Create namespace:
  ```bash
  kubectl create namespace dapr-components
  ```
* Verify:
  ```bash
  kubectl get namespaces
  ```
## Deploy rabbitmq
## Apply dapr pubsub configmap
* Apply: This will be deployed in `dapr-components`
  * [`cd orchestrate-hubtosensor-services\templates`](templates)
  ```bash
  kubectl apply -f rabbitmq-pubsub-configmap.yaml
  ```
* Verify:
  ```bash
  kubectl get configmap -n dapr-components
  kubectl get components -n dapr-components
  ```
## Install helm chart
* Install your application:
  ```bash
  helm install flexibility-hub-simulator . --namespace default
  ```
* Check deployment:
  ```bash
  helm list -n default
  kubectl get pods -n default
  ```
## Validate dapr integration
* Inspect pod annotations:
  ```bash
  kubectl describe pod <pod> -n default
  ```
* Confirm component registration:
  ```bash
  dapr components -k
  ```