# [Introduction](https://github.com/sbhrwl/system_design/blob/main/docs/services/dapr/README.md)
* [Problem](#problem)
* [Original architecture](#original-architecture)
* [Challenges](#challenges)
* [Approaches for cloud-agnostic microservices](#approaches-for-cloud-agnostic-microservices)
* [Dapr advantages](#dapr-advantages)
* [Dapr implementation changes](#dapr-implementation-changes)
* [Service interaction](#service-interaction)
* [Commands](commands/README.md)
## Problem
* Microservices promise agility and scalability but face portability issues across GCP, AWS, and Azure.
* Hardcoded dependencies on specific brokers and databases limit maintainability and cloud migration.

## Original architecture
* **Flex Hub Simulator**: communicates with RabbitMQ.
* **Storage Service**: persists data in PostgreSQL.
* **ServiceMesh (Istio)**: provides mTLS and routing.
* **Kubernetes**: orchestrates all services.
* **Flow overview**:
  * Flex Hub Simulator → RabbitMQ → Bridge → Protocol Adapter → HES Simulator
  * Storage Service → PostgreSQL → Analytics / persistence
## Challenges
* Hardcoded clients (RabbitMQ, JDBC/PostgreSQL) in services.
* Cloud migration requires swapping brokers and databases, resulting in code changes and increased testing.
## Approaches for cloud-agnostic microservices

| approach                       | messaging/db abstraction                      | ease of use | cloud portability | maintenance                   |
| ------------------------------ | --------------------------------------------- | ----------- | ----------------- | ----------------------------- |
| spring profiles / config files | manual switching via configs                  | easy        | low               | high effort per cloud         |
| spring cloud stream            | abstracts messaging only                      | medium      | medium            | db still hardcoded            |
| dapr                           | abstracts messaging, state, bindings, secrets | medium      | high              | single codebase across clouds |

## Dapr advantages
* Decouples services from broker and database implementations.
* Cloud-agnostic components defined in YAML; swapping backends requires no code changes.
* Works seamlessly with Kubernetes and Istio mTLS.
* Reduces maintenance overhead with a single, portable codebase.

## Dapr implementation changes
* Remove hardcoded broker/DB configs from Spring configuration.
* Retain only service-specific settings (ports, logging, feature flags).
* **Minimal `application.yml` example**:
```yaml
application:
  name: flexibility-hub-simulator

server:
  port: ${SERVER_PORT:8081}

dapr:
  http-port: ${DAPR_HTTP_PORT:3500}
  grpc-port: ${DAPR_GRPC_PORT:50001}

logging:
  level:
    com.apexsphere.flexibility_hub_simulator: DEBUG
```

* Define Dapr components for messaging and state store:
  * **RabbitMQ Pub/Sub component**
```yaml
apiVersion: dapr.io/v1alpha1
kind: Component
metadata:
  name: message-broker
spec:
  type: pubsub.rabbitmq
  version: v1
  metadata:
  - name: host
    value: "amqp://admin:admin@rabbitmq:5672"
  - name: consumerID
    value: "flex-hub-consumer"
```

  * **PostgreSQL state store component**
```yaml
apiVersion: dapr.io/v1alpha1
kind: Component
metadata:
  name: storage-db
spec:
  type: state.postgresql
  version: v1
  metadata:
  - name: connectionString
    value: "host=postgres dbname=mydatabase user=myuser password=mypassword sslmode=disable"
```

## Service interaction
* **Flex Hub Simulator → Pub/Sub**
```http
POST http://localhost:3500/v1.0/publish/message-broker/flexibility-hub.request
```

* **Storage Service → State DB**
```http
POST http://localhost:3500/v1.0/state/storage-db
```

* No code changes needed for broker or DB swaps; only YAML updates.
* Istio mTLS, service discovery, and Kubernetes orchestration remain intact.
