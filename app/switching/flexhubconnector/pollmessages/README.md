# Poll messages
```mermaid
flowchart TD
    A["Bootstrap main"] --> B["Main run"]
    B --> C["loadConfig"]
    C --> D["DaggerApplicationComponent.create(config)"]
    D --> E["Application.start()"]
    E --> F["camelContext.start()"]
    F --> G["ScheduledCamelRoutes timer starts"]
    G --> H["peek-messages fires"]
    H --> I["PeekMessagesUseCase.peekMessages()"]
    I --> J["for each tenant"]
    J --> K["SoapClientAdapter.peekMessage()"]
    K --> L["direct:peek"]
    L --> M["OutboundCamelRoutes"]
    M --> N["SOAP peekMessage to Data Hub"]
    N --> O{"message found?"}
    O -- yes --> P["sendRelayControlCmd to gRPC"]
    P --> Q["SoapClientAdapter.dequeue()"]
    Q --> R["direct:dequeueMessage"]
    R --> S["SOAP dequeueMessage"]
    O -- no --> T["stop tenant loop"]
```

Files
- Main.java: C:\Git\gfc-app\flex-hub-connector\src\main\java\com\landisgyr\gfc\flexhub_connector\app\Main.java
- Application.java: C:\Git\gfc-app\flex-hub-connector\src\main\java\com\landisgyr\gfc\flexhub_connector\infrastructure\di\Application.java
- ScheduledCamelRoutes.java: C:\Git\gfc-app\flex-hub-connector\src\main\java\com\landisgyr\gfc\flexhub_connector\adapters\inbound\scheduler\ScheduledCamelRoutes.java
- PeekMessagesUseCase.java: C:\Git\gfc-app\flex-hub-connector\src\main\java\com\landisgyr\gfc\flexhub_connector\app\usecase\PeekMessagesUseCase.java
- SoapClientAdapter.java: C:\Git\gfc-app\flex-hub-connector\src\main\java\com\landisgyr\gfc\flexhub_connector\adapters\outbound\soap\SoapClientAdapter.java
- OutboundCamelRoutes.java: C:\Git\gfc-app\flex-hub-connector\src\main\java\com\landisgyr\gfc\flexhub_connector\adapters\outbound\soap\OutboundCamelRoutes.java
