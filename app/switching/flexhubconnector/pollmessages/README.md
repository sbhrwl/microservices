# Poll messages
```mermaid
flowchart TD
    A[Bootstrap.main()] --> B[Main.run()]
    B --> C[loadConfig()]
    C --> D[DaggerApplicationComponent.create(config)]
    D --> E[Application built by Dagger]
    E --> F[app.start()]
    F --> G[camelContext.start()]
    G --> H[ScheduledCamelRoutes timer starts]
    H --> I[peek-messages fires]
    I --> J[PeekMessagesUseCase.peekMessages()]
    J --> K{for each tenant}
    K --> L[SoapClientAdapter.peekMessage()]
    L --> M[direct:peek]
    M --> N[OutboundCamelRoutes]
    N --> O[SOAP peekMessage to Data Hub]
    O --> P{message found?}
    P -- yes --> Q[SendRelayControlCommand to gRPC]
    Q --> R[SoapClientAdapter.dequeue()]
    R --> S[direct:dequeueMessage]
    S --> T[SOAP dequeueMessage]
    P -- no --> U[stop loop for tenant]
```

Files
- Main.java: C:\Git\gfc-app\flex-hub-connector\src\main\java\com\landisgyr\gfc\flexhub_connector\app\Main.java
- Application.java: C:\Git\gfc-app\flex-hub-connector\src\main\java\com\landisgyr\gfc\flexhub_connector\infrastructure\di\Application.java
- ScheduledCamelRoutes.java: C:\Git\gfc-app\flex-hub-connector\src\main\java\com\landisgyr\gfc\flexhub_connector\adapters\inbound\scheduler\ScheduledCamelRoutes.java
- PeekMessagesUseCase.java: C:\Git\gfc-app\flex-hub-connector\src\main\java\com\landisgyr\gfc\flexhub_connector\app\usecase\PeekMessagesUseCase.java
- SoapClientAdapter.java: C:\Git\gfc-app\flex-hub-connector\src\main\java\com\landisgyr\gfc\flexhub_connector\adapters\outbound\soap\SoapClientAdapter.java
- OutboundCamelRoutes.java: C:\Git\gfc-app\flex-hub-connector\src\main\java\com\landisgyr\gfc\flexhub_connector\adapters\outbound\soap\OutboundCamelRoutes.java
