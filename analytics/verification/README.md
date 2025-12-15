# Verification
- Publish sensor data to ActiveMQ
  - `POST`: `http://localhost:9081/api/powerquality/generate`
  - No payload
  - It will send `~300 messages` to the `power-quality-queue`
- **Ingestion service**
```bash
Successfully pushed 1 points to InfluxDB
Received message: com.example.ingestion.dto.PowerQualityMessage@30eabbf8
Successfully pushed 1 points to InfluxDB
Received message: com.example.ingestion.dto.PowerQualityMessage@738f57bd
```