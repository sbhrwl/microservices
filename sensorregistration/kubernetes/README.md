# Move to Kubernetes
- [Kubernetes commands](https://github.com/sbhrwl/system_design/blob/main/docs/deployment/containerisation/Kubernetes/k8scommands/README.md)
## Push images to Docker registry
- [Docker repository](https://hub.docker.com/repositories/sbhrwldocker)
```
docker push sbhrwldocker/command-orchestration:latest

docker push sbhrwldocker/task-orchestration:latest
```
## Deployment files 
- [`command-orchestration.yaml`](command-orchestration.yaml)
- [`task-orchestration.yaml`](task-orchestration.yaml)
- Combined: [`orchestration-services.yaml`](orchestration-services.yaml)

## Apply
```bash
kubectl apply -f command-orchestration.yaml
kubectl apply -f task-orchestration.yaml

kubectl apply -f orchestration-services.yaml
```
- [Check status and perform other Kubernetes operations](https://github.com/sbhrwl/microservices/blob/main/services/generatemessageservice/kubernetes/README.md#deploy-docker-images-on-kubernetes)
## Access
- `http://localhost:30081/...` → `commandorchestrationservice`
- `http://localhost:30082/...` → `taskorchestrationservice`
- [`Failures with Kafka`](failuresWithKafka/README.md)
  ```
  # Important: host.docker.internal does NOT work from inside Kubernetes pods.
  # Replace with your host machine IP (e.g., 192.168.1.100) if Kafka is running on Docker Desktop.
  # This allows the pod to reach the Kafka broker running on your host.
  ```
