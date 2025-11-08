# Architecting for Cloud
* [Goal](#goal)
* [Guiding principles](#guiding-principles)
* [Overview](#overview)
* [Host project](#host-project)
* [Networking and shared VPC](#networking--shared-vpc)
* [Service project lifecycle](#service-project-lifecycle)
* [Secrets and configuration management](#secrets--configuration-management)
* [Access and connectivity](#access--connectivity)
* [Deployment workflow](#deployment-workflow)
* [Cloud readiness checklist](#cloud-readiness-checklist)
* [Environment request process](#environment-request-process)
## Goal
* **Pre-deployment readiness criteria** for apps.
* **Operational best practices** for post-deployment.
## Guiding principles
* **Cloud-native mindset:** scalability, resilience, and automation.
* **Security first:** zero trust, least privilege, mTLS for service-to-service communication.
* **Separation of concerns:** isolate management, networking, and workloads.
* **Automation:** infrastructure as code (Terraform), repeatable deployments.
* **Observability:** logging, monitoring, tracing from day one.
## Overview
* Control and policy management centralized in host project.
* Shared VPC managed by networking project.
* Service projects consume shared subnets and host workloads.
* Vault and CI/CD provide operational automation and secure handling.
* **Flow:**
  * `host (landing zone)` → `networking project (shared VPC)` → `service projects (applications)`
```mermaid
flowchart TD
    A[Landing Zone - Host Project] --> B[Networking Project - Shared VPC]
    B --> C[Service Project 1 - App Workloads]
    B --> D[Service Project 2 - App Workloads]
    C --> E[Vault and Secrets Management]
    C --> F[CI/CD Pipelines]
    D --> E
    D --> F
```

```mermaid
classDiagram
    class ServiceProject {
        +string name
        +string owner
        +deploy()
    }
    class VPC {
        +string cidr
        +list subnets
    }
    class Vault {
        +storeSecret()
        +retrieveSecret()
    }
    ServiceProject --> VPC : uses
    ServiceProject --> Vault : accesses
```
### Example
* [Creating service project](serviceproject/README.md)
  * [Configure Hashicorp vault to persist secrets in Cloud SQL](secrets/README.md)
  * [Accessing services running in service project via Island browser](islandbrowser/README.md)
* [Configure web app url to use Internal DNS and certificates signed with public root CA](internalDNS/README.md)
* [Accessing web application via Island browser](useraccesstoapplication/README.md)
* [Deployment process](deployment/README.md)
* [Alternate design](alternatedesign/README.md)
## Host project
* Host project aka landing zone
  * Central authority for organization-level configuration.
  * Manages IAM boundaries, firewall baselines, VPC definitions.
  * Enforces organization policies and service control.
  * Hosts cross-project services (logging, monitoring, billing).
* **Outcome:** consistent governance and baseline security.
## Networking and shared vpc
* Networking project owns VPCs, subnets, and routing.
* Shared VPC exposes subnets to service projects.
* Centralized firewall and routing policies.
**Outcome:** scalable, secure, and compliant connectivity.
```mermaid
sequenceDiagram
    participant AppTeam
    participant PlatformTeam
    participant VPC
    AppTeam->>PlatformTeam: Request network resources
    PlatformTeam->>VPC: Configure subnets and firewall
    PlatformTeam-->>AppTeam: Subnets and access details
```
### Takeaways
- **Host project aka Landing zone**
  - `Network admin`, enables to centrally manage VPCs, firewall rules, and **IAM settings** across networking projects
- **Networking project**
  - Consists of VPC, subnets and firewall rules.
  - `Shared VPC` is enabled on the networking project.
- **Service project**
  - Service project is linked to networking project.
  - Hosts the application workloads such as VMs and Cloud SQL.
* **Shared VPC**: This allows your service project to `use the subnets` defined in your networking project.
## Service project lifecycle
* Linked to networking project to consume shared subnets.
* Hosts application workloads: compute, containers, databases.
* Isolated for security and billing but aligned through shared standards.
* **Lifecycle stages:**
  * Provisioning via [Terraform templates](link-to-IaC-repo)
  * Deploy workloads using [CI/CD pipeline guide](link-to-CICD-guide)
  * Integrate with [Vault](link-to-vault-guide) and [observability stack](link-to-observability-guide)
```mermaid
sequenceDiagram
    participant Dev as Developer
    participant CI as CI/CD
    participant Service as Service Project
    Dev->>CI: Commit code
    CI->>Service: Deploy workloads
    Service-->>CI: Deployment result
```

## Secrets and configuration management
* **Vault + Cloud SQL** integration for secret persistence and access control.
* Dynamic secrets wherever possible (short-lived tokens, ephemeral credentials).
* Environment-specific policies tied to IAM roles.
* Consistent bootstrap pattern for new projects.
```mermaid
flowchart LR
    A[App] --> B[Vault]
    B --> C[Cloud SQL]
    A --> D[CI/CD Pipeline]
```

## Access and connectivity
* **Internal access:** Island browser → internal DNS → service endpoint.
* TLS certificates signed by public CA for internal DNS.
* **Service access:** via mTLS, service accounts, or IAM-based permissions.
* Developer/admin access through secure bastion or Identity-Aware Proxy (IAP).
```mermaid
flowchart TD
    Dev[Developer] -->|Access via IAP| App[Application]
    App -->|TLS and mTLS| Service[Service Project]
```

## Deployment workflow
* **CI/CD orchestration:**
  * IaC (Terraform) to create and configure projects.
  * Pipeline stages: build → test → deploy → verify.
  * Canary or blue-green deployments.
  * Rollback and observability hooks.
* **Recommended tooling:** GitHub Actions or Cloud Build + Terraform Cloud.
```mermaid
sequenceDiagram
    participant Repo
    participant CI
    participant Service
    Repo->>CI: Push code
    CI->>Service: Deploy infrastructure
    CI->>Service: Deploy application
    Service-->>CI: Report deployment status
```

## Environment request process
```mermaid
sequenceDiagram
    participant Dev as Developer
    participant Platform as Platform Team
    participant VPC as Networking Project
    participant Vault as Secrets Manager
    participant CI as CI/CD Pipeline

    Dev->>Platform: Submit environment request
    Platform->>VPC: Provision subnets and firewall
    Platform->>Vault: Configure secrets access
    Platform-->>Dev: Provide environment details
    Dev->>CI: Deploy application to environment
    CI->>Vault: Retrieve secrets dynamically
    CI->>VPC: Connect services via shared VPC
    CI-->>Dev: Deployment status
```
