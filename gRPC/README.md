# Google Remote Procedure Call
- [Introduction](introduction/README.md)
- [Architecture](architecture/README.md)
- [Prerequisites](prerequisites/README.md)
- [Ingestion service](ingestion-service/README.md)
- [Hub service](hub-service/README.md)
- [Verification](#verification)

## Verification
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
- Ingestion service
```bash
[Ingestion service] [nio-9081-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 2 ms
[Ingestion service] [nio-9081-exec-1] c.e.i.c.MessageGeneratorController       : Attempting to send message to queue 'registration.queue': RegistrationRequestPojo{sensorId='sensor123', sensorModel='ModelX', email='user@example.com'}
[Ingestion service] [nio-9081-exec-1] c.e.i.c.MessageGeneratorController       : Successfully sent message to ActiveMQ: RegistrationRequestPojo{sensorId='sensor123', sensorModel='ModelX', email='user@example.com'}
[Ingestion service] [ntContainer#0-1] c.e.i.listener.ActiveMQMessageListener   : Received message from ActiveMQ: RegistrationRequestPojo{sensorId='sensor123', sensorModel='ModelX', email='user@example.com'}
[Ingestion service] [ntContainer#0-1] c.e.i.listener.ActiveMQMessageListener   : Mapped to Protobuf message: sensorId=sensor123, sensorModel=ModelX, email=user@example.com
[Ingestion service] [ntContainer#0-1] c.e.i.listener.ActiveMQMessageListener   : Sending gRPC request to Hub Service for sensor: sensor123
[Ingestion service] [ntContainer#0-1] c.e.i.listener.ActiveMQMessageListener   : Received gRPC response from Hub Service: Success=true, Message='Sensor registered successfully.'
```
- Hub service
```bash
[Hub service] [           main] c.e.hubservice.config.GrpcServerConfig   : Starting gRPC server on port 50051
[Hub service] [           main] c.e.hubservice.config.GrpcServerConfig   : gRPC server started successfully.
[Hub service] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 9082 (http) with context path ''
[Hub service] [           main] c.e.hubservice.HubServiceApplication     : Started HubServiceApplication in 14.681 seconds (process running for 17.458)
[Hub service] [ault-executor-0] c.e.h.service.RegistrationServiceImpl    : Received gRPC RegistrationRequestMessage: SensorId=sensor123, SensorModel=ModelX, Email=user@example.com
[Hub service] [ault-executor-0] c.e.h.service.RegistrationServiceImpl    : Processing registration for sensor: sensor123
[Hub service] [ault-executor-0] c.e.h.service.RegistrationServiceImpl    : Sending gRPC RegistrationResponseMessage: Success=true, Message='Sensor registered successfully.'
```
- DB verification
```sql
SELECT * FROM sensor_registrations;

SELECT * FROM sensor_registrations WHERE sensor_id = 'sensor123';

SELECT COUNT(*) FROM sensor_registrations;

SELECT * FROM sensor_registrations WHERE email = 'user@example.com';
```

---

## Kubernetes Deployment Plan with Helm & HPA:
 * Containerize Applications (Docker):
   * Create a Dockerfile for both the Ingestion Service and the Hub Service.
   * Build Docker images for each application (e.g., ingestion-service:latest, hub-service:latest).
   * Push these images to a container registry (e.g., Docker Hub, Google Container Registry).
 * Develop Helm Charts for Applications:
   * Create a Helm chart for the Ingestion Service and another for the Hub Service.
   * Each chart will define the Kubernetes resources for its respective application, using values.yaml for configurable parameters.
   * Chart Components (templates directory):
     * Deployment: Define the application pods, container images, resource requests/limits.
     * Service: Expose the application within the cluster (e.g., ClusterIP for Hub gRPC, NodePort/LoadBalancer/Ingress for Ingestion REST).
     * ConfigMap: Manage application.properties and other non-sensitive configurations.
     * Secret: Manage sensitive data like database passwords and ActiveMQ credentials.
     * Horizontal Pod Autoscaler (HPA): Define HPA resources to automatically scale pods based on CPU/Memory utilization or custom metrics.
 * Deploy Dependencies using Helm:
   * Utilize existing Helm charts for ActiveMQ (e.g., from Bitnami or official sources) and PostgreSQL to deploy them within your Kubernetes cluster.
   * Configure these charts via values.yaml to meet your service's connectivity requirements.
 * Deploy Applications to Kubernetes Cluster via Helm:
   * Install the Hub Service Helm chart.
   * Install the Ingestion Service Helm chart.
   * Use helm install <release-name> <chart-path> or helm upgrade --install commands.
 * Configure Horizontal Pod Autoscaler (HPA) within Charts:
   * Within each service's Helm chart, define the HPA object.
   * Specify the target CPU utilization percentage or memory usage, and the minimum/maximum number of replicas for scaling.
   * HPA will then automatically adjust the number of pods to meet demand.
This plan integrates Helm for managing your applications and their dependencies, and HPA for robust autoscaling.
