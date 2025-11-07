# Kubernetes service account
* The application runs on a Kubernetes cluster and connects to multiple cloud services: Secret Manager, Certificate Manager, Database, Pub/Sub, and S3.
* A dedicated **Kubernetes Service Account (KSA)** will be created for the application.
* This KSA will be **mapped via Workload Identity** to a single **GCP IAM Service Account** that represents the application in the cloud.
* The IAM Service Account will be granted the following roles:
  * **Secret Manager:** `roles/secretmanager.secretAccessor`
  * **Certificate Manager:** `roles/certificatemanager.viewer`
  * **Database (Cloud SQL):** `roles/cloudsql.client`
  * **Pub/Sub:** `roles/pubsub.publisher`, `roles/pubsub.subscriber`
  * **S3 / Cloud Storage:** `roles/storage.objectAdmin`
* This IAM Service Account will provide unified access control across all required cloud resources.
* The application uses **Dapr** for service integration and messaging. Dapr does **not** change or add to the identity or access model; it uses the same credentials provided through the mapped IAM Service Account.
```mermaid
flowchart TB

%% ===========================
%% ENTRY LAYER
%% ===========================
A["User Interface\nStatic from GCS, Dynamic via APIs"]
B["Load Balancer / Ingress"]
C["API Gateway\nThird-Party and External API Access"]

A --> B --> C --> N1

%% ===========================
%% APPLICATION LAYER
%% ===========================
subgraph N1["Namespace: app-services"]
    direction TB
    S1["K8s Service: service-a"] --> P1["Pod A\nApp + Dapr"]
    S2["K8s Service: service-b"] --> P2["Pod B\nApp + Dapr"]
    S3["K8s Service: service-c"] --> P3["Pod C\nApp + Dapr"]

    P1 <--> P2
    P2 <--> P3
end

%% ===========================
%% SECURITY & DATA DEPENDENCIES
%% ===========================
subgraph EXTERNAL["External Dependencies"]
    direction TB
    F1["Secret Manager"]
    F2["Certificate Manager"]
    E1["MongoDB Atlas"]
    E2["Cloud SQL (PostgreSQL)"]
    E3["Google Cloud Storage"]
    E4["Pub/Sub"]
    G1["Artifact Registry"]
    G2["Cloud Logging & Monitoring"]
    G3["IAM / Workload Identity"]
end

%% ===========================
%% CLUSTER SYSTEM COMPONENTS
%% ===========================
subgraph N2["Namespace: kube-system"]
    direction TB
    CM1["Core DNS"]
    CM2["cert-manager Controller"]
    CM3["Dapr Control Plane"]
end

%% ===========================
%% LOGICAL CONNECTIONS
%% ===========================
N1 --> EXTERNAL
N1 --> N2
```
