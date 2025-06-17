#

## Analysis

| **Category**                  | **Details**                                                                                                                                                                                                                                                                                              |
| ----------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Database & Storage**        | - Current DB: Oracle (no partitioning, acceptable performance)  <br> - Volume: 100k meters → \~500k rows/day <br> - Data retention: 90 days (Oracle) <br> - 10-min profile: \~14.4M rows/day <br> - 10-min data retention: TBD (possibly 60 days) <br> - Open to migrating 10-min data to better storage |
| **Data Ingestion & Pipeline** | - Pipeline: Kafka → service → protocol conversion → enrichment → Oracle DB <br> - Bottlenecks observed: CPU, IO, memory under load <br> - No raw message persistence (no replay/audit support)                                                                                                           |
| **Scalability & Performance** | - Current service is stateful and not horizontally scalable <br> - Target batch processing latency: ≤10 minutes <br> - Some tolerance for missing/late data                                                                                                                                              |
| **Query & Access Patterns**   | - Initial queries: batch processing acceptable <br> - Real-time queries to be revisited later <br> - Queries are pushed downstream; no current direct load                                                                                                                                               |
| **Monitoring & Reliability**  | - Ingestion failure monitoring/alerting handled downstream <br> - No inbuilt observability/replay capability in ingestion flow                                                                                                                                                                           |
| **Data Model & Growth**       | - Current sensor registers: 5; expected growth: 15 <br> - Design must accommodate register growth                                                                                                                                                                                                        |
| **Messaging System**          | - Messaging uses ActiveMQ (not Kafka) <br> - Scaling strategy for registers via ActiveMQ is not yet defined <br> - Clustering/partitioning/load balancing may be introduced                                                                                                                              |


## Solution Options Discussion
1. Open to decoupling ingestion with raw data storage in object storage (e.g., GCS).
2. Potential processing delay to enable buffering and scalability.
3. Benefits include resilience, scalability, and flexibility for growing data volume.
4. Idea to write a service that reads from ActiveMQ, does protocol conversion, and saves to GCS or DB.
5. Current service is stateful and enqueues converted data to another queue for DB saving.

6. Exploring whether to use time-series DB (InfluxDB, TimescaleDB) or data lake (BigQuery, Iceberg).
7. Data is mostly stored and passed downstream; minimal querying.
8. Data is transformed and pushed to analytics in required formats.
9. Downstream apps don’t require structured SQL-like access.
10. No enrichment or joins needed before handing off the data.
11. Preference is to optimize for storage cost over query speed.
