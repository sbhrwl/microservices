# Layered responsibilities
- [Introduction](#introduction)
- [When API gateway helps](#when-api-gateway-helps)
- [Who handles what](#who-handles-what)
- [Setup for development](#setup-for-development)
- [Setup for production](#setup-for-production)
## Introduction
* Layered responsibilities
  * `LB vs Ingress vs API Gateway`
* **Rule of Thumb:** 
  * LB = Availability, 
  * Gateway = Control, 
  * Ingress = Internal routing.

| Layer                   | Role                      | Key Features                                           |
| ----------------------- | ------------------------- | ------------------------------------------------------ |
| **Load Balancer**       | Traffic distribution & HA | Layer 4/7 routing, health checks, failover             |
| **API Gateway**         | Application-level control | Auth, rate limiting, transformations, caching, logging |
| **Ingress Controller**  | Internal routing          | Path-based routing, TLS termination                    |
| **Reverse Proxy / CDN** | UI delivery               | TLS, caching, compression, API routing                 |

## When API gateway helps
* **Multiple external microservices** exposed through a single endpoint.
* **Rate limiting** per client/API key.
* **Auth at the edge** (OAuth, JWT).
* **Request transformations** (REST → gRPC, JSON → XML).
* **Centralized logging & analytics**.
* **Analogy:** 
  * LB = traffic cop
  * Ingress = traffic sign inside cluster
  * API Gateway = security checkpoint + concierge.
* `Question to ask`
  * Do i need advanced features like authentication, rate limiting, or request transformations at the edge? 
  * This usually drives whether an API gateway is necessary.
## Who handles what

| Feature                    | API Gateway  | Load Balancer | Ingress    |
| -------------------------- | ------------ | ------------- | ---------- |
| Auth & Authorization       | ✅            | ❌             | ⚠️ limited |
| Rate limiting              | ✅ per client | ⚠️ global     | ⚠️ basic   |
| Transformation             | ✅            | ❌             | ❌          |
| Caching                    | ✅            | ❌             | ⚠️ limited |
| Logging / Analytics        | ✅            | ⚠️ metrics    | ⚠️ basic   |
| TLS termination            | ✅            | ✅             | ✅          |
| Circuit breaking / retries | ✅            | ❌             | ❌          |

## Setup for development
* Pros: HA, routing, TLS
* **Cons**: Limited auth, rate limiting, transformations, logging
<img src="images/dev-setup.jpg">

<details>
  <summary>uml</summary>

**UML:**
```
@startuml
skinparam rectangle {
  BackgroundColor #F5F5F5
  BorderColor Black
}
    
package "Cluster Layer" {
    [Ingress Controller \n(NGINX)] as INGRESS #LightYellow
    package "Microservices" {
        [Service A] #LightPink
        [Service B] #LightPink
        [Service C] #LightPink
    }
}
INGRESS --> [Service A]
INGRESS --> [Service B]
INGRESS --> [Service C]
@enduml
```
</details>

## Setup for production
* Use **API Gateway for `public APIs`** requiring auth, rate limiting, transformations.
* Use **Ingress for `internal routing`** inside Kubernetes.
* Always place **load balancers** for HA in front of key layers.
* For UI outside cluster → **CDN + optional reverse proxy**.
* Avoid over-engineering; introduce API Gateway **only when necessary**.
<img src="images/prod-setup-1.jpg">

<details>
  <summary>uml</summary>

**UML:**
```
@startuml
skinparam rectangle {
  BackgroundColor #F5F5F5
  BorderColor Black
}
actor Client
package "External Layer (Public)" {
    [External LB] as ELB #LightBlue
}

package "External Layer (Private)" {
    [API Gateway] as APIGW #LightGreen
}

package "Cluster Layer" {
    [Cloud-managed \nIngress Controller \nprovisions \n**Internal load balancer**] as ILB #LightYellow
    package "Microservices" {
        [Service A] #LightPink
        [Service B] #LightPink
        [Service C] #LightPink
    }
}
Client --> ELB : "Global traffic distribution"
ELB --> APIGW : "Application-level control"
APIGW --> ILB : "Internal HA routing"
ILB --> [Service A]
ILB --> [Service B]
ILB --> [Service C]
@enduml
```
</details>

* This translates to
  - `Ingress Controller` shown inside the cluster (correctly).
  - `ILB is outside` but connected — it’s **provisioned by the Ingress Controller through the cloud API**.
  - Added `Ingress Resource` to show how routing happens within Kubernetes after the `ILB forwards traffic`.
  - `Client → ELB → API Gateway → Ingress Controller → ILB → Ingress Resource → Services`
<img src="images/prod-setup-2.jpg">

<details>
  <summary>uml</summary>

**UML:**
```
@startuml
skinparam rectangle {
  BackgroundColor #F5F5F5
  BorderColor Black
}

actor Client

package "External Layer (Public)" {
    [External Load Balancer (ELB)] as ELB #LightBlue
}

package "External Layer (Private)" {
    [API Gateway] as APIGW #LightGreen
}

package "Cluster Layer" {
    [Cloud-managed Ingress Controller] as IngressCtrl #LightYellow
    [Ingress Resource (routes traffic)] as IngressRes #White

    package "Microservices" {
        [Service A] #LightPink
        [Service B] #LightPink
        [Service C] #LightPink
    }
}

' Outside the cluster, but created by the ingress controller
[Internal Load Balancer (ILB)] as ILB #LightGray

' Connections
Client --> ELB : "Global traffic distribution"
ELB --> APIGW : "Application-level routing"
APIGW --> IngressCtrl : "Private traffic (internal endpoint)"
IngressCtrl --> ILB : "Provisions via cloud API"
ILB --> IngressRes
IngressRes --> [Service A]
IngressRes --> [Service B]
IngressRes --> [Service C]
@enduml
```
</details>
