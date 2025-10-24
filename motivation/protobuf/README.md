# Protobuf usage
- [Introduction](#introduction)
- [Protobuf setup guide for Java projects](#protobuf-setup-guide-for-java-projects)
  - [Add your proto files](#add-your-proto-files)
  - [Add Protobuf dependencies and plugin to pom](#add-protobuf-dependencies-and-plugin-to-pom)
  - [Generate java classes from proto](#generate-java-classes-from-proto)
  - [Use the generated classes](#use-the-generated-classes)
  - [Troubleshooting checklist](#troubleshooting-checklist)
  - [Updating proto files](#updating-proto-files)

## Introduction
* **Internal microservices** often use Protobuf, especially when built with **gRPC** or communicating via **message brokers**.
* Protobuf helps ensure **performance, compactness, and schema evolution** in internal systems.

| Use Case                      | Protobuf Usage | Notes                                                              |
| ----------------------------- | -------------- | ------------------------------------------------------------------ |
| Internal microservices        | ✅ Yes          | Common with gRPC or REST+Protobuf in performance-critical systems  |
| Public REST APIs              | ❌ No           | JSON preferred due to readability and broad client compatibility   |
| Message brokers (Kafka, etc.) | ✅ Yes          | Widely used with schema registries for efficient, compact messages |
| Event-driven architecture     | ✅ Yes          | Used for structured, fast, and evolvable event formats             |

## Protobuf setup guide for Java projects
### Add your proto files
- Place all your `.proto` files in the standard directory:
  ```
  src/main/proto/
  ```
- Example:
  ```
  src/main/proto/taskMessage.proto
  ```
- `taskMessage.proto` file
```proto
syntax = "proto3";

option java_package = "com.example.taskservice";
option java_outer_classname = "TaskMessageProto";

message TaskMessage {
  string taskId = 1;
  CommandType commandType = 2;
  repeated string commandArgs = 3;
  repeated string sensorList = 4;
}

enum CommandType {
  DIRECT = 0;
  OPEN = 1;
  PULSE = 2;
}
```

### Add Protobuf dependencies and plugin to pom
- Add the following to your `<dependencies>` section (use the latest compatible version):
```xml
<dependency>
    <groupId>com.google.protobuf</groupId>
    <artifactId>protobuf-java</artifactId>
    <version>3.25.3</version>
</dependency>
```

- Add the following to your `<build><plugins>` section:
```xml
<plugin>
    <groupId>org.xolstice.maven.plugins</groupId>
    <artifactId>protobuf-maven-plugin</artifactId>
    <version>0.6.1</version>
    <configuration>
        <protocArtifact>com.google.protobuf:protoc:3.25.3:exe:${os.detected.classifier}</protocArtifact>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>compile</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
- **Tip:** : Always match the `protobuf-java` version and the `protocArtifact` version.
### Generate java classes from proto
- Run the following Maven command to generate Java classes from your `.proto` files:
```sh
mvn clean compile
```
- Generated files will appear in:
  ```
  target/generated-sources/protobuf/java/
  ```
### Use the generated classes
- Import and use the generated classes in your Java code.
- Example import:
  ```java
  import com.example.yourpackage.YourProtoOuterClass;
  ```

### Troubleshooting checklist
- **Versions:** Ensure `protobuf-java` and `protocArtifact` versions match.
- **Directory:** `.proto` files must be in `src/main/proto/`.
- **IDE:** If using an IDE, mark `target/generated-sources/protobuf/java` as a source directory.
- **Clean Build:** If you change `.proto` files, always run `mvn clean compile`.
### Updating proto files
- After editing `.proto` files, always re-run:
  ```sh
  mvn clean compile
  ```
