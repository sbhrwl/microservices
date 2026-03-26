# Send commands
* [System context](#system-context)
* [Container view](#container-view)
* [Component view](#component-view)
* [Code mapping](#code-mapping)
## System context
- Who interacts with your system?
<img src="images/system-context.jpg">

<details>
  <summary>mermaid</summary>

```mermaid
flowchart TD
  Client["External Client<br/>(calls gRPC)"]
  Dapr["Dapr Sidecar"]
  System["IEC 61968 Connector Service"]
  Broker["Message Broker"]
  Downstream["Downstream Systems"]

  Client --> Dapr
  Dapr --> System
  System --> Broker
  Broker --> Downstream
```
</details>

## Container view
- What are the major building blocks inside?
  - **gRPC adapter** → entry point
  - **mapping layer** → intent → schema model
  - **jaxb layer** → object → XML
  - **routing** → delivery via Camel
- This behaves like an **assembly line**
<img src="images/container-view.jpg">

<details>
  <summary>mermaid</summary>

```mermaid
flowchart TD
  subgraph ClientSide
    Client["Client"]
  end

  subgraph Service["IEC 61968 Connector"]
    Grpc["gRPC Adapter"]
    Mapping["Mapping Layer"]
    Jaxb["JAXB / XML Layer"]
    Routing["Routing (Camel)"]
  end

  Broker["Message Broker"]

  Client --> Grpc
  Grpc --> Mapping
  Mapping --> Jaxb
  Jaxb --> Routing
  Routing --> Broker
```
</details>

## Component view
- What are the real moving parts (your actual classes)?
  - The **most critical boundary** sits at:
    - `ProtoToJaxbMapper`
<img src="images/component-view.jpg">

<details>
  <summary>mermaid</summary>

```mermaid
flowchart TD
  subgraph GrpcLayer
    A["DeviceInteractionImpl"]
    B["TenantIdInterceptor"]
  end

  subgraph MappingLayer
    C["ProtoMapper"]
    D["ProtoToJaxbMapper"]
    E["XML Helpers<br/>(DeviceFlexibilityIdMapper, etc.)"]
  end

  subgraph JaxbLayer
    F["JaxbUtil"]
    G["JaxbConfigFactory"]
    H["Adapters<br/>(DateAdapter, ZonedDateTimeAdapter)"]
  end

  subgraph RoutingLayer
    I["MessageRouteBuilder"]
  end

  A --> C
  C --> D
  D --> E
  E --> F
  F --> I
  G --> F
  H --> F
```
</details>

## Code mapping
- How does the build enforce architecture?
  - Schema is not documentation, Schema is **compiled truth**
  - If the schema changes, your code must obey or fail
<img src="images/code-mapping.jpg">

<details>
  <summary>mermaid</summary>

```mermaid
flowchart TD
  XSD["XSD Schemas"]
  XJB["XJB Bindings"]
  Gradle["Gradle generateJaxb"]
  JAXB["Generated JAXB Classes"]
  Compile["compileJava"]
  Code["Application Code"]

  XSD --> Gradle
  XJB --> Gradle
  Gradle --> JAXB
  JAXB --> Compile
  Compile --> Code
```
</details>
