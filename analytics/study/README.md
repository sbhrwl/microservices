# Data ingestion and storage architecture for Sensor data
## Overview
- The primary focus is to meet analytics requirements, ensure scalability, and prepare for expected data growth.
## Analysis

| **Category**                  | **Details**                      |
| ----------------------------- | -------------------------------- |
| **Database & Storage**        | - Current DB: Oracle (no partitioning, acceptable performance) <br> - Volume: 100k meters → \~500k rows/day <br> - Data retention: 90 days (Oracle) <br> - 10-min profile: \~14.4M rows/day <br> - 10-min data retention TBD (possibly 60 days) <br> - Open to migrating 10-min data to better storage solution |
| **Data Ingestion & Pipeline** | - Pipeline: Kafka → service → protocol conversion → enrichment → Oracle DB <br> - Bottlenecks observed: CPU, IO, memory under load <br> - No raw message persistence (no replay/audit support)|
| **Scalability & Performance** | - Current service is stateful and not horizontally scalable <br> - Target batch processing latency: ≤10 minutes <br> - Some tolerance for missing/late data|
| **Query & Access Patterns**   | - Initial queries: batch processing acceptable <br> - Real-time queries to be revisited later <br> - Queries pushed downstream; no current direct heavy query load|
| **Monitoring & Reliability**  | - Ingestion failure monitoring/alerting handled downstream <br> - No inbuilt observability or replay capability in ingestion flow|
| **Data Model & Growth**       | - Current sensor registers: 5 <br> - Expected growth to 15 registers <br> - Design must accommodate register growth|
| **Messaging System**          | - Messaging uses ActiveMQ <br> - Scaling strategy for registers via ActiveMQ undefined <br> - Future options: clustering, partitioning, load balancing|


## Solution options discussion

| **Category**                 | **Item**                                                                                          | **Decision**     |
| ---------------------------- | ------------------------------------------------------------------------------------------------- | ---------------- |
| **Ingestion & Buffering**    | 1. Open to decoupling ingestion with raw data storage in object storage (e.g., GCS).              | Data Lake        |
|                              | 2. Potential processing delay accepted to enable buffering and scalability.                       | Data Lake        |
|                              | 3. Idea to write a service that reads from ActiveMQ, converts protocol, saves to GCS or DB.       | Neutral          |
|                              | 4. Current service is stateful, enqueues converted data to another queue for DB saving.           | Neutral          |
| **Scalability & Resilience** | 1. Benefits include improved resilience, scalability, and flexibility for growing data volumes.   | Data Lake        |
| **Storage vs. Query Needs**  | 1. Evaluating time-series DBs (InfluxDB, TimescaleDB) vs data lake solutions (BigQuery, Iceberg). | Under Evaluation |
|                              | 2. Data mostly stored and passed downstream; minimal querying needs.                              | Data Lake        |
|                              | 3. Data transformed and pushed to analytics in required formats.                                  | Data Lake        |
|                              | 4. Downstream apps don’t require structured SQL-like access.                                      | Data Lake        |
|                              | 5. No enrichment or joins before handing off the data.                                            | Data Lake        |
|                              | 6. Preference to optimize for storage cost over query speed.                                      | Data Lake        |

## Summary
* Based on current needs, **Data Lake architecture** (e.g., Google Cloud Storage + Parquet + Iceberg or BigQuery) is the most suitable choice.
* Time-series DBs might be preferred if requirements shift to:
  * Frequent, low-latency queries on recent data.
  * Built-in rollups, aggregations, alerting.
  * Active dashboard use (Grafana, etc.).
  * Efficient retention and compression policies.
  * High-ingestion throughput with fast query performance.

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


## Store parquet files in GCS and expose them via BigQuery external tables
### 🆚 Why This Option Wins

| Option| Pros| Cons| Verdict|
| ----- | --- | --- | ------ |
| **1. Store in GCS (only)**                                  | ✅ Low cost  <br> ✅ Easy batch file access <br> ✅ Good for archival & delivery                   | ❌ Not queryable directly <br> ❌ Analytics app would need to download & process files              | ❌ Rejected — not suitable since the analytics team doesn't download files |
| **2. Store in BigQuery (only)**                             | ✅ Fast queries <br> ✅ Full SQL power <br> ✅ Simplifies analytics access                         | ❌ Higher cost (storage + streaming insert/ingest) <br> ❌ Not optimal for rare scans of large data | ❌ Rejected — overkill and costly for rare, simple queries                 |
| **✅ 3. Store in GCS + expose via BigQuery external tables** | ✅ Low storage cost (GCS) <br> ✅ On-demand querying via BigQuery <br> ✅ No ingestion duplication | ❌ Slightly slower than native BQ tables <br> ❌ Requires schema to be consistent across files      | ✅ **Chosen** — best balance of cost, simplicity, and functionality        |

- This setup:
  * lets you keep cheap, batch-optimized storage in GCS,
  * avoids downloading files by enabling SQL access via BigQuery,
  * and supports your current usage: stable schema, small data, rare queries.

### Security
- We don't need an API Gateway in front of BigQuery.** Here's why:
  - BigQuery Already Handles Access Control:
    * You can **grant IAM permissions** (like `bigquery.dataViewer`) to the analytics vendor's service account.
    * You can **restrict access to specific datasets, tables, even columns**.
    *  BigQuery **authenticates via Google Cloud IAM** and supports **fine-grained audit logging**.
  - Why Not API Gateway?
    * API Gateway is for **controlling HTTP requests to APIs** — not a natural fit for SQL workloads.
    * Putting an API Gateway in front of BigQuery would:
      * Add **latency**,
      * Require you to **proxy SQL queries or REST API calls manually**,
      * Duplicate features already provided by **IAM and VPC Service Controls**.
- Better option:
  - If you want to restrict **network access**:
    - Use **Private Service Connect (PSC)** or **VPC-SC (Service Controls)** to limit where BigQuery can be accessed from (e.g., only from specific VPCs or IPs).
### Next steps
- Which of these would you like to address first?
- Design the microservice that generates and uploads data to GCS
- Structure your GCS bucket and file paths
- Define BigQuery external tables over Parquet files
- IAM permissions for analytics vendor
- Or a Private Service Connect flow if needed
