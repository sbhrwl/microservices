# Verification
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
- **Ingestion service**
```bash
[Ingestion service] [nio-9081-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 2 ms
[Ingestion service] [nio-9081-exec-1] c.e.i.c.MessageGeneratorController       : Attempting to send message to queue 'registration.queue': RegistrationRequestPojo{sensorId='sensor123', sensorModel='ModelX', email='user@example.com'}
[Ingestion service] [nio-9081-exec-1] c.e.i.c.MessageGeneratorController       : Successfully sent message to ActiveMQ: RegistrationRequestPojo{sensorId='sensor123', sensorModel='ModelX', email='user@example.com'}
[Ingestion service] [ntContainer#0-1] c.e.i.listener.ActiveMQMessageListener   : Received message from ActiveMQ: RegistrationRequestPojo{sensorId='sensor123', sensorModel='ModelX', email='user@example.com'}
[Ingestion service] [ntContainer#0-1] c.e.i.listener.ActiveMQMessageListener   : Mapped to Protobuf message: sensorId=sensor123, sensorModel=ModelX, email=user@example.com
[Ingestion service] [ntContainer#0-1] c.e.i.listener.ActiveMQMessageListener   : Sending gRPC request to Hub Service for sensor: sensor123
[Ingestion service] [ntContainer#0-1] c.e.i.listener.ActiveMQMessageListener   : Received gRPC response from Hub Service: Success=true, Message='Sensor registered successfully.'
```
- **Hub service**
```bash
[Hub service] [           main] c.e.hubservice.config.GrpcServerConfig   : Starting gRPC server on port 50051
[Hub service] [           main] c.e.hubservice.config.GrpcServerConfig   : gRPC server started successfully.
[Hub service] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 9082 (http) with context path ''
[Hub service] [           main] c.e.hubservice.HubServiceApplication     : Started HubServiceApplication in 14.681 seconds (process running for 17.458)
[Hub service] [ault-executor-0] c.e.h.service.RegistrationServiceImpl    : Received gRPC RegistrationRequestMessage: SensorId=sensor123, SensorModel=ModelX, Email=user@example.com
[Hub service] [ault-executor-0] c.e.h.service.RegistrationServiceImpl    : Processing registration for sensor: sensor123
[Hub service] [ault-executor-0] c.e.h.service.RegistrationServiceImpl    : Sending gRPC RegistrationResponseMessage: Success=true, Message='Sensor registered successfully.'
```
- **DB verification**
```sql
SELECT * FROM sensor_registrations;

SELECT * FROM sensor_registrations WHERE sensor_id = 'sensor123';

SELECT COUNT(*) FROM sensor_registrations;

SELECT * FROM sensor_registrations WHERE email = 'user@example.com';
```
