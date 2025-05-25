# Move to Kubernetes
- [Kubernetes commands](https://github.com/sbhrwl/system_design/blob/main/docs/deployment/containerisation/Kubernetes/k8scommands/README.md)
## Push images to Docker registry
- [Docker repository](https://hub.docker.com/repositories/sbhrwldocker)
```
docker push sbhrwldocker/ui-service:latest
docker push sbhrwldocker/sensor-service:latest
docker push sbhrwldocker/registration-service:latest
docker push sbhrwldocker/notification-service:latest
```
## Deployment files 
- [`ui-service.yaml`](ui-service.yaml)
- [`sensor-service.yaml`](sensor-service.yaml)
- [`registration-service.yaml`](registration-service.yaml)
- [`notification-service.yaml`](notification-service.yaml)
- Combined: [`orchestrate-sensor-services.yaml`](orchestrate-sensor-services.yaml)

## Apply
```bash
kubectl apply -f ui-service.yaml
kubectl apply -f sensor-service.yaml
kubectl apply -f registration-service.yaml
kubectl apply -f notification-service.yaml

kubectl apply -f orchestrate-sensor-services.yaml
```
- [Check status and perform other Kubernetes operations](https://github.com/sbhrwl/microservices/blob/main/motivation/generatemessage/kubernetes/README.md#deploy-docker-images-on-kubernetes)
## Access
* List of exposed URLs for your current services, assuming typical **NodePort** or **port-forwarding** access mappings for local development:
  * `http://localhost:30081/...` → `ui-service`
  * `http://localhost:30082/...` → `sensor-service`
  * `http://localhost:30083/...` → `registration-service`
  * `http://localhost:30084/...` → `notification-service`
  * `http://localhost:30080/...` → `keycloak` (if running locally on port 8080 and exposed as 30080)
* List of **internal Kubernetes service URLs** (accessible from within the cluster, such as by other services or pods):
  * `http://ui-service:9081/...` → `ui-service`
  * `http://sensor-service:9082/...` → `sensor-service`
  * `http://registration-service:9083/...` → `registration-service`
  * `http://notification-service:9084/...` → `notification-service`
  * `http://keycloak:8080/...` → `keycloak` (assuming it's deployed in the cluster with a service named `keycloak`)
- [`Failures with Kafka`](failuresWithKafka/README.md)
  ```
  # Important: host.docker.internal does NOT work from inside Kubernetes pods.
  # Replace with your host machine IP (e.g., 192.168.1.100) if Kafka is running on Docker Desktop.
  # This allows the pod to reach the Kafka broker running on your host.
  ```
