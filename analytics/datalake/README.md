# Designing data lake in GCS
## Objective
- Store parsed DLMS smart meter data efficiently for periodic analytics, keeping it cloud-agnostic and query-ready.
## Transformation options
### Time with Meter Id
- 🔍 Easy time-based filtering  
- 🔍 Can load data for specific meters  
- ⚠️ Too many small files (if each meter is separate)  
- `gs://pq-data/{year}/{month}/{day}/{hour}/meter-{meterId}.json`
- File count estimate
  - **500k meters × 24 pushes/day = `12 million files/day`**
### Time with OBIS code
- 📊 Optimized for analytical queries by metric type  
- ⚠️ More complexity in ingestion  
- ⚠️ Spreads one meter's data across multiple files
- `gs://pq-data/{year}/{month}/{day}/{hour}/obis-{code}.json`
### Time only 
- All meters in one file per hour
- 🧩 Simplest structure  
- ⚠️ Harder to isolate individual meters  
- 🚀 Fast for global batch queries  
- `gs://pq-data/{year}/{month}/{day}/{hour}/all-meters.json`
- File count estimate
  - **500k meters × 10 OBIS codes × 24 pushes/day = `120 million OBIS readings/day`**

## Mitigating large number of files
- **Batch** by group of meters (e.g., 100 meters per file) → reduces to 120k files/day.
- Use GCS compose or *"Dataflow job** to consolidate small files hourly or daily.
