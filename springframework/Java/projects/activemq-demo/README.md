# Subscribe to ActiveMQ
- [Setup ActiveMQ](#setup-activemq)
- [Running the project](#running-the-project)
- [Monitor JMX in VisualVM](#monitor-jmx-in-visualvm)
## Setup ActiveMQ
- Create a [docker compose](docker-compose.yml) file
- run docker compose
  - This will launch ActiveMQ with the same configuration as the manual `docker run` command
```
docker-compose up -d
```
- Verify ActiveMQ Docker container is running
```
docker ps
```

|Port | Purpose | How to Access|
|---- | ------ | --------|
|8161 | Web Console (HTTP) | browser → http://localhost:8161/admin|
## Running the project
- Build the project
```
mvn clean install
```
- Compile
```
mvn compile
```
- Run the [producer](src/main/java/com/example/Producer.java)
```
mvn exec:java "-Dexec.mainClass=com.example.Producer"

mvn "exec:java" "-Dexec.mainClass=com.example.Producer" "-Dexec.jvmArgs=-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=5000 -Dcom.sun.management.jmxremote.rmi.port=5000 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=localhost"

Sent: Hello from Producer 1
```
- Run the [consumer](src/main/java/com/example/Consumer.java)
```
mvn exec:java "-Dexec.mainClass=com.example.Consumer"

mvn "exec:java" "-Dexec.mainClass=com.example.Consumer" "-Dexec.jvmArgs=-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=5000 -Dcom.sun.management.jmxremote.rmi.port=5000 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=localhost"

Received: Hello from Producer 1
```

## Monitor JMX in VisualVM
- [Download VisualVM](https://visualvm.github.io/download.html)

|Port | Purpose | How to Access|
|---- | ------ | --------|
|1099 | JMX Monitoring (binary) | VisualVM / JConsole → add JMX connection `localhost:1099`|
|5000 | Producer | VisualVM / JConsole → add JMX connection `localhost:5000`|
|5001 | Consumer | VisualVM / JConsole → add JMX connection `localhost:5001`|

<details>
  <summary>Project setup</summary>

## Project setup
### Prerequisites
- Java JDK 11+
- Apache Maven
- VS Code
- VS Code Extensions:
  - Java Extension Pack
  - Maven for Java
- Docker (ActiveMQ container already running on `localhost:61616`)
### Step by step instructions in VS Code
#### Create the maven project
- Open VS Code.
- Open the Command Palette (`Ctrl + Shift + P` or `Cmd + Shift + P`).
- Type `Maven: Create Maven Project` and select it.
- Choose `quickstart` archetype.
- Select the latest version of org.apache.maven.archetypes: maven-archetype-quickstart.
- Fill in:
  - Group ID: `com.example`
  - Artifact ID: `activemq-demo`
- Choose a location to save the project.
- This will create the folder **`activemq-demo/`** with basic structure.

#### Add ActiveMQ dependency
- Open the `pom.xml` file.
- Inside `<dependencies>`, add this:
```xml
<dependency>
    <groupId>org.apache.activemq</groupId>
    <artifactId>activemq-all</artifactId>
    <version>-17.6</version>
</dependency>
```
</details>
