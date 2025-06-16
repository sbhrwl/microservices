# Microservices
- [Motivation](motivation/README.md)
- [Designing a system](https://github.com/sbhrwl/system_design/blob/main/projects/design/README.md)
- [Registering sensors](sensorregistration/README.md)
- [Sending commands to sensors](commandorchestration/README.md)
- [Analytics on sensor data](analytics/README.md)
- [gRPC](gRPC/README.md)

### Data Ingestion Upgrade Discussion – Phase 1

1. **Current DB**: Oracle, handling daily data.
2. **Partitioning**: Not used, but performance is acceptable for current use.
3. **Volume**: 100k meters → ~500k rows/day.
4. **10-min Profile Impact**: Will grow to ~14.4M rows/day.
5. **Query Type**: Batch processing is acceptable initially; real-time to be revisited later.