# Runtime behavior
* [MongoDB schema loader](#mongodb-schema-loader)
  * [Entry point](#entry-point)
  * [Execution](#execution)
  * [Runtime configuration](#runtime-configuration)
  * [Expected behavior](#expected-behavior)
  * [Expected output](#expected-output)
* [IEC 61968 test producer](#iec-61968-test-producer)
  * [Entry point](#entry-point-1)
  * [Execution](#execution-1)
  * [Runtime behavior](#runtime-behavior-1)
  * [Runtime modes](#runtime-modes)
  * [Message sources](#message-sources)
  * [Expected output](#expected-output-1)
  * [Message publishing logic](#message-publishing-logic)
* [Runtime comparison](#runtime-comparison)
* [Troubleshooting](#troubleshooting)
  * [MongoDB loader](#mongodb-loader)
  * [IEC producer](#iec-producer)
## MongoDB schema loader
### Entry point
```javascript
// File: index.js
// No main function visible in snippets - execution starts at module level
```
### Execution
```bash
npm run start
```
* **Equivalent to:**
```bash
set NODE_ENV=production && set NODE_OPTIONS=--max-old-space-size=8192 && node index.js
```

### Runtime configuration

| Setting        | Value                       | Purpose                                     |
| -------------- | --------------------------- | ------------------------------------------- |
| `NODE_ENV`     | `production`                | Uses `MONGODB_URI` (not `TEST_MONGODB_URI`) |
| `NODE_OPTIONS` | `--max-old-space-size=8192` | Allocates 8GB heap memory                   |

### Expected behavior
* ⚠️ Unknown – `index.js` content not visible
* **Likely operations**: 1. Connect to MongoDB 2. Create/update collections 3. Generate sample data 4. Create indexes 5. Validate schemas
### Expected output
```
Connected to MongoDB: mongodb://localhost:27017/gfc
Creating collections...
  ✓ devices
  ✓ energyReading15
  ✓ daily-time-series
  ✓ events
Generating sample data...
  ✓ 1000 devices created
  ✓ 96000 energy readings created
  ✓ 1000 daily aggregates created
Creating indexes...
  ✓ Device indexes created
  ✓ Time-series indexes created
Done in 45.3s
```

## IEC 61968 test producer
### Entry point
```java
// File: Producer.java
public static void main(String[] args) {
    try {
        new Producer().start();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

### Execution
* **Build tool unknown** – check for `pom.xml` → Maven, `build.gradle` → Gradle
* **Maven:**
```bash
cd tooling/iec61968-test-producer
mvn clean package
java -jar target/iec61968-test-producer.jar
```

* **Direct execution (development):**
```bash
mvn exec:java -Dexec.mainClass="com.landisgyr.smoc.Producer"
```

### Runtime behavior
```java
public void start() throws JMSException, IOException {
    ActiveMQConnectionFactory connectionFactory =
        new ActiveMQConnectionFactory("sit100_gfcuser", "lDjWM6zCrvXNaMOTsYJp", 
                                      "tcp://fijyvvrsit100.eu.bm.net:61616");
    Connection connection = connectionFactory.createConnection();
    connection.start();
    session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    Topic topic = session.createTopic("SIT100_PUSH_SMOC");
    producer = session.createProducer(topic);
    Stream<Path> messages = Files.list(Paths.get("src/test/resources/13.4"));
    messages.forEach(this::publishMessage);
    producer.close();
    session.close();
    connection.close();
}
```

### Runtime modes
* **Mode 1: Topic publishing (active)**
```java
Topic topic = session.createTopic("SIT100_PUSH_SMOC");
producer = session.createProducer(topic);
```

* **Mode 2: Queue publishing (commented out)**
```java
// Queue queue = session.createQueue("IEC20_PUSH_1");
```

### Message sources
* **Active source**
```java
Stream<Path> messages = Files.list(Paths.get("src/test/resources/13.4"));
```

* **Alternative source (commented)**
```java
// Stream<Path> messages = Files.list(Paths.get("src/test/resources/13.2"));
```

* **Expected directory structure:**
```
src/test/resources/
├── 13.2/
│   ├── message1.xml
│   ├── message2.xml
│   └── ...
└── 13.4/
    ├── message1.xml
    ├── message2.xml
    └── ...
```

### Expected output
```
Sent count 47 messages
```

* **On error:**
```
javax.jms.JMSException: Could not connect to broker URL: tcp://...
```

### Message publishing logic
```java
private void publishMessage(Path messagePath) {
    try {
        String messageContent = Files.readString(messagePath);
        TextMessage message = session.createTextMessage(messageContent);
        producer.send(message);
        messageSentCount++;
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

* **Process:** 1. Read XML file from disk 2. Create JMS TextMessage 3. Send to topic/queue 4. Increment counter
## Runtime comparison

| Aspect        | MongoDB loader            | IEC producer              |
| ------------- | ------------------------- | ------------------------- |
| Language      | JavaScript (Node.js)      | Java                      |
| Entry         | Module-level execution    | `main()` method           |
| Execution     | `npm run start`           | `java -jar ...`           |
| Configuration | `.env` file               | Hard-coded (⚠️)           |
| Mode          | Single mode               | Topic/Queue toggle        |
| Output        | Database population       | Message publishing        |
| Duration      | Minutes (data generation) | Seconds (file publishing) |
| Memory        | 8GB heap                  | Default JVM heap          |

## Troubleshooting
### MongoDB loader
* **Connection failure**
```
Error: connect ECONNREFUSED 127.0.0.1:27017
```

* Fix: Start MongoDB or update `MONGODB_URI` in `.env`
* **Out of memory**
```
FATAL ERROR: Reached heap limit Allocation failed
```

* Fix: Increase `--max-old-space-size` value
### IEC producer
* **Broker connection failure**
```
JMSException: Could not connect to broker
```

* Fix: Verify broker URL, credentials, network access
* **Missing message files**
```
NoSuchFileException: src/test/resources/13.4
```

* Fix: Ensure message files exist in specified directory

* **Authentication failure**
```
SecurityException: User name [X] or password is invalid
```

* Fix: Update credentials in `Producer.java` (⚠️ hard-coded)
