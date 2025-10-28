## Architecture
* [Introduction](#introduction)
* [Dapr vs Istio sidecar](#dapr-vs-istio-sidecar)
* [Design](#design)
## Introduction
* Dapr enables truly cloud-native, portable microservices.
* Maintenance overhead is reduced with a single codebase.
* Flex Hub Simulator and Storage Service can switch messaging and DB backends with zero code changes.
* Sidecar-based abstraction aligns with multi-cloud, containerized architectures.

## Dapr vs Istio sidecar
- Dapr sidecar: handles application-level integrations (message broker, state store, pub/sub, bindings, etc.).
- Istio sidecar: handles network-level concerns (mTLS, retries, routing, observability for service-to-service calls).

## Design
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