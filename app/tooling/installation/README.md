# Installation
* [Prerequisites](#prerequisites)
* [Infrastructure prerequisites](#infrastructure-prerequisites)
* [Installation steps](#installation-steps)
* [Post-installation verification](#post-installation-verification)
* [Troubleshooting](#troubleshooting)
* [Build artifacts](#build-artifacts)
## Prerequisites
### System requirements
* The tooling suite requires both Java and Node.js runtimes
* Requirements are inferred from source code and dependencies
* Exact versions are not fully specified in the repository
### Java development kit (JDK)
* Required for the IEC 61968 test producer
* Repository does not specify a required version
* JDK 11 or later is recommended
* JDK 8 is the inferred minimum due to `javax.jms` usage
```bash
java -version
```

* Output should indicate Java 8 or higher
### Java build tool
* Required to build the Java test producer
* Build tool is not explicitly defined in the repository
* Common possibilities include
  * Maven using `pom.xml`
  * Gradle using `build.gradle` or `build.gradle.kts`
* Build tool can be identified by inspecting the producer directory
```bash
ls pom.xml build.gradle build.gradle.kts build.xml
```

### Node.js runtime
* Required for the MongoDB schema loader
* Version is not explicitly defined
* Dependency constraints imply Node.js 14.20.1 or later
* Node.js 18 LTS or 20 LTS is recommended
```bash
node --version
```

### npm
* Required to install schema loader dependencies
* npm is bundled with Node.js
* Lockfile format requires npm 7 or later
```bash
npm --version
```

## Infrastructure prerequisites
### Apache ActiveMQ
* Required for running the IEC 61968 test producer
* Used as the JMS message broker
* Docker-based setup is recommended for development

```bash
docker run -d \
  --name activemq \
  -p 61616:61616 \
  -p 8161:8161 \
  apache/activemq-classic:latest
```

* Default verification endpoints include
  * JMS broker at `tcp://localhost:61616`
  * Web console at `http://localhost:8161/admin`

### MongoDB
* Required for running the MongoDB schema loader
* Version is not explicitly defined
* Mongoose compatibility implies MongoDB 4.0 or later
* MongoDB 5.0 or 6.0 is recommended
* Docker-based setup is recommended for development
```bash
docker run -d \
  --name mongodb \
  -p 27017:27017 \
  -e MONGO_INITDB_ROOT_USERNAME=admin \
  -e MONGO_INITDB_ROOT_PASSWORD=password \
  mongo:6.0
```

```bash
mongosh mongodb://localhost:27017
```

## Installation steps
### IEC 61968 test producer (Java)
* Navigate to the producer directory
```bash
cd tooling/iec61968-test-producer
```

* Identify the build tool by checking for configuration files
```bash
ls pom.xml build.gradle build.gradle.kts build.xml
```

#### Maven build path

* Applicable if `pom.xml` exists

```bash
mvn clean install
```

* Build artifacts are expected under `target/`
#### Gradle build path
* Applicable if `build.gradle` exists
```bash
./gradlew build
```

* Build artifacts are expected under `build/`
* Verify compiled classes exist
```bash
ls target/classes/com/landisgyr/gfc/
ls build/classes/java/main/com/landisgyr/gfc/
```

* Configure ActiveMQ connection details
* Configuration method is not visible in code
* Common approaches include
  * Properties files
  * Command-line arguments
  * Environment variables
* Execute the producer using the selected build tool
```bash
java -jar iec61968-test-producer.jar
```

* Successful execution establishes a connection to ActiveMQ
* Messages should appear in the configured queue

### MongoDB schema loader (Node.js)
* Navigate to the schema loader directory
```bash
cd tooling/mongodb-schemas
```

* Install dependencies
```bash
npm install
```

* Verify installed packages
```bash
npm list --depth=0
```

* Create a `.env` configuration file
```bash
nano .env
```

* Configure MongoDB connection details
* Variable names must match those expected in `utils/config.js`
```bash
MONGODB_URI=mongodb://localhost:27017/gfc
NODE_ENV=development
```

* Verify MongoDB connectivity
```bash
mongosh mongodb://localhost:27017/gfc
```

* Enable required data generation functions in `index.js`
* Only uncommented functions will execute
* Run the schema loader
```bash
npm start
```

```bash
node index.js
```

## Post-installation verification
### Producer verification
* Verify Java compilation succeeded
* Verify ActiveMQ is reachable on port 61616
* Verify messages appear in ActiveMQ queues
### Loader verification
* Verify MongoDB connectivity
* Verify collections are created
* Verify document counts increase after execution
```bash
show collections
db.devices.countDocuments()
```
## Troubleshooting
### Java producer issues
* Class not found errors indicate missing build artifacts
* JMS connection errors indicate ActiveMQ is not running
* Dependency errors indicate incomplete dependency resolution
### Node.js loader issues
* Missing module errors indicate incomplete `npm install`
* Connection timeouts indicate MongoDB is not running
* Authentication failures indicate incorrect credentials
* Heap memory errors indicate insufficient Node.js memory allocation
## Build artifacts
### Java producer
* Maven builds output to `target/`
* Gradle builds output to `build/`
* Executable JAR files are produced in both cases
### Node.js loader
* Dependencies are installed under `node_modules/`
* Runtime configuration is stored in `.env`
* Entry point is `index.js`
