# Move to Kubernetes
## Push images to Docker registry 
```
docker push sbhrwldocker/command-orchestration:latest

docker push sbhrwldocker/task-orchestration:latest
```
## Deployment files 
- [`command-orchestration.yaml`](command-orchestration.yaml)
- [`task-orchestration.yaml`](task-orchestration.yaml)
- Combined: [`orchestration-services.yaml`](orchestration-services.yaml)

## Apply
- [Troubleshooting](https://github.com/sbhrwl/microservices/blob/main/kubernetes/README.md#deploy-docker-images-on-kubernetes)
```bash
kubectl apply -f command-orchestration.yaml
kubectl apply -f task-orchestration.yaml

kubectl apply -f orchestration-services.yaml
```

## Access
- `http://localhost:30081/...` → `commandorchestrationservice`
- `http://localhost:30082/...` → `taskorchestrationservice`

## Helm charts

