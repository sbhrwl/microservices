# Grafana visualisations
- [Setup connection to InfluxDB](#setup-connection-to-influxdb)
- [Build dashboard](#build-dashboard)
  - [Real time voltage widget](#real-time-voltage-widget)
  - [Real time current widget](#real-time-current-widget)
  - [Historical voltage trends](#historical-voltage-trends)
  - [Voltage anomaly alert](#voltage-anomaly-alert)
  - [Compare voltage across meters and phases](#compare-voltage-across-meters-and-phases)
## Setup connection to InfluxDB
- Open your browser, go to `http://localhost:3000`
- Login: `admin` / `admin123`
- On the left menu, click **Settings (gear icon)** > **Data Sources**
- Click **Add data source**
- Select **InfluxDB**
- Configure:
  * URL: `http://192.168.0.102:8086`
    * use `ipconfig` for finding ip address of your machine
  * Auth
    * Enable `Basic auth`
      * username: admin
      * password: admin123
  * Database / Bucket: your InfluxDB bucket (e.g., `power_quality`)
  * Organization: your InfluxDB org name
- Click **Save & Test**
## Build dashboard
* Real-time monitoring
* Historical trends
* Visual anomaly alert
* Comparison across meters

### Real time voltage widget
- Go to your dashboard → **Add panel**
- Query:
  * FROM: `power_quality`
  * SELECT: `last("voltage")`
  * GROUP BY: `time($__interval)`
- Set:
  * Visualization: **Time series**
  * Title: `Real-time Voltage`
  * Refresh: `5s`
- Click **Apply**.
### Real time current widget
- Go to your dashboard → **Add panel**
- Query:
  * FROM: `power_quality`
  * SELECT: `last("current")`
  * GROUP BY: `time($__interval)`
- Set:
  * Visualization: **Time series**
  * Title: `Real-time Current`
  * Refresh: `5s`
- Click **Apply**.
### Historical voltage trends
- **Add panel**
- Query:
  * FROM: `power_quality`
  * SELECT: `mean("voltage")`
  * GROUP BY: `time(1h)`
- Time range: top-right → set to **Last 7 days**
- Visualization: **Time series**
- Title: `Voltage - Daily Trend`
- Click **Apply** when done.
### Voltage anomaly alert
- Visual threshold)
- Open **Real-time Voltage** panel → click **Edit**
- Go to **Overrides** or **Thresholds**
- Add a threshold:
  * Value: `210`
  * Color: **red**
- Optional: Show area below threshold as filled
- Click **Apply**
### Compare voltage across meters and phases
- Assuming you have a tag like `meter_id` or `phase` in your data:
- **Add panel**
- Query:
  * FROM: `power_quality`
  * SELECT: `mean("voltage")`
  * GROUP BY: `time($__interval), "meter_id"` *(or "phase")*
- Visualization: **Time series**
- Title: `Voltage Comparison by Meter`
- Click **Apply**
