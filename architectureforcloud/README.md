# [Architecture for cloud](FormatLater/README.md)
* [Governance and foundation](#governance-and-foundation)
* [Network and connectivity](#network-and-connectivity)
* [Service projects and workload hosting](#service-projects-and-workload-hosting)
* [Cloud service access and dependencies](#cloud-service-access-and-dependencies)
* [Automation and CICD enablement](#automation-and-cicd-enablement)
* [Observability and operations](#observability-and-operations)
* [Security and compliance](#security-and-compliance)
* [Environment request and onboarding workflow](#environment-request-and-onboarding-workflow)
## [Governance and foundation](governanceandfoundation/README.md)
* Core principles (security, scalability, automation, least privilege)
* Organizational structure (projects/environments, team boundaries, resource hierarchy)
* Landing zone / foundational environment overview
* Environment taxonomy (dev, test, prod, sandbox, shared services)
* Resource naming, labeling, and tagging standards
* Identity and access management strategy
* Policy controls, audit, and compliance framework
## [Network and connectivity](networkandconnectivity/README.md)
* Network architecture and isolation (VPC/subnet equivalents)
* Subnet/segment design (management, workloads, data, ingress/egress)
* Firewall and network security policies
* Load balancing and reverse proxy strategy
* DNS and naming strategy (internal/external)
* Certificate management (internal PKI, public CA)
* Connectivity options (hybrid, VPN, peering, private links)
* Routing and egress strategy
## [Service projects and workload hosting](serviceprojects/README.md)
* Environment/project linkage and access model
* Compute workloads (VMs, containers, serverless)
* Workload isolation and environment mapping
* Artifact and container registry concepts
* Service accounts and API access patterns
* Secrets management (vault, key management, rotation)
* Inbound access and traffic routing (internal/external)
* Access to cloud services (databases, storage, messaging, APIs)
## [Cloud service access and dependencies](cloudserviceaccess/README.md)
* General service categories required by applications (messaging, database, storage, pub/sub, external APIs)
* Access request and approval process for services
* Network and IAM considerations for service consumption
* Guardrails for usage, quotas, and connectivity
* Backup and disaster recovery strategy for data and service dependencies
  * Backups and recovery options
  * Regional redundancy and failover
  * DR testing and validation
## [Automation and CICD enablement](automationandcicd/README.md)
* Infrastructure as Code and environment provisioning
* CI/CD pipelines for app deployment
* Environment promotion workflows (dev → test → prod)
* Configuration drift detection and policy enforcement
* Policy-as-Code (compliance and governance)
## [Observability and operations](observabilityandoperations/README.md)
* Logging and metrics collection
* Monitoring and alerting strategies
* Tracing and performance profiling
* Operational runbooks and incident management
* Cost visibility and resource optimization
## [Security and compliance](securityandcompliance/README.md)
* Identity and access security
* Network security controls
* Data protection and encryption
* Secrets governance
* Vulnerability and threat management
* Compliance and regulatory frameworks
## [Environment request and onboarding workflow](environmentrequest/README.md)
* Prerequisites for app readiness
* Environment request process and workflow
* Review and approval gates
* Automated provisioning and validation
* Post-deployment checklist
* Ownership and lifecycle management
