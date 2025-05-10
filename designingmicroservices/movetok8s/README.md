# Move to Kubernetes
## [`command-orchestration.yaml`](command-orchestration.yaml)

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: command-orchestration-deployment
spec:
  replicas: 1
  selector:
    matchLabels:
      app: command-orchestration
  template:
    metadata:
      labels:
        app: command-orchestration
    spec:
      containers:
      - name: command-orchestration
        image: your-dockerhub/commandorchestration:latest
        ports:
        - containerPort: 9081
        env:
        - name: SPRING_DATA_MONGODB_URI
          value: mongodb://host.docker.internal:27017/yourdb
        - name: KAFKA_BOOTSTRAP_SERVERS
          value: host.docker.internal:9092
        - name: SERVER_PORT
          value: "9081"
---
apiVersion: v1
kind: Service
metadata:
  name: command-orchestration-service
spec:
  type: NodePort
  selector:
    app: command-orchestration
  ports:
    - protocol: TCP
      port: 8081
      targetPort: 9081
      nodePort: 30081
```
## [`task-orchestration.yaml`](task-orchestration.yaml)

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: task-orchestration-deployment
spec:
  replicas: 1
  selector:
    matchLabels:
      app: task-orchestration
  template:
    metadata:
      labels:
        app: task-orchestration
    spec:
      containers:
      - name: task-orchestration
        image: your-dockerhub/taskorchestration:latest
        ports:
        - containerPort: 9082
        env:
        - name: SPRING_DATA_MONGODB_URI
          value: mongodb://host.docker.internal:27017/yourdb
        - name: KAFKA_BOOTSTRAP_SERVERS
          value: host.docker.internal:9092
        - name: SERVER_PORT
          value: "9082"
---
apiVersion: v1
kind: Service
metadata:
  name: task-orchestration-service
spec:
  type: NodePort
  selector:
    app: task-orchestration
  ports:
    - protocol: TCP
      port: 8082
      targetPort: 9082
      nodePort: 30082
```
## Apply
```bash
kubectl apply -f command-orchestration.yaml
kubectl apply -f task-orchestration.yaml
```

## Access
* `http://localhost:30081/...` → `commandorchestrationservice`
* `http://localhost:30082/...` → `taskorchestrationservice`
