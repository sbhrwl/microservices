# Move to Kubernetes
- [`command-orchestration.yaml`](command-orchestration.yaml)
- [`task-orchestration.yaml`](task-orchestration.yaml)
- Combined: [`orchestration-services.yaml`](`orchestration-services.yaml`)

## Apply
```bash
kubectl apply -f command-orchestration.yaml
kubectl apply -f task-orchestration.yaml

kubectl apply -f orchestration-services.yaml
```

## Access
- `http://localhost:30081/...` → `commandorchestrationservice`
- `http://localhost:30082/...` → `taskorchestrationservice`
