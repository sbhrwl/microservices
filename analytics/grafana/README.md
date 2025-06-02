# Grafana connection to InfluxDB
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
