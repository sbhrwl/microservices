# Hub to Sensor
- [Introduction](introduction/README.md)
- [Architecture](introduction/architecture/README.md)
- [Prerequisites](prerequisites/README.md)
- [Services](hub-to-sensor/README.md)
  - [Flexibility Hub simulator](hub-to-sensor/flexibility-hub-simulator/README.md)
    - [Production version](hub-to-sensor/flexibility-hub-simulator-prod/README.md)
  - [Flexibility bridge service](hub-to-sensor/flexibility-bridge-service/README.md)
  - [Storage service gRPC](hub-to-sensor/storage-service-grpc/README.md)
    - [Storage service restful](hub-to-sensor/storage-service/README.md)
  - [Protocol adapter service](hub-to-sensor/protocol-adapter-service/README.md)
  - [HES simulator](hub-to-sensor/hes-simulator/README.md)
  - [Data API service](hub-to-sensor/data-api-service/README.md)
  - [UI app](hub-to-sensor/ui-app/README.md)
- [Containers](containers/README.md)
- [Kubernetes](kubernetes/README.md)
- [Helm charts](helmcharts/README.md)
- [Horizontal Pod Autoscalar](hpa/README.md)
- [Deployment across environments](deploymentacrossenv/README.md)
- [Service discovery](servicediscovery/README.md)
- [Ingress](ingress/README.md)
- [Service mesh](servicemesh/README.md)
- [DAPR](dapr/README.md)
- [Verification](verification/README.md)

---

```
PS C:\Git\microservices\hubToSensor\ingress\trial\orchestrate-hubtosensor-services> kubectl exec -it data-api-546dbcd7c5-wbwfs -n staging -- /bin/sh -c "curl -s localhost:8085/api/v1/requests"
[{"id":65,"duration":0,"operation":"DIRECT-ON","relayNumber":1,"sensorId":"sensor-001","status":"Request staus: Completed"},{"id":71,"duration":0,"operation":"DIRECT-ON","relayNumber":1,"sensorId":"sensor-001","status":"Request staus: Completed"},{"id":66,"duration":0,"operation":"DIRECT-ON","relayNumber":1,"sensorId":"sensor-001","status":"Request staus: Completed"},{"id":61,"duration":0,"operation":"DIRECT-OFF","relayNumber":1,"sensorId":"sensor-004","status":"Request staus: Completed"},{"id":67,"duration":0,"operation":"DIRECT-ON","relayNumber":1,"sensorId":"sensor-001","status":"Request staus: Completed"},{"id":62,"duration":0,"operation":"DIRECT-ON","relayNumber":1,"sensorId":"sensor-001","status":"Request staus: Completed"},{"id":68,"duration":0,"operation":"DIRECT-ON","relayNumber":1,"sensorId":"sensor-001","status":"Request staus: Failed (Code: 404, Msg: Target sensor 'sensor-001' not found or offline.)"},{"id":63,"duration":0,"operation":"DIRECT-ON","relayNumber":1,"sensorId":"sensor-001","status":"Request staus: Failed (Code: 404, Msg: Target sensor 'sensor-001' not found or offline.)"},{"id":69,"duration":0,"operation":"DIRECT-ON","relayNumber":1,"sensorId":"sensor-001","status":"Request staus: Failed (Code: 404, Msg: Target sensor 'sensor-001' not found or offline.)"},{"id":64,"duration":0,"operation":"DIRECT-ON","relayNumber":1,"sensorId":"sensor-001","status":"Request staus: Completed"},{"id":70,"duration":0,"operation":"DIRECT-ON","relayNumber":1,"sensorId":"sensor-001","status":"Request staus: Completed"}]
PS C:\Git\microservices\hubToSensor\ingress\trial\orchestrate-hubtosensor-services> curl -I -k https://fhs.local/data-api/api/v1/requests
```