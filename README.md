# Microservices
- [Motivation](motivation/README.md)
- [Designing a system](https://github.com/sbhrwl/system_design/blob/main/projects/design/README.md)
- [Registering sensors](sensorregistration/README.md)
- [Sending commands to sensors](commandorchestration/README.md)
- [Analytics on sensor data](analytics/README.md)
- [gRPC](gRPC/README.md)

## Data Ingestion Upgrade Discussion
### Phase 1
1. **Current DB**: Oracle, handling daily data.
2. **Partitioning**: Not used, but performance is acceptable for current use.
3. **Volume**: 100k meters → ~500k rows/day.
4. **10-min Profile Impact**: Will grow to ~14.4M rows/day.
5. **Query Type**: Batch processing is acceptable initially; real-time to be revisited later.
### Phase 2
6. Ingestion pipeline: Kafka → service → protocol conversion → enrichment → Oracle DB.
7. Bottlenecks: CPU, IO, and memory all impacted under load.
8. Service is stateful and not horizontally scalable currently.
9. No raw message persistence for replay or audit.
10. Data retention in Oracle is 90 days.
### Phase 3
11. 10-min data retention undecided; possibly 60 days.
12. Current queries are pushed downstream; no direct query load now.
13. Open to migrating 10-min data to a better storage system.
14. Target batch processing latency: within or less than 10 minutes.
15. Some tolerance for missing or late data.
### Phase 4
16. Monitoring and alerting for ingestion failures is handled by downstream analytics.
17. Current sensor registers: 5; expected to grow to 15 registers per sensor.
18. New design must accommodate the increase in registers.
19. Messaging uses ActiveMQ (not Kafka); scaling strategy for registers unknown.
20. ActiveMQ partitioning/clustering/load balancing can be introduced if needed.