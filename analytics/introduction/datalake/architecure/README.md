# Architecture
- [Overview and purpose](#overview-and-purpose)
- [End to end pipeline](#end-to-end-pipeline)
- [Data delivery design](#data-delivery-design)
- [Security and IAM model](#security-and-iam-model)
- [Next steps](#next-steps)
- [Evaluation of plan](#evaluation-of-plan)
  - [Minor suggestions](#minor-suggestions)
- [Final summary](#final-summary)
## Overview and purpose
- The primary focus is to meet analytics requirements, ensure scalability, and prepare for expected data growth.
- This microservice handles ingestion of 10-minute profile data from field sensors and makes it accessible to the analytics team through GCS and BigQuery external tables.
- Data flow:
  - Collect raw 10-minute data from sensors (via ActiveMQ).
  - Convert it into analytics-friendly Parquet format.
  - Upload files to Google Cloud Storage (GCS).
  - Expose data via BigQuery external tables for analytics applications.

## End to end pipeline
```
[ActiveMQ] --> [Ingestion Microservice]
[Ingestion Microservice] --> [Parquet File Generation]
[Parquet File Generation] --> [GCS Upload]
[GCS Upload] --> [BigQuery External Tables]
[BigQuery External Tables] --> [Analytics Application (Pull-Based)]
```

## Data delivery design

| **Category**                | **Details**                                                                                                                           |
| --------------------------- | ------------------------------------------------------------------------------------------------------------------------------------- |
| **Data Requirements**       | - Analytics team needs raw 10-min data delivered regularly; they handle queries and analysis.                                         |
|                             | - Focus on reliable, scalable storage and timely delivery; query performance is less critical.                                        |
| **Delivery Approach**       | - Pull-based data delivery preferred; analytics team pulls files on their schedule.                                                   |
|                             | - Files batched every 30 minutes to balance freshness and overhead.                                                                   |
| **Data Volume & File Size** | - Estimate: 100k devices × 20 registers every 10 minutes.                                                                             |
|                             | - Single large files per 30 minutes are too heavy; split into smaller chunks (e.g., per register, device group, or 10-min intervals). |
| **Storage & Access**        | - Cloud object storage (GCS/S3) chosen for hosting files.                                                                             |
|                             | - Secure access through API gateway handling authentication, logging, throttling, and retries.                                        |
| **File Format & Schema**    | - Parquet chosen for compactness and analytics friendliness.                                                                          |
|                             | - Schema evolution planned to support more registers in the future.                                                                   |
|                             | - No schema versioning in metadata or file path planned currently.                                                                    |
| **File Organization**       | - File path pattern: `/meter-data/{register}/yyyy/MM/dd/HH/mm/part-xxxx.parquet`.                                                     |
|                             | - Files will always include **all registers** every 10 minutes (no delta filtering).                                                  |
| **Metadata**                | - Separate `.json` metadata files per Parquet chunk, containing:                                                                      |
|                             | - Device count <br>   - Timestamp range <br>   - Register list <br>   - Generation time                                               |
|                             | - Manifest or index files per time period (e.g., daily) for easy file discovery.                                                      |
| **Upload & Reliability**    | - Service uploads files directly to GCS.                                                                                              |
|                             | - Upload failures handled with automatic retries; no manual queuing outside ActiveMQ.                                                 |
|                             | - ActiveMQ used for message queuing; no upload status pushed back to MQ.                                                              |
| **Monitoring**              | - Basic logs on service host; no centralized logging yet.                                                                             |
|                             | - Future enhancements may include metrics, dashboards, and alerting.                                                                  |

## Security and IAM model
- We don't need an API Gateway in front of BigQuery. Here's why:
  - BigQuery Already Handles Access Control:
    * You can **grant IAM permissions** (like `bigquery.dataViewer`) to the analytics vendor's service account.
    * You can **restrict access to specific datasets, tables, even columns**.
    * BigQuery **authenticates via Google Cloud IAM** and supports **fine-grained audit logging**.
  - Why Not API Gateway?
    * API Gateway is for **controlling HTTP requests to APIs** — not a natural fit for SQL workloads.
    * Putting an API Gateway in front of BigQuery would:
      * Add **latency**,
      * Require you to **proxy SQL queries or REST API calls manually**,
      * Duplicate features already provided by **IAM and VPC Service Controls**.
- Better option:
  - If you want to restrict **network access**:
    - Use **Private Service Connect (PSC)** or **VPC-SC (Service Controls)** to limit where BigQuery can be accessed from (e.g., only from specific VPCs or IPs).

## Next steps
- Design the microservice(s) that:
  - Consumes messages from ActiveMQ.
  - Generates Parquet files.
  - Uploads Parquet files to GCS.
- Structure your GCS bucket and file paths:
  - **File format and schema**
    - Parquet chosen for compactness and analytics friendliness.
    - Schema evolution planned to support more registers in the future.
    - No schema versioning in metadata or file path planned currently.
  - **File organization**
    - File path pattern: `/meter-data/{register}/yyyy/MM/dd/HH/mm/part-xxxx.parquet`.
    - Files will always include **all registers** every 10 minutes (no delta filtering).
  - **Metadata**
    - Separate `.json` metadata files per Parquet chunk, containing:
      - Device count.
      - Timestamp range.
      - Register list.
      - Generation time.
    - Manifest or index files per time period (e.g., daily) for easy file discovery.
  - Upload failures handled with automatic retries.
- Define BigQuery external tables over Parquet files.
- IAM permissions for analytics vendor.
- Or a Private Service Connect flow if needed.

## Evaluation of plan
| Area                     | Review                                                                                                                                            |
| ------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Data Flow**            | Clear end-to-end flow: `ActiveMQ → Microservice → Parquet → GCS → BigQuery External Table`. Matches your architecture and separation of concerns. |
| **File Format**          | Parquet is the right choice — compact, analytics-optimized, and forward-compatible.                                                               |
| **Schema Evolution**     | Anticipates register growth — avoids rigid assumptions.                                                                                           |
| **File Path Convention** | `/meter-data/{register}/yyyy/MM/dd/HH/mm/part-xxxx.parquet` is clean, partitionable, and BigQuery-friendly.                                       |
| **Metadata Handling**    | JSON sidecar files + manifest/index files make downstream ingestion/discovery easy. Future-proof and flexible.                                    |
| **Reliability**          | Automatic retry strategy + no dependency on manual status pushbacks. Great for scalable pipelines.                                                |
| **Access Model**         | IAM for vendor access + external tables avoids unnecessary API layers. Minimal coupling, high control.                                            |

### Minor suggestions
| Area                             | Comment                                                                                                        |
| -------------------------------- | -------------------------------------------------------------------------------------------------------------- |
| **No Schema Versioning in Path** | Okay for now. If schema changes become disruptive (e.g., field type change), consider adding versioning later. |
| **All Registers Every 10 Min**   | Efficient for now, but if payloads become large in future, you may revisit delta-based design.                 |
| **Manifest Granularity**         | Daily is fine for now. If ingestion SLA tightens, consider hourly manifests later.                             |

## Final summary
- You're on the **right track** with a robust and scalable architecture:
  * Decoupled,
  * Storage-optimized,
  * Minimal operational overhead,
  * Future-ready for analytics team needs.
