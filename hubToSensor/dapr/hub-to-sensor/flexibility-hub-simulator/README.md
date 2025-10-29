# Flexibility Hub simulator
- [Properties](#properties)
- [How to run](#how-to-run)
- [Test](#test)
- [kubectl apply vs dapr run](#kubectl-apply-vs-dapr-run)
## Properties
- [application.yml](src/main/resources/application.yml)
## How to run
- Create spring boot app with [spring initialiser](https://start.spring.io/)
- Build 
```bash
mvn clean install
mvn clean install -U
mvn clean install -DskipTests
```
- Run
```bash
mvn spring-boot:run
dapr run --app-id flexibility-hub-simulator --app-port 8081 --resources-path .\dapr\config-files -- mvn spring-boot:run`
```

## Test
- Push data to Broker
  - `POST`: `http://localhost:8081/api/messages`
  - Payload
    ```json
    {
      "sensorId": "sensor-001",
      "operation": "DIRECT-ON",
      "relayNumber": 2,
      "duration": 30,
      "status": "Received"
    }
    ```

## kubectl apply vs dapr run
### What happens when you do
```bash
kubectl apply -f rabbitmq-pubsub.yaml
```
- This command is used **only in Kubernetes mode**.
- It tells the **Dapr control plane running inside the cluster** to load a **Component definition** (in this case, your RabbitMQ pub/sub).
- So, **`when`** your app runs inside Kubernetes with:
```yaml
spec:
  type: pubsub.rabbitmq
```
- Dapr automatically discovers it because it’s stored as a **Kubernetes Custom Resource** (`Component` object).
- In other words:
  - `kubectl apply` registers the component with Dapr inside the cluster — so any app in that namespace can use it.
### ⚙️ What happens when you do
```bash
dapr run --app-id flexibility-hub-simulator --app-port 8081 --resources-path .\dapr\config-files -- mvn spring-boot:run
```
- This is **local (self-hosted) mode** — Dapr is not using Kubernetes.
- So it doesn’t have access to any Components stored in K8s.
- Instead, you give it a **local folder** via:
```
--resources-path .\dapr\config-files
```
- That folder contains YAML files like `rabbitmq-pubsub.yaml`.
- Dapr reads those directly from disk and loads them as local components.

### In summary

| Context                 | Where component is stored               | How Dapr finds it                      |
| ----------------------- | --------------------------------------- | -------------------------------------- |
| **Kubernetes mode**     | In the cluster (`kubectl apply`)        | Dapr operator injects it automatically |
| **Self-hosted (local)** | On your filesystem (`--resources-path`) | You explicitly point to it             |

- So in your current setup — where you’re running locally with `dapr run` —
  - ✅ you **do not** need to run `kubectl apply -f rabbitmq-pubsub.yaml`.
  - That was only relevant if you were deploying the same app inside Kubernetes.
