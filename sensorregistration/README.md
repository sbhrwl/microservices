# Sensor registration
- [Introduction](introduction/README.md)
- [Architecture](architecture/README.md)
- [Prerequisites](prerequisites/README.md)
- [Services](sensor-registration/README.md)
  - [UI service](sensor-registration/ui-service/README.md)
  - [Sensor service](sensor-registration/sensor-service/README.md)
  - [Registration service](sensor-registration/registration-service/README.md)
  - [Notification service](sensor-registration/notification-service/README.md)
- [Containers](containers/README.md)
- [Helm charts](helmcharts/README.md)
- [Verification](#verification)

## Verification
- Get Access token `http://localhost:8080/realms/master/protocol/openid-connect/token`
  - Modify `Body -> x-wwww-form-url-encoded`
     ```
     grant_type : password
     client_id  : sensor-service (created in Keycloak)
     username  : endpointaccessuser
     password  : password123
     ```
- Push data to Broker
  - `POST`: `http://localhost:9081/generate/registration`
  - Payload
    ```json
      {
          "sensorId": "sensor123",
          "sensorModel": "ModelX",
          "email": "user@example.com"
      }
    ```
- **Sensor service**
```bash
[nio-9082-exec-5] c.e.s.service.KafkaProducerService       : Sending message to Kafka: {"sensorId":"sensor123","sensorModel":"ModelX","email":"user@example.com"}
[nio-9082-exec-5] o.a.k.clients.producer.KafkaProducer     : [Producer clientId=producer-1] Instantiated an idempotent producer.
[nio-9082-exec-5] o.a.kafka.common.utils.AppInfoParser     : Kafka version: 3.6.2
[nio-9082-exec-5] o.a.kafka.common.utils.AppInfoParser     : Kafka commitId: c4deed513057c94e
[nio-9082-exec-5] o.a.kafka.common.utils.AppInfoParser     : Kafka startTimeMs: 1750764150119
[ad | producer-1] org.apache.kafka.clients.Metadata        : [Producer clientId=producer-1] Cluster ID: eFaWNFroQdyeJp4hZKhdog
[ad | producer-1] o.a.k.c.p.internals.TransactionManager   : [Producer clientId=producer-1] ProducerId set to 1000 with epoch 0
[ad | producer-1] c.e.s.service.KafkaProducerService       : Sent sensor registration message with sensorId: {"sensorId":"sensor123","sensorModel":"ModelX","email":"user@example.com"}
```
- **Registration service**
```bash
[ntainer#0-0-C-1] c.e.r.consumer.RegistrationConsumer      : Received message from 'sensor-registrations': ConsumerRecord(topic = sensor-registrations, partition = 0, leaderEpoch = 0, offset = 0, CreateTime = 1750764150494, serialized key size = -1, serialized value size = 74, headers = RecordHeaders(headers = [], isReadOnly = false), key = null, value = {"sensorId":"sensor123","sensorModel":"ModelX","email":"user@example.com"})
[ntainer#0-0-C-1] c.e.r.consumer.RegistrationConsumer      : Successfully saved registration for sensor ID: sensor123
[ntainer#0-0-C-1] c.e.r.consumer.RegistrationConsumer      : Successfully sent notification email for sensor ID: sensor123
```
- **Notification service**
```bash
[nio-9084-exec-1] c.e.n.service.EmailService               : Simulating sending registration confirmation email to: user@example.com for sensor ID: sensor123
```
