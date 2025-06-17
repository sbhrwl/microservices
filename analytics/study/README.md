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

| Category | Item | Decision |
|----------|------|----------|
| **Ingestion & Buffering** | 1. Open to decoupling ingestion with raw data storage in object storage (e.g., GCS). | Data Lake |
| | 2. Potential processing delay to enable buffering and scalability. | Data Lake |
| | 4. Idea to write a service that reads from ActiveMQ, does protocol conversion, and saves to GCS or DB. | Neutral |
| | 5. Current service is stateful and enqueues converted data to another queue for DB saving. | Neutral |
| **Scalability & Resilience** | 1. Benefits include resilience, scalability, and flexibility for growing data volume. | Data Lake |
| **Storage vs. Query Needs** | 2. Exploring whether to use time-series DB (InfluxDB, TimescaleDB) or data lake (BigQuery, Iceberg). | Under Evaluation |
| | 2. Data is mostly stored and passed downstream; minimal querying. | Data Lake |
| | 3. Data is transformed and pushed to analytics in required formats. | Data Lake |
| | 4. Downstream apps don’t require structured SQL-like access. | Data Lake |
| | 5. No enrichment or joins needed before handing off the data. | Data Lake |
| | 6. Preference is to optimize for storage cost over query speed. | Data Lake |

### Summary
- Based on current needs and trade-offs, a **Data Lake** (e.g., GCS + Parquet + Iceberg or BigQuery) is the more suitable option.

### When Time-Series DB Might Be Better
- Frequent, low-latency queries on recent data.
- Need for rollups, aggregations, or alerts on the data.
- Data is actively used in dashboards (e.g., Grafana).
- Built-in retention policies and compression are needed.
- High-ingestion throughput with query efficiency is critical.

