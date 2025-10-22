# [Service mesh](istio/README.md)
- [Internal services and TLS traffic](#internal-services-and-tls-traffic)
  - [Storage service](#storage-service)
- [External access](#external-access)
- [Observability and debugging](#observability-and-debugging)
- [Optional improvements](#optional-improvements)
- [Istio setup](#istiosetup/README.md)
- [UML](#uml)
## Internal services and TLS traffic
* **Message Broker ↔ Flexibility hub simulator/ Flexibility Bridge / Protocol Adapter / HES Simulator**
  * Istio can **automatically inject Envoy sidecars** into all services.
  * **mTLS**: all TLS traffic between services can be managed by Istio. 
    * **`No need`** to handle custom TLS in your services.
  * **Service discovery**: broker and clients locate each other via **service names** (K8s DNS) 
    * **`No hardcoded endpoints`**.
### Storage service
* Accessed by Flexibility Bridge and Protocol Adapter.
* With Istio:
  * gRPC traffic can be routed via sidecars.
  * Only healthy pods receive traffic (readiness checks).
  * **Retries / circuit breaking** can protect Storage Service from overload.
## External access
* External access for UI, Data API and Flexibility hub simulator)
* Istio **IngressGateway** replaces your Ingress controller:
  * Expose **single public endpoint**.
  * Routing paths `/api`, `/ui`, `/simulator` → **VirtualService rules**.
  * TLS termination at the gateway.
  * Can integrate with **Keycloak** for auth.
## Observability and debugging
* [Istio](istio/README.md) adds
  * Metrics per service / per path.
  * Distributed tracing: track **Flexibility request through broker → services → back**.
  * Logs automatically include source/destination service info.
## Optional improvements
* **Traffic splitting**: test new versions of Protocol Adapter or Flexibility Bridge.
* **Rate limiting**: protect Message Broker from sudden spikes.
* **Fault injection**: simulate failures in HES Simulator or adapters.
## UML
<img src="images/istio.jpg">

<details>
  <summary>uml</summary>
 
**UML**
```uml
@startuml
!define RECTANGLE class

' Colors
skinparam rectangle {
  BackgroundColor White
  BorderColor Black
  Shadowing true
}

' Packages for visual grouping
package "Kubernetes Cluster - External Services" #ADD8E6 {
    RECTANGLE "UI App"
    RECTANGLE "Data API"
    RECTANGLE "Flex Hub Simulator"
}

package "Kubernetes Cluster - Internal Services" #90EE90 {
    RECTANGLE "Flexibility Bridge"
    RECTANGLE "Protocol Adapter"
    RECTANGLE "HES Simulator"
    RECTANGLE "Storage Service"
    RECTANGLE "Istio Service Mesh\n- mTLS\n- Retries / Circuit Breaking\n- Observability (Prometheus/Grafana)"

    ' Sidecars as separate color
    note right of "Flexibility Bridge" #D3D3D3 : Envoy sidecar
    note right of "Protocol Adapter" #D3D3D3 : Envoy sidecar
    note right of "HES Simulator" #D3D3D3 : Envoy sidecar
    note right of "Storage Service" #D3D3D3 : Envoy sidecar
}

package "Exposed Infrastructure (Outside Cluster)" #FFA07A {
    RECTANGLE "Istio IngressGateway"
}

package "External Systems" #F0E68C {
    RECTANGLE "Message Broker"
    RECTANGLE "Database"
}

' External access
"UI App" --> "Istio IngressGateway" : HTTPS
"Data API" --> "Istio IngressGateway" : HTTPS
"Flex Hub Simulator" --> "Istio IngressGateway" : HTTPS

' Routing
"Istio IngressGateway" --> "Data API" : /api
"Istio IngressGateway" --> "UI App" : /ui
"Istio IngressGateway" --> "Flex Hub Simulator" : /simulator

' Broker flow
"Flex Hub Simulator" --> "Message Broker" : publish requests/events (TLS)
"Message Broker" --> "Flexibility Bridge" : consume & store via Storage Service
"Message Broker" --> "Protocol Adapter" : consume & convert protocol
"Message Broker" --> "HES Simulator" : consume converted requests
"HES Simulator" --> "Message Broker" : publish responses (TLS)
"Message Broker" --> "Protocol Adapter" : consume responses & parse
"Message Broker" --> "Flexibility Bridge" : consume parsed responses & update Storage Service
"Flex Hub Simulator" --> "Message Broker" : consume final responses

' Storage service to Database
"Flexibility Bridge" --> "Storage Service" : gRPC write/update
"Protocol Adapter" --> "Storage Service" : gRPC read/write
"Storage Service" --> "Database" : persist / read data

' Data API direct connection to Database
"Data API" --> "Database" : direct read/write

@enduml
```

</details>
