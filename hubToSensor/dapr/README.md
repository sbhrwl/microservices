# DAPR
## Setup
* **Control plane components** (the same ones `dapr init -k` installs)
 * `dapr-operator` manages sidecar injection and component configs.
 * `dapr-sentry` handles mTLS (can be disabled, but still part of setup).
 * `dapr-placement` is needed for actor placement, but gets installed anyway.
```
C:\Git\microservices\hubToSensor\hpa\orchestrate-hubtosensor-services>dapr --version
CLI version: 1.12.0
Runtime version: 1.12.2

C:\Git\microservices\hubToSensor\hpa\orchestrate-hubtosensor-services>dapr init -k
Making the jump to hyperspace...
Note: To install Dapr using Helm, see here: https://docs.dapr.io/getting-started/install-dapr-kubernetes/#install-with-helm-advanced
Container images will be pulled from Docker Hub
Deploying the Dapr control plane with latest version to your cluster...
Deploying the Dapr dashboard with latest version to your cluster...
Success! Dapr has been installed to namespace dapr-system. To verify, run `dapr status -k' in your terminal. To get started, go here: https://aka.ms/dapr-getting-started

C:\Git\microservices\hubToSensor\hpa\orchestrate-hubtosensor-services>dapr status -k
  NAME                   NAMESPACE    HEALTHY  STATUS   REPLICAS  VERSION  AGE  CREATED
  dapr-dashboard         dapr-system  True     Running  1         0.15.0   2m   2025-10-27 18:17.14
  dapr-sentry            dapr-system  True     Running  1         1.16.1   2m   2025-10-27 18:17.10
  dapr-placement-server  dapr-system  True     Running  1         1.16.1   2m   2025-10-27 18:17.10
  dapr-operator          dapr-system  True     Running  1         1.16.1   2m   2025-10-27 18:17.10
  dapr-sidecar-injector  dapr-system  True     Running  1         1.16.1   2m   2025-10-27 18:17.10

C:\Git\microservices\hubToSensor\hpa\orchestrate-hubtosensor-services>kubectl get pods -n dapr-system
NAME                                    READY   STATUS    RESTARTS      AGE
dapr-dashboard-5cb455db6f-2g9jf         1/1     Running   0             2m29s
dapr-operator-c6458b8bf-zqmsl           1/1     Running   2 (86s ago)   2m33s
dapr-placement-server-0                 1/1     Running   0             2m33s
dapr-scheduler-server-0                 1/1     Running   0             2m33s
dapr-scheduler-server-1                 1/1     Running   0             2m33s
dapr-scheduler-server-2                 1/1     Running   0             2m33s
dapr-sentry-79d87f5fc7-xcvgg            1/1     Running   0             2m33s
dapr-sidecar-injector-f5b58c7c7-p6rgz   1/1     Running   0             2m33s

C:\Git\microservices\hubToSensor\dapr\config-files>kubectl apply -f rabbitmq-pubsub.yaml
component.dapr.io/rabbitmq-pubsub created

C:\Git\microservices\hubToSensor\dapr\config-files>kubectl apply -f postgres-statestore.yaml
component.dapr.io/postgres-statestore created
```
- [rabbitmq-pubsub.yaml](config-files/rabbitmq-pubsub.yaml)
- [postgres-statestore.yaml](config-files/postgres-statestore.yaml)

* [Problem](#problem)
* [Original architecture](#original-architecture)
* [Challenges](#challenges)
* [Approaches for cloud-agnostic microservices](#approaches-for-cloud-agnostic-microservices)
* [Dapr advantages](#dapr-advantages)
* [Dapr implementation changes](#dapr-implementation-changes)
* [Service interaction](#service-interaction)
* [Takeaway](#takeaway)
* [Dapr vs Istio sidecar](#dapr-vs-istio-sidecar)
* [UML](#uml)
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

## Takeaway
* Dapr enables truly cloud-native, portable microservices.
* Maintenance overhead is reduced with a single codebase.
* Flex Hub Simulator and Storage Service can switch messaging and DB backends with zero code changes.
* Sidecar-based abstraction aligns with multi-cloud, containerized architectures.

## Dapr vs Istio sidecar
- Dapr sidecar: handles application-level integrations (message broker, state store, pub/sub, bindings, etc.).
- Istio sidecar: handles network-level concerns (mTLS, retries, routing, observability for service-to-service calls).

## UML
<img src="images/dapr.jpg">

<details>
  <summary>uml</summary>
 
**UML**
```uml
@startuml
skinparam class {
  BackgroundColor White
  BorderColor Black
  Shadowing true
}

' ===== External Services =====
package "Kubernetes Cluster - External Services" #ADD8E6 {
  class UI_App
  class Data_API
  class FHS {
    -- DAPR --
    Flex_Hub_Simulator
    -- Envoy/Istio --
  }
}

' ===== Internal Services =====
package "Kubernetes Cluster - Internal Services" #90EE90 {
  class FB {
    -- DAPR --
    Flexibility_Bridge
    -- Envoy/Istio --
  }

  class PA {
    -- DAPR --
    Protocol_Adapter
    -- Envoy/Istio --
  }

  class HES {
    -- DAPR --
    HES_Simulator
    -- Envoy/Istio --
  }

  class SS {
    -- DAPR --
    Storage_Service
    -- Envoy/Istio --
  }
}

' ===== Infrastructure =====
package "Exposed Infrastructure (Outside Cluster)" #FFA07A {
  class IngressGateway
}

' ===== External Systems =====
package "External Systems" #F0E68C {
  class Message_Broker
  class Database
}

' ===== External access =====
UI_App --> IngressGateway : HTTPS
Data_API --> IngressGateway : HTTPS
FHS --> IngressGateway : HTTPS

' ===== Routing =====
IngressGateway --> Data_API : /api
IngressGateway --> UI_App : /ui
IngressGateway --> FHS : /simulator

' ===== Broker flow =====
FHS --> Message_Broker : publish requests/events (TLS)
Message_Broker --> FB : consume & store via Storage_Service
Message_Broker --> PA : consume & convert protocol
Message_Broker --> HES : consume converted requests
HES --> Message_Broker : publish responses (TLS)
Message_Broker --> PA : consume responses & parse
Message_Broker --> FB : consume parsed responses & update Storage_Service
FHS --> Message_Broker : consume final responses

' ===== Storage service to Database =====
FB --> SS : gRPC write/update
PA --> SS : gRPC read/write
SS --> Database : persist / read data

' ===== Data_API direct connection =====
Data_API --> Database : direct read/write

@enduml
```

</details>
