# Security perspective
- [Aspects](#aspects)
- [ActiveMQ to Ingestion microservice](#activemq-to-ingestion-microservice)
- [Ingestion microservice to Parquet file generation](#ingestion-microservice-to-parquet-file-generation)
- [Parquet file generation to GCS upload](#parquet-file-generation-to-gcs-upload)
- [GCS upload to BigQuery external tables](#gcs-upload-to-bigquery-external-tables)
- [BigQuery external tables to Analytics application](#bigquery-external-tables-to-analytics-application)
- [General hardening recommendations](#general-hardening-recommendations)
## Aspects
* **Authentication and authorization**
* **Data integrity and confidentiality**
* **Network security**
* **Auditability and logging**
* **Least privilege access**
## ActiveMQ to Ingestion microservice
- **Security concerns:**
  * Unauthorized access to queues
  * Spoofed messages
  * Lack of encryption in transit
- **Recommendations:**
  * **Use TLS** to secure connections to ActiveMQ (SSL for broker and client).
  * **Enable authentication** using JAAS or external LDAP.
  * **Implement ACLs** on queues (e.g., producers/consumers).
  * **Validate message content** and structure in the microservice to prevent injection or malformed payloads.
## Ingestion microservice to Parquet file generation
- **Security Concerns:**
  * Sensitive data leakage to disk
  * Local filesystem exposure
  * Insecure temp file handling
- **Recommendations:**
  * Store generated files in **ephemeral or encrypted volumes** (e.g., tmpfs or encrypted local disks).
  * Avoid writing raw payloads; **sanitize and validate data before writing**.
  * Set proper **file permissions** (`chmod 600`) and use a dedicated service account.
  * **Log file access and generation** events.
## Parquet file generation to GCS upload
- **Security Concerns:**
  * Data interception during upload
  * Incorrectly scoped credentials
  * Public bucket exposure
- **Recommendations:**
  * Use **signed URLs or service account credentials with minimal scope** (only `storage.objects.create`).
  * **Ensure HTTPS is enforced** for GCS uploads.
  * **Encrypt data at rest** in GCS (default in GCP).
  * **Use Object Lifecycle Management** to delete temp files quickly.
## GCS upload to BigQuery external tables
- **Security Concerns:**
  * Exposure of raw data via BigQuery
  * Use of shared or overly permissive datasets
  * Persistent links to files that might change
- **Recommendations:**
  * **Restrict GCS bucket access** to only the BigQuery service account.
  * Use **IAM Conditions** to limit access based on time/IP/user.
  * **Apply BigQuery Row-Level Security (RLS)** if user-specific access is needed.
  * Monitor **access logs on both GCS and BigQuery**.
## BigQuery external tables to Analytics application
- **Security Concerns:**
  * Unauthorized access to analytics data
  * Data exfiltration from dashboards
  * Poor query controls leading to cost spikes or DDoS
- **Recommendations:**
  * Use **OAuth2 or IAM-based service accounts** for the Analytics app.
  * Implement **query cost quotas** and rate limiting.
  * Use **parameterized queries** and **views** to restrict data access.
  * Enable **Cloud Audit Logs** and monitor for anomalous queries or access patterns.
## General hardening recommendations

| Area                   | Recommendation                                                                                           |
| ---------------------- | -------------------------------------------------------------------------------------------------------- |
| **Secrets Management** | Use Secret Manager or environment variables with Kubernetes secrets, **never hard-code** credentials.    |
| **Least Privilege**    | Each service/component must have **its own identity** with **only required permissions**.                |
| **Monitoring**         | Use **Cloud Audit Logs**, **VPC Flow Logs**, and **Cloud Armor** (if behind HTTPS LB) for visibility.    |
| **Incident Response**  | Implement **alerts for unauthorized access**, unusual file movement, or excessive queries.               |
| **Compliance**         | If handling PII or financial data, ensure alignment with **GDPR**, **SOC2**, or **ISO 27001** standards. |

