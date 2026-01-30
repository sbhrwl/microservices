# File upload
- [Flexibilities import route](#flexibilities-import-route)
- [Dapr service invocation](#dapr-service-invocation)
- [gRPC service handler](#grpc-service-handler)
## Flexibilities import route
- [`routes/flexibilities-import.route.ts`](routes/flexibilities-import.route.ts)
- `curl -X POST http://localhost:4000/api/flexibilities/import -F file=@Flexibilities-L540.csv`
## Dapr service invocation
* Dapr sidecar discovers `gfc-core` service via mDNS
* Routes gRPC request to target service
```
API Gateway Dapr (port 4000) 
  ↓ 
gfc-core Dapr (port 50012)
  ↓
gfc-core gRPC (port 9090)
```
## gRPC service handler
* ServiceImpl: [`src/main/java/com/landisgyr/gfc/grpc/FlexibilityServiceImpl.java`](gfc-core/serviceimpl/FlexibilityServiceImpl.java)
  * [Documentation](gfc-core/serviceimpl/README.md)
