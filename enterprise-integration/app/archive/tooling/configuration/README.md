# Configuration
- [Overview](#overview)
- [MongoDB schema loader configuration](#mongodb-schema-loader-configuration)
  - [Environment variable management](#environment-variable-management)
  - [.env file structure](#env-file-structure)
  - [Connection string formats](#connection-string-formats)
  - [Environment variable defaults](#environment-variable-defaults)
  - [Runtime overrides](#runtime-overrides)
  - [Configuration precedence](#configuration-precedence)
  - [.env best practices](#env-best-practices)
- [IEC 61968 test producer configuration](#iec-61968-test-producer-configuration)
  - [Configuration uncertainty](#configuration-uncertainty)
  - [Inferred configuration requirements](#inferred-configuration-requirements)
  - [Likely configuration approaches](#likely-configuration-approaches)
  - [ActiveMQ connection formats](#activemq-connection-formats)
  - [Event generation configuration](#event-generation-configuration)
  - [Resource file configuration](#resource-file-configuration)
- [Configuration comparison](#configuration-comparison)
- [Configuration checklist](#configuration-checklist)
## Overview
- Tooling suite uses different configuration mechanisms per component
- MongoDB schema loader uses environment variables via .env
- IEC 61968 test producer configuration mechanism is unclear from code
- Section documents known configuration patterns and open gaps
## MongoDB schema loader configuration
### Environment variable management
- Uses dotenv for environment configuration
```javascript
// File: utils/config.js
require('dotenv').config()
const MONGODB_URI = process.env.NODE_ENV === 'test' ? process.env.TEST_MONGODB_URI : process.env.MONGODB_URI
let PORT = process.env.PORT
module.exports = { MONGODB_URI, PORT }
```
* Environment-based switching between test and non-test MongoDB URIs
* TEST_MONGODB_URI is used when NODE_ENV=test
* PORT is exported but usage is unclear
### .env file structure
* Location is tooling/mongodb-schemas/.env
* File must be created manually
```bash
MONGODB_URI=mongodb://localhost:27017/gfc
NODE_ENV=development
```

* Production example includes MongoDB Atlas connection string
* Connection string includes protocol, credentials, cluster, database, and options
* Supported options include retryWrites, w, authSource, and proxy settings
```bash
TEST_MONGODB_URI=mongodb://localhost:27017/gfc-test
MONGODB_URI=mongodb://localhost:27017/gfc-dev
NODE_ENV=test
```

* TEST_MONGODB_URI is used when NODE_ENV=test
### Connection string formats
* Local unauthenticated format mongodb://host:port/database
* Local authenticated format includes username, password, and authSource
* Docker connections use container hostname instead of localhost
* Atlas connections use mongodb+srv protocol and DNS seedlist
### Environment variable defaults
* No defaults defined in current implementation
* Application fails if MONGODB_URI is undefined
* TEST_MONGODB_URI is mandatory when NODE_ENV=test
### Runtime overrides
* npm start script sets NODE_ENV and NODE_OPTIONS
* NODE_ENV override forces production behavior
* NODE_OPTIONS adjusts Node.js heap size
### Configuration precedence
* Command-line environment variables
* npm script variables
* .env file variables
* Code defaults if implemented
### .env best practices
* .env must not be committed to version control
* .env.example should be used for team reference
* Separate files recommended for development, staging, and production

## IEC 61968 test producer configuration
### Configuration uncertainty
* Configuration loading mechanism not visible in snippets
* ActiveMQ credentials and broker URLs are hard-coded
* Environment switching is performed via commented code
* No externalized configuration is visible

```java
new ActiveMQConnectionFactory("broker", "broker1", "tcp://fijyvvrhessw24.eu.bm.net:61616");
```

### Inferred configuration requirements
* Broker URL
* Broker username
* Broker password
* Destination queue or topic name
### Likely configuration approaches
* application.properties file
* application.yml file
* Environment variables
* Command-line arguments
### ActiveMQ connection formats
* tcp://hostname:port
* failover:(tcp://host1:port,tcp://host2:port)
* ssl://hostname:port
### Event generation configuration
* Event probabilities are currently hard-coded
* Device range parameters are passed as method arguments
* External configuration is recommended for both
### Resource file configuration
* Event and alarm templates loaded from classpath or filesystem
* Expected under src/main/resources
## Configuration comparison
* MongoDB loader uses `.env` and `environment variables`
* Test producer configuration method is unknown
* Loader supports environment switching via NODE_ENV
* Producer switches environments via code comments
* Loader externalizes credentials
* Producer embeds credentials in source
## Configuration checklist
* Create .env file for MongoDB loader
* Set MONGODB_URI and NODE_ENV
* Verify MongoDB connectivity
* Exclude .env from version control
* Externalize ActiveMQ credentials from producer source
* Define broker URL and destination
