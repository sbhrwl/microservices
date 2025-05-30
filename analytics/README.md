# [Analytics on power quality data](https://github.com/sbhrwl/energy/blob/main/meteringdata/PQadapter/README.md)
- [Event collection](#event-collection)
- [Designing a system](https://github.com/sbhrwl/system_design/blob/main/projects/design/README.md)
- [Grid problems to solve using power quality data](grid-problems-to-solve-using-power-quality-data)
- [Data source or initial discovery](data-source-or-initial-discovery)
- [Ingestion flow](#ingestion-flow)
## Grid problems to solve using power quality data
- **Phase imbalance**
- **Voltage fluctuations and sags**
- **Harmonics and distortion**
## Event collection 

| Measurement            | Phase imbalance | Voltage fluctuations and sags | Harmonics and distortion |
|------------------------|------------------|------------------------------|-------------------------|
| Phase (A/B/C)          | ✅               | ✅                           |                         |
| Phase Voltage          | ✅               | ✅                           | ✅                      |
| Phase Current          | ✅               |                              | ✅                      |
| Power Factor           |                  |                              | ✅                      |
| Power Quality (THD, dips, swells) |        | ✅                           | ✅                      |

## Data source or initial discovery
- Data is collected from **DLMS smart meters**.
- Smart meters **push power quality data hourly** using a **push profile**.
- Each push profile includes **individual OBIS codes** for:
  - Phase voltage
  - Phase current
  - Power (active/reactive)
  - Power factor
  - Power quality metrics (e.g., THD, sags/swells)
## Ingestion flow
- Data is pushed into a **message broker** that is part of the **HES (Headend System)**.
- **All meter data arrives on a single queue**, regardless of:
  - Meter ID
  - OBIS code
### Design decision pending
- No existing service yet to **split or route messages** by meter or OBIS code.
- This will be the **starting point** for designing the data processing pipeline.
