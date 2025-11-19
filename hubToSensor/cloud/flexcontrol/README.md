# Flexibility control
- [Application overview](#application-overview)
- [Software frameworks](#software-frameworks)
- [Logical architecture](#logical-architecture)
- [Project structure](#project-structure)
- [Networking](#networking)
## Application overview
- The Grid Flex Control application will be developed using Angular, GraphQL and Java.
- The application will be containerized to run as private GKE cluster in Google Cloud Platform.
<img src="images/app-overview.jpg">

 * **User interface**
  * Frontend for user interaction
  * Communicates with GraphQL API
* **GraphQL**
  * Acts as API layer
  * Orchestrates data between UI and GFC service
* **GFC service**
  * Core backend logic handler
  * Interacts with NoSQL database
  * Exchanges data with Flex Control Service and IEC Adapter
* **Flex control service**
  * Business logic for flex control operations
  * Communicates with both GFC service and IEC Adapter
  * Persists data to SQL database
* **IEC adapter**
  * Protocol adapter (IEC-61968-9) for external communication
  * Connects Flex Control Service with external Head End System
* **Databases**
  * **SQL:** stores structured operational data
  * **NoSQL:** stores unstructured data for GFC service
## Software frameworks
- The Grid Flex Control application will be developed using these frameworks and tools:
  -	Frontend - AngularJS, HTML5, Nginx gateway
  -	Backend - Java, Restful APIs
  -	Storage – PostgreSQL, MongoDB
  -	CI/CD – GitLab, Docker, Helm Charts
## Logical architecture
- To support the requirements for Grid Flex Control, the diagram demonstrates the logical architecture.
<img src="images/app-architecture.jpg">

## Project structure
- Each deployment (either staging, hosted for a specific tenant, or a multi-tenant solution) consists of two projects, as shown in logical architecture.
- The projects are designed with considerations for IAM, cost management and operations.
- Specific considerations include security boundaries, exposure to the internet, costs relating to specific types of GCP resources, CI/CD pipelines and related manual security and operation processes.

| Project logical index in iac       | Project root name | Note                                                        |
| ---------------------------------- | ----------------- | ----------------------------------------------------------- |
| Grid Flex Control Service project  | srv               | Grid Flex Control services in GKE and database in Cloud SQL |
| Grid Flex Control Security project | scr               | Secret Manager, Artifact Registry and KMS                   |

- **Required GCP APIs**

| API                                | srv | scr |
| ---------------------------------- | :-: | :-: |
| Compute Engine                     |  x  |     |
| Google Kubernetes Engine           |  x  |     |
| Cloud SQL                          |  x  |     |
| Google Cloud Storage               |  x  |     |
| Secret Manager                     |     |  x  |
| Artifact Registry                  |     |  x  |
| Cloud Key Management Service (KMS) |     |  x  |
| Cloud Logging                      |  x  |  x  |
| Cloud Monitoring                   |  x  |  x  |
| Container Analysis                 |  x  |  x  |
| Container Security                 |  x  |     |

## Networking
- The networking currently proposed is shown in the following diagram.
<img src="images/networking.jpg">
