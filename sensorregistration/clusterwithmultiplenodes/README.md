# Deployment across 3 nodes 
- Setting Up a 3-Node Kubernetes Cluster with Docker Desktop and Kind (Kubernetes in Docker)
  - 1 control pane
  - 2 worker nodes

## Prerequisites
- Docker Desktop installed and running on Windows.
- [Chocolatey package manager](chocolatey/README.md) installed.
- PowerShell or CMD for running commands.
## Steps
- Verify Kubernetes is Running
  ```bash
  kubectl get nodes
  ````
- Install Kind
  - Kind is a tool for running Kubernetes clusters in Docker containers. You can install it using `Chocolatey`:
    ```powershell
    choco install kind -y
    ```
- Create a 3 node Kubernetes cluster
  - Run the following command in PowerShell to create a 2-node cluster (1 control-plane + 1 worker):
  - This will create a local Kubernetes cluster with `one control-plane node` and `one worker node`.
```bash
kind create cluster --name three-node-cluster --config - <<EOF
kind: Cluster
apiVersion: kind.x-k8s.io/v1alpha4
nodes:
  - role: control-plane
  - role: worker
  - role: worker
EOF
```
- Verify the Cluster Nodes
```bash
kubectl get nodes
```
- Label nodes
```
kubectl label node <worker-node-1-name> node-role=worker
kubectl label node <worker-node-2-name> node-role=worker
```
- Change to `values.yaml`
  - **`nodeSelector`** ensures pods run only on nodes with `node-role=worker`.
  - **`affinity`** (optional) encourages the scheduler to spread replicas across different nodes using anti-affinity to the same pod type.
```yaml
command:
  name: command-orchestration
  replicas: 1
  image: sbhrwldocker/command-orchestration:latest
  containerPort: 9081
  servicePort: 8081
  nodePort: 30081

  # Ensure this pod is scheduled only on worker nodes
  nodeSelector:
    node-role: worker

  # Optional: Distribute replicas across worker nodes if more than 1 replica
  affinity:
    podAntiAffinity:
      preferredDuringSchedulingIgnoredDuringExecution:
        - weight: 100
          podAffinityTerm:
            topologyKey: "kubernetes.io/hostname"
            labelSelector:
              matchExpressions:
                - key: app
                  operator: In
                  values:
                    - command-orchestration

task:
  name: task-orchestration
  replicas: 1
  image: sbhrwldocker/task-orchestration:latest
  containerPort: 9082
  servicePort: 8082
  nodePort: 30082

  # Ensure this pod is scheduled only on worker nodes
  nodeSelector:
    node-role: worker

  # Optional: Distribute replicas across worker nodes if more than 1 replica
  affinity:
    podAntiAffinity:
      preferredDuringSchedulingIgnoredDuringExecution:
        - weight: 100
          podAffinityTerm:
            topologyKey: "kubernetes.io/hostname"
            labelSelector:
              matchExpressions:
                - key: app
                  operator: In
                  values:
                    - task-orchestration

env:
  MONGO_HOST: host.docker.internal
  MONGO_PORT: "27017"
  SPRING_DATA_MONGODB_USERNAME: root
  SPRING_DATA_MONGODB_PASSWORD: root123
  SPRING_DATA_MONGODB_AUTHENTICATION_DATABASE: admin
  SPRING_DATA_MONGODB_DATABASE: commandorchestrator
  KAFKA_BOOTSTRAP_SERVERS: host.docker.internal:29092

commandOrchestration:
  autoscaling:
    enabled: true
    minReplicas: 2
    maxReplicas: 10
    targetCPUUtilizationPercentage: 75

taskOrchestration:
  autoscaling:
    enabled: true
    minReplicas: 2
    maxReplicas: 10
    targetCPUUtilizationPercentage: 75
```
