# Move to Kubernetes
- [`command-orchestration.yaml`](command-orchestration.yaml)
- [`task-orchestration.yaml`](task-orchestration.yaml)

## Apply
```bash
kubectl apply -f command-orchestration.yaml
kubectl apply -f task-orchestration.yaml
```

## Access
- `http://localhost:30081/...` → `commandorchestrationservice`
- `http://localhost:30082/...` → `taskorchestrationservice`
