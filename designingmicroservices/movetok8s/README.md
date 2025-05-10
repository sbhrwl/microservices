# Move to Kubernetes
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
- [Status check for kubernetees pods and services](https://github.com/sbhrwl/microservices/blob/main/kubernetes/README.md#deploy-docker-images-on-kubernetes)
```bash
kubectl apply -f command-orchestration.yaml
kubectl apply -f task-orchestration.yaml

kubectl apply -f orchestration-services.yaml
```

## Access
- `http://localhost:30081/...` → `commandorchestrationservice`
- `http://localhost:30082/...` → `taskorchestrationservice`
- [`Failures with Kafka`](failuresWithKafka/README.md)
## Helm charts
