- `proto/` → contract (generated code lives under `target/`)
- `grpc/` → transport layer
- `service/` → business logic
- `repo/` → persistence
- `model/` → domain

## Connect MongoDB
- Verify at **MongoDB**
- Open MongoDB shell
    - Connect: `Please enter a MongoDB connection string (Default: mongodb://localhost/): mongodb://root:root123@localhost:27017/admin`
    - Check available Databses: `show dbs`
    - Swicth to DB: `use sensorregistration`
    - Query documents in the collections: `db.sensorRegistrations.find().pretty()`
        - Find a sensor: `db.sensorRegistrations.findOne({ sensorId: "sensor789" })`
    - Delete documents from the collections: `db.sensorRegistrations.deleteMany({})`
        - Delete a sensor: `db.sensorRegistrations.deleteOne({ sensorId: "sensor789" })`