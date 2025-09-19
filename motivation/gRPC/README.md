# gRPC setup
- [Configure pom xml](#configure-pom-xml)
- [Define Protocol Buffer file](#define-protocol-buffer-file)
- [Generate gRPC code](#generate-grpc-code)
- [Implement the gRPC service](#implement-the-grpc-service)
- [Configure Spring Boot](#configure-spring-boot)
- [Create a gRPC Client](#create-a-grpc-client)
## Configure pom xml
- Versions in `properties` tag
```xml
<properties>
    <java.version>17</java.version>
    <grpc.version>1.59.0</grpc.version>
    <protobuf.version>3.24.4</protobuf.version>
    <os-maven-plugin.version>1.7.1</os-maven-plugin.version>
    <protobuf-plugin.version>0.6.1</protobuf-plugin.version>
    <grpc-spring-boot-starter.version>2.15.0.RELEASE</grpc-spring-boot-starter.version>
</properties>
```
- Dependencies
```xml
<dependency>
    <groupId>net.devh</groupId>
    <artifactId>grpc-client-spring-boot-starter</artifactId>
    <version>${grpc-spring-boot-starter.version}</version>
</dependency>
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-protobuf</artifactId>
    <version>${grpc.version}</version>
</dependency>
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-stub</artifactId>
    <version>${grpc.version}</version>
</dependency>
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-netty-shaded</artifactId>
    <version>${grpc.version}</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>javax.annotation</groupId>
    <artifactId>javax.annotation-api</artifactId>
    <version>1.3.2</version>
</dependency>
```
- Plugins
```xml
<plugin>
    <groupId>org.xolstice.maven.plugins</groupId>
    <artifactId>protobuf-maven-plugin</artifactId>
    <version>${protobuf-plugin.version}</version>
    <configuration>
        <protocArtifact>com.google.protobuf:protoc:${protobuf.version}:exe:${os.detected.classifier}</protocArtifact>
        <pluginId>grpc-java</pluginId>
        <pluginArtifact>io.grpc:protoc-gen-grpc-java:${grpc.version}:exe:${os.detected.classifier}</pluginArtifact>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>compile</goal>
                <goal>compile-custom</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
## Define Protocol Buffer file
- First, define the structure of your gRPC service and the messages it will use.
- Create a file with a `.proto` extension.
- This file specifies the service's methods, and the structure of the data it sends and receives.
- Example `data.proto` file:
```protobuf
syntax = "proto3";

// Defines the Java package and class names for the generated code.
option java_multiple_files = true;
option java_package = "com.apexsphere.storage_service.service";
option java_outer_classname = "RecordProto";

// The service definition. This will contain the RPC methods.
service RecordService {
    // Add this line to define the method for saving a record
    rpc saveRecord (RecordRequest) returns (RecordResponse);
}

// A message to represent the data you want to send and receive.
// It mirrors the fields in your Record class.
message RecordRequest {
    string sensor_id = 1;
    string operation = 2;
    int32 relay_number = 3;
    int32 duration = 4;
    string status = 5;
}

// A message for the response after saving a record.
message RecordResponse {
    bool success = 1;
    string message = 2;
}
```

## Generate gRPC code
- `mvn clean install`
  - Generates the necessary Java classes from `.proto` file.
  - This includes the `service interface` and the `message classes`.
## Implement the gRPC service
- Create a new Spring Boot class that implements the generated gRPC service interface.
- This class will contain the business logic, similar to your previous REST controller.
- You'll need to annotate it with `@GrpcService`.
```java
package com.apexsphere.storage_service.service;

import com.apexsphere.storage_service.model.Record;
import com.apexsphere.storage_service.service.RecordServiceGrpc; // The generated class
import com.apexsphere.storage_service.service.RecordRequest; // The generated message
import com.apexsphere.storage_service.service.RecordResponse; // The generated message
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class RecordGrpcServiceImpl extends RecordServiceGrpc.RecordServiceImplBase {

    private final RecordService recordService; // Inject your existing service

    @Autowired
    public RecordGrpcServiceImpl(RecordService recordService) {
        this.recordService = recordService;
    }

    @Override
    public void saveRecord(RecordRequest request, StreamObserver<RecordResponse> responseObserver) {
        try {
            // Map the gRPC request to your existing domain model
            Record record = new Record(
                request.getSensorId(),
                request.getOperation(),
                request.getRelayNumber(),
                request.getDuration(),
                request.getStatus()
            );

            // Call the existing business logic method
            Record savedRecord = recordService.saveRecord(record);

            // Build and send the gRPC response
            RecordResponse response = RecordResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Record saved successfully with ID: " + savedRecord.getId())
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            // Handle any exceptions during the process
            RecordResponse response = RecordResponse.newBuilder()
                .setSuccess(false)
                .setMessage("Failed to save record: " + e.getMessage())
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
```

## Configure Spring Boot
- Add the necessary gRPC dependencies to your `pom.xml` (Maven) or `build.gradle` (Gradle) file.
- Also, configure your `application.properties` to specify the gRPC server port.
```properties
grpc.server.port=9090
```

## Create a gRPC Client 
- Optional
- To test your new service, you can create a simple gRPC client that communicates with it.
- This client will also use the generated code to send the request and receive the response.
```java
import com.example.grpc.service.DataServiceGrpc;
import com.example.grpc.service.DataRequest;
import com.example.grpc.service.DataResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GrpcClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
            .usePlaintext() // For development only
            .build();

        DataServiceGrpc.DataServiceBlockingStub stub = DataServiceGrpc.newBlockingStub(channel);

        DataRequest request = DataRequest.newBuilder()
            .setSomeField("Hello gRPC")
            .setAnotherField(123)
            .build();

        DataResponse response = stub.saveData(request);

        System.out.println("Response from server: " + response.getMessage());

        channel.shutdown();
    }
}
```
