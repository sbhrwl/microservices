# Scalability perspective
- [ActiveMQ to Ingestion microservice](#activemq-to-ingestion-microservice)
- [Ingestion microservice to Parquet file generation](#ingestion-microservice-to-parquet-file-generation)
- [Parquet file generation to GCS upload](#parquet-file-generation-to-gcs-upload)
- [GCS upload to BigQuery external tables](#gcs-upload-to-bigquery-external-tables)
- [BigQuery external tables to Analytics application](#bigquery-external-tables-to-analytics-application)
- [Overall system scalability recommendations](#overall-system-scalability-recommendations)
## ActiveMQ to Ingestion microservice
- **Scalability Factors:**
  * **Broker throughput:** ActiveMQ supports vertical scaling but can become a bottleneck at high throughput.
  * **Consumer concurrency:** The ingestion microservice must scale horizontally to consume from multiple queues or topics.
- **Recommendations:**
  * Use **ActiveMQ Artemis** or evaluate Kafka if message volume is high.
  * Enable **prefetch tuning** and **consumer concurrency control**.
  * Use **horizontal pod autoscaling (HPA)** for the ingestion microservice based on queue depth or CPU.
## Ingestion microservice to Parquet file generation
- **Scalability Factors:**
  * **CPU/memory intensive:** Generating Parquet files (especially batch compression) is resource-heavy.
  * **Concurrency risks:** If multiple files are written simultaneously, you risk I/O contention or race conditions.
- **Recommendations:**
  * Use **dedicated worker pools** to offload Parquet generation.
  * Write to a **distributed filesystem** or use memory-efficient streaming writes.
  * **Sharding** by message type, customer, or time bucket helps distribute the load.
## Parquet file generation to GCS upload
- **Scalability Factors:**
  * **Parallelism support:** GCS scales extremely well for concurrent writes.
  * **Network bandwidth:** Upload throughput is limited by egress capacity from your nodes.
- **Recommendations:**
  * Use **multi-threaded upload** and **chunked writes**.
  * Employ **upload queues** with retry logic.
  * If running in GKE or Compute Engine, place workers in the same region/bucket location for optimal performance.
## GCS upload to BigQuery external tables
- **Scalability Factors:**
  * **Metadata latency:** External tables don’t perform as well as native tables under heavy query loads.
  * **Concurrent query access:** Accessing large external files by multiple users causes I/O bottlenecks.
- **Recommendations:**
  * For high-frequency queries, consider a **scheduled load into native BigQuery tables**.
  * Use **partitioned files and folder structures (e.g., /year/month/day)** to reduce scan scope.
  * Limit external table usage to near-real-time or exploratory workloads.
## BigQuery external tables to Analytics application
- **Scalability Factors:**
  * **Query concurrency limits:** BigQuery has quotas on slots and concurrent jobs.
  * **Client-side resource usage:** Pull-based systems can get overwhelmed if backpressure isn’t implemented.
- **Recommendations:**
  * **Use BI Engine caching** or **materialized views** for repeated queries.
  * Implement **query cost controls** and rate limits for clients.
  * Use **streaming inserts** if the pipeline evolves to real-time.
## Overall system scalability recommendations

| Category               | Recommendation                                                                                                     |
| ---------------------- | ------------------------------------------------------------------------------------------------------------------ |
| **Elasticity**         | Use Kubernetes autoscaling for all compute-heavy microservices.                                                    |
| **Storage**            | Design GCS and Parquet file layout to support parallel reads/writes.                                               |
| **Monitoring**         | Use Cloud Monitoring + custom metrics to trigger autoscaling and alerting.                                         |
| **Throughput testing** | Load test each stage (e.g., ingestion rate, upload speed, query concurrency).                                      |
| **Decoupling**         | Keep each stage asynchronous (e.g., queue → batch → upload) for better failure isolation and backpressure control. |
