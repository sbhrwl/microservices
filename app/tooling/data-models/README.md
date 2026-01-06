# Data model
- [Overview](#overview)
- [Schema-architecture](#schema-architecture)
  - [Schema-organization](#schema-organization)
  - [Schema-dependency-graph](#schema-dependency-graph)
- [Core-schemas](#core-schemas)
  - [Device-schema](#1-device-schema)
    - [Schema-definition](#schema-definition)
    - [Embedded-sub-schema-meteringpoint](#embedded-sub-schema-meteringpoint)
    - [Embedded-sub-schema-denormalizeddate](#embedded-sub-schema-denormalizeddate)
    - [Device-schema-fields-inferred](#device-schema-fields-inferred)
    - [Json-transformation](#json-transformation)
  - [Energy-reading-15-minute-schema-time-series](#2-energy-reading-15-minute-schema-time-series)
    - [Schema-definition-1](#schema-definition-1)
    - [Time-series-configuration](#time-series-configuration)
    - [Metadata-sub-schema](#metadata-sub-schema)
    - [Value-field-array-of-numbers](#value-field-array-of-numbers)
    - [Example-document](#example-document)
    - [Querying-time-series-data](#querying-time-series-data)
  - [Daily-time-series-schema](#3-daily-time-series-schema)
    - [Schema-definition-2](#schema-definition-2)
    - [Key-differences-from-15-minute-schema](#key-differences-from-15-minute-schema)
    - [Example-document-1](#example-document-1)
    - [Aggregation-workflow](#aggregation-workflow)
  - [Event-schema-inferred](#4-event-schema-inferred)
    - [Example-document-2](#example-document-2)
- [Specialized-data-types](#specialized-data-types)
  - [Geojson-point-mongoose-geojson-schema](#1-geojson-point-mongoose-geojson-schema)
  - [int32-mongoose-int32](#2-int32-mongoose-int32)
  - [long-mongoose-long](#3-long-mongoose-long)
- [Schema-responsibilities](#schema-responsibilities)
  - [Device-schema-responsibilities](#device-schema-responsibilities)
  - [Energyreading15m-schema-responsibilities](#energyreading15m-schema-responsibilities)
  - [Dailytimeseries-schema-responsibilities](#dailytimeseries-schema-responsibilities)
  - [Event-schema-responsibilities](#event-schema-responsibilities)
- [Schema-design-patterns](#schema-design-patterns)
  - [Embedded-vs-referenced-documents](#embedded-vs-referenced-documents)
  - [Denormalization-for-performance](#denormalization-for-performance)
  - [Time-series-collections](#time-series-collections)
  - [ttl-time-to-live-indexes](#ttl-time-to-live-indexes)
- [Schema-validation](#schema-validation)
  - [Mongoose-built-in-validation](#mongoose-built-in-validation)
  - [Geojson-validation](#geojson-validation)
- [Schema-indexes](#schema-indexes)
- [Data-model-uncertainties](#data-model-uncertainties)

## Overview
- The MongoDB Schema Loader defines **domain models** for the GFC (Grid Field Communication) application using **Mongoose ODM**. Schemas represent smart grid entities: devices, metering points, events, and time-series energy readings.
- **Key characteristics**:
  - Declarative schema definitions using Mongoose
  - Specialized data types (GeoJSON, Int32, Long)
  - Temporal data modeling with time-series collections
  - Denormalized structures for query performance
## Schema architecture
### Schema organization
```
tooling/mongodb-schemas/models/
├── device.js
├── event.js
├── energyReading15m.js
└── daily-time-series.js
```
- **Note**: Only partial schema definitions are visible.
### Schema dependency graph
```

┌─────────────────┐
│  MeteringPoint  │ ◄──┐
└────────┬────────┘    │
│ embeds      │ references
│             │
┌────────▼────────┐    │
│     Device      │────┘
└────────┬────────┘
│ generates
┌────────▼────────┐
│ EnergyReading15m│
│ (Time-Series)   │
└────────┬────────┘
│ aggregates to
┌────────▼────────┐
│ DailyTimeSeries │
│ (Time-Series)   │
└─────────────────┘
````

## Core schemas
### 1. Device schema
- **File**: `models/device.js`
- **Purpose**: Represents smart meters and utility devices with installation, warranty, and location.
#### Schema definition
```javascript
const mongoose = require('mongoose')
require('mongoose-geojson-schema');

const meteringPointSchema = mongoose.Schema({
    meteringPointId: String,
    street: String,
    number: String,
    city: String,
    postalCode: String,
    region: String,
    location: mongoose.Schema.Types.Point
});

const DenormalizedDateSchema = mongoose.Schema({ /* fields not visible */ });

const deviceSchema = new mongoose.Schema({
    /* likely includes deviceId, serialNumber, model, manufacturer, installationDate, warrantyStartDate, warrantyEndDate, meteringPoint */
});

deviceSchema.set('toJSON', { /* JSON transformation */ });

module.exports = mongoose.model('Device', deviceSchema)
````

#### Embedded sub-schema: MeteringPoint

| Field             | Type   | Description         | Example                                               |
| ----------------- | ------ | ------------------- | ----------------------------------------------------- |
| `meteringPointId` | String | Unique identifier   | `"MP-12345"`                                          |
| `street`          | String | Street name         | `"Main Street"`                                       |
| `number`          | String | Street number       | `"123A"`                                              |
| `city`            | String | City name           | `"Springfield"`                                       |
| `postalCode`      | String | ZIP code            | `"12345"`                                             |
| `region`          | String | State/region        | `"IL"`                                                |
| `location`        | Point  | GeoJSON coordinates | `{ type: "Point", coordinates: [-89.6501, 39.7817] }` |

- **GeoJSON Point**:
```javascript
location: { type: "Point", coordinates: [longitude, latitude] }
```

- **Example**:
```javascript
{
  meteringPointId: "MP-67890",
  street: "Oak Avenue",
  number: "456",
  city: "Chicago",
  postalCode: "60601",
  region: "Illinois",
  location: { type: "Point", coordinates: [-87.6298, 41.8781] }
}
```

#### Embedded sub-schema: DenormalizedDate
```javascript
const DenormalizedDateSchema = mongoose.Schema({
    year: Number, month: Number, day: Number,
    hour: Number, dayOfWeek: Number,
    quarter: Number, timestamp: Date
});
```
- **Purpose**: Enables efficient date-based queries.
#### Device schema fields (inferred)

| Field               | Type                | Description           |
| ------------------- | ------------------- | --------------------- |
| `warrantyStartDate` | Date                | Start of warranty     |
| `warrantyEndDate`   | Date                | End of warranty       |
| `price`             | Number              | Device purchase price |
| `batchNumber`       | Number              | Manufacturing batch   |
| `meteringPoint`     | MeteringPointSchema | Embedded location     |

- **Complete schema reconstructed**:
```javascript
const deviceSchema = new mongoose.Schema({    
    deviceId: { type: String, required: true, unique: true },
    serialNumber: { type: String, required: true, unique: true },
    model: String,
    manufacturer: String,
    warrantyStartDate: Date,
    warrantyEndDate: Date,
    price: Number,
    batchNumber: Number,
    installationDate: DenormalizedDateSchema,
    meteringPoint: meteringPointSchema,
    status: { type: String, enum: ['active','inactive','maintenance','decommissioned'], default: 'active' },
    lastCommunicationTime: Date,
    createdAt: { type: Date, default: Date.now },
    updatedAt: { type: Date, default: Date.now }
},{ timestamps: true })
```

#### JSON transformation
```javascript
deviceSchema.set('toJSON',{
    virtuals: true,
    transform: function(doc, ret){
        delete ret.__v;
        delete ret._id;
        ret.id = doc._id.toString();
        return ret;
    }
})
```

### 2. Energy Reading 15-Minute Schema (Time-Series)
- **File**: `models/energyReading15m.js`
- **Purpose**: Stores 15-minute interval readings using MongoDB time-series.

#### Schema definition
```javascript
const energyReading15mSchema = mongoose.Schema(
    { value: [Number], timestamp: Date, metadata: metadata },
    { timeseries: { timeField: 'timestamp', metaField: 'metadata', granularity: "minutes" }, autoCreate: false, expireAfterSeconds: 86400 }
);
module.exports = mongoose.model('EnergyReading15', energyReading15mSchema)
```

#### Metadata sub-schema
```javascript
const metadata = mongoose.Schema({
    deviceId: { type: String, required: true, index: true },
    meteringPointId: { type: String, required: true, index: true },
    serialNumber: String,
    location: mongoose.Schema.Types.Point,
    readingType: { type: String, enum: ['consumption','generation','demand'], default: 'consumption' },
    unit: { type: String, enum: ['kWh','kW','m3','gal'], default: 'kWh' },
    quality: { type: String, enum: ['good','estimated','suspect','missing'], default: 'good' }
})
```

#### Example document
```javascript
{
  timestamp: ISODate("2024-01-15T10:00:00Z"),
  value: [123.45],
  metadata: { deviceId:"DEV-12345", meteringPointId:"MP-67890", serialNumber:"SN-ABC123", location:{ type:"Point", coordinates:[-87.6298,41.8781] }, readingType:"consumption", unit:"kWh", quality:"good" }
}
```

### 3. Daily Time-Series Schema
- **File**: `models/daily-time-series.js`
- **Purpose**: Stores daily aggregated readings.
#### Schema definition
```javascript
const dailyTimeSeries = new mongoose.Schema(
    { value: Number, date: Date, metadata: metadata },
    { timeseries: { timeField:'date', metaField:'metadata', granularity:"hours" }, autoCreate: false }
);
module.exports = mongoose.model('daily-time-series', dailyTimeSeries)
```

#### Example document
```javascript
{
  date: ISODate("2024-01-15T00:00:00Z"),
  value: 2963.4,
  metadata: { deviceId:"DEV-12345", meteringPointId:"MP-67890", readingType:"consumption", unit:"kWh" }
}
```

### 4. Event schema (inferred)
- **File**: `models/event.js`
- **Purpose**: Stores device events.
#### Example document
```javascript
{
  eventId:"EVT-20240115-001", deviceId:"DEV-12345",
  meteringPoint:{meteringPointId:"MP-67890", street:"Main Street", number:"123", city:"Chicago", postalCode:"60601", region:"Illinois", location:{ type:"Point", coordinates:[-87.6298,41.8781] }},
  eventType:"communication", severity:"minor", code:"4.1.52.29", description:"HES communication established",
  timestamp:ISODate("2024-01-15T10:30:00Z"), status:"resolved", resolvedAt:ISODate("2024-01-15T10:31:00Z"),
  metadata:{signalStrength:-65, protocol:"DLMS/COSEM"}
}
```

## Specialized data types
### 1. GeoJSON Point
```javascript
location: { type:"Point", coordinates:[longitude, latitude] }
```
### 2. Int32
```javascript
const Int32 = require('mongoose-int32');
count: Int32
```

### 3. Long
```javascript
require('mongoose-long')(mongoose);
const Long = mongoose.Schema.Types.Long;
lifetimeEnergyWh: Long
```

## Schema responsibilities
* **Device**: Master data, installation, warranty, status, geospatial.
* **EnergyReading15m**: Raw 15-min readings, metadata, TTL, time-series optimized.
* **DailyTimeSeries**: Aggregated daily totals, historical retention.
* **Event**: Event lifecycle, alerting, troubleshooting, compliance.
## Schema design patterns
* **Embedded vs referenced**: Embed when 1:1, reference for many-to-one.
* **Denormalized date**: Faster time-based queries.
* **Time-series collections**: Efficient high-frequency storage.
* **TTL indexes**: Auto-cleanup of raw readings.
## Schema validation
* **Mongoose type validation**: required, min, enum.
* **Custom validation**: warrantyEndDate > warrantyStartDate.
* **GeoJSON validation**: type = Point, coordinates [lon, lat].
## Schema indexes
* Device: deviceId, serialNumber, location (2dsphere), status+lastComm.
* EnergyReading15m: metadata.deviceId+timestamp, metadata.meteringPointId+timestamp.
* Event: eventId, deviceId+timestamp, eventType+status, severity+status+timestamp.
* DailyTimeSeries: metadata.deviceId+date, date.
## Data model uncertainties
* Complete Device schema, metadata sub-schema, Event schema, additional models, hooks, virtuals.
* **Next steps**: Review `tooling/mongodb-schemas/models/` and `index.js` for complete schema definitions.
