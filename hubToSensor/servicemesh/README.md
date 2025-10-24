# [Service mesh](istio/README.md)
- [Introduction](#introduction)
  - [Internal services and TLS traffic](#internal-services-and-tls-traffic)
  - [External access](#external-access)
  - [Observability and debugging](#observability-and-debugging)
  - [Additional capabilities](#additional-capabilities)
- [Plan](#plan)
- [Design](#design)
- [Restart Istio pods](#restart-istio-pods)
## Introduction 
### Internal services and TLS traffic
* **Message Broker ↔ Flexibility hub simulator/ Flexibility Bridge / Protocol Adapter / HES Simulator**
  * Istio can **automatically inject Envoy sidecars** into all services.
  * **mTLS**: all TLS traffic between services can be managed by Istio. 
    * **`No need`** to handle custom TLS in your services.
  * **Service discovery**: broker and clients locate each other via **service names** (K8s DNS) 
    * **`No hardcoded endpoints`**.
* **Storage service**
  * Accessed by Flexibility Bridge and Protocol Adapter.
  * With Istio:
    * gRPC traffic can be routed via sidecars.
    * Only healthy pods receive traffic (readiness checks).
    * **Retries / circuit breaking** can protect Storage Service from overload.
### External access
* External access for UI, Data API and Flexibility hub simulator)
* Istio **IngressGateway** replaces your Ingress controller:
  * Expose **single public endpoint**.
  * Routing paths `/api`, `/ui`, `/simulator` → **VirtualService rules**.
  * TLS termination at the gateway.
  * Can integrate with **Keycloak** for auth.
### Observability and debugging
* Istio adds
  * Metrics `per service` / `per path`.
  * Distributed tracing: **`track Flexibility request through broker → services → back`**.
  * Logs automatically include source/destination service info.
### Additional capabilities 
* **Traffic splitting**: test new versions of Protocol Adapter or Flexibility Bridge.
* **Rate limiting**: protect Message Broker from sudden spikes.
* **Fault injection**: simulate failures in HES Simulator or adapters.
## Plan

| Step | Focus/Tool | Goal | Implementation Change |
|---|---|---|---|
| [1. Istio Installation](istiosetup/README.md) | Helm (istio/base, istio/istiod) | Install the Istio Control Plane (Istiod) using the official Helm charts. | Use helm repo add and helm install commands to deploy to istio-system namespace. |
| [2. Application onboarding](applicationonboarding/README.md) | Helm Chart Values / Namespace | Enable automatic Envoy sidecar injection for your application. | Add the label istio-injection: enabled to your application's namespace (or Deployment annotations) and re-deploy/upgrade your service via Helm. |
| [3. External Access (Ingress)](externalaccess/README.md) | Istio Gateway | Define the mesh's public entry point using an Istio resource instead of a standard Ingress. | Folder/File: Create a separate manifest for the Gateway resource, pointing to the default istio-ingressgateway Service. |
| 4. Traffic Routing | Istio VirtualService | Route external traffic from the Gateway to your application service. | Folder/File: Create a VirtualService to link the new Gateway to your existing Kubernetes Service. |
| 5. Observability | Istio Add-ons / istioctl | Access the Service Mesh visualizer (Kiali). | Use kubectl apply -f to deploy the add-ons and istioctl dashboard kiali to launch the UI. |

- Pods still running after [application `off`bording](applicationonboarding/README.md)
```
PS C:\Git\microservices\hubToSensor\servicemesh\externalaccess> kubectl get pods -n istio-system
NAME                                   READY   STATUS    RESTARTS   AGE
istio-ingressgateway-797bbf485-pvbd2   1/1     Running   0          12h
istiod-6cdb654854-hs2q2                1/1     Running   0          18h
PS C:\Git\microservices\hubToSensor\servicemesh\externalaccess> kubectl get pods -n ingress-nginx
NAME                                        READY   STATUS    RESTARTS        AGE
ingress-nginx-controller-7d8cffd99c-rqz6d   1/1     Running   1 (2d11h ago)   2d13h
```
## Design 
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

## Restart Istio pods
- **(Scale Down/Up):**
  * If you want to stop them completely and bring them back later, you should scale their controlling **Deployments** down to 0 replicas, and then back up to 1 (or the desired count). This is the proper way to pause a service.
  * **Scale Down:**
    ```bash
    kubectl scale deployment istio-ingressgateway -n istio-system --replicas=0
    kubectl scale deployment istiod -n istio-system --replicas=0
    ```

  * **Scale Up (to restart later):**
    ```bash
    kubectl scale deployment istio-ingressgateway -n istio-system --replicas=1
    kubectl scale deployment istiod -n istio-system --replicas=1
    ```
