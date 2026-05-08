# dist/etc
- [application.conf](#application.conf)
- [logback.xml](#logback.xml)
- [logging.properties](logging.properties)
## application.conf
```java
flex-hub-connector {
  grpc-server {
    listen = 50051
    listen = ${?GRPC_SERVER_PORT}

    max-inbound-message-size = 4 MB
    max-inbound-message-size = ${?MAX_INBOUND_MESSAGE_SIZE}
  }

  remote-services {
    control-command-grpc-client {
      address = "gfc-core.gfc-01.svc.cluster.local:50051"
      address = ${?GFC_CORE_ADDRESS}

    }
  }
}
```
## logback.xml
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<configuration debug="false">

    <appender name="STDOUT" target="System.out" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%date{ISO8601} [%thread] %highlight(%-5level) %cyan(%logger{50}) - %msg %n</pattern>
        </encoder>
    </appender>

    <appender name="JSON_STDOUT" target="System.out" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="com.landisgyr.gfc.flexhub_connector.infrastructure.logging.StructuredLoggerEncoder"/>
    </appender>

    <logger name="com.landisgyr.gfc" level="${log.level:-INFO}"/>

    <root level="INFO">
        <appender-ref ref="${log.appender:-JSON_STDOUT}"/>
    </root>
</configuration>

```
## logging.properties
```
# register SLF4JBridgeHandler as handler for the j.u.l. root logger
handlers = org.slf4j.bridge.SLF4JBridgeHandler
# handlers= java.util.logging.ConsoleHandler
# .level= FINE

# Limit the message that are printed on the console to INFO and above.
#java.util.logging.ConsoleHandler.level = INFO
```
