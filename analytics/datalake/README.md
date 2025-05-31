# Designing data lake in GCS
## Objective
- Store parsed DLMS smart meter data efficiently for periodic analytics, keeping it cloud-agnostic and query-ready.
## Transformation options
### Time only 
- All meters in one file per hour
- 🧩 Simplest structure  
- ⚠️ Harder to isolate individual meters  
- 🚀 Fast for global batch queries  
- `gs://pq-data/{year}/{month}/{day}/{hour}/all-meters.json`
### Time with Meter Id
- Easy time-based filtering  
- Can load data for specific meters  
- `gs://pq-data/{year}/{month}/{day}/{hour}/meter-{meterId}.json`
- File count estimate
  - **500k meters × 24 pushes/day = `12 million files/day`**
### Time with OBIS code
- Optimized for analytical queries by metric type  
- ⚠️ More complexity in ingestion  
- ⚠️ Spreads one meter's data across multiple files
- `gs://pq-data/{year}/{month}/{day}/{hour}/obis-{code}.json`
- File count estimate
  - **500k meters × 10 OBIS codes × 24 pushes/day = `120 million OBIS readings/day`**

## Mitigating large number of files
- **Batch** by group of meters or obis readings
  - If we batch `500 meters` per file:
    - 12 million ÷ 500 = 24,000 files/day
  - If we batch `5,000 obis readings` per file:
    - 120 million ÷ 5,000 = 24,000 files/day
- Use GCS compose or **Dataflow job** to consolidate small files hourly or daily.

### Storage access efficiency
- Time + OBIS Code vs Time + Meter

| Criteria                        | Time + OBIS Code                            | Time + Meter                             |
|-------------------------------|---------------------------------------------|------------------------------------------|
| **Optimized for**             | Analytics by OBIS (e.g., voltage trends)    | Per-meter diagnostics / traceability     |
| **Reads for bulk analytics** | ✅ Efficient (scan one OBIS type)           | ❌ Less efficient (need to scan all meters) |
| **Reads for single meter**    | ❌ Slower (need to search all OBIS files)   | ✅ Fast (data is in one place per meter)  |
| **Write complexity**          | Medium (grouping by OBIS)                   | Low (one file per push)                  |
| **File count (raw)**          | 24,000/day (with 5k OBIS readings/file)     | 12M/day (or 24k/day if batched)          |
| **Best for**                  | Aggregation, trend detection                | Auditing, debugging per device           |

### Summary
- Choose **Time + OBIS Code** if:
  - Main use case is **analytics across all meters**
  - You want to quickly analyze e.g., voltage dips, sags, across regions
- Choose **Time + Meter** if:
  - Your focus is **per-device investigation**, e.g., troubleshooting a faulty meter
  - Simpler writing logic, no need to group by OBIS
