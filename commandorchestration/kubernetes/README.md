# Move to Kubernetes
- [Kubernetes commands](https://github.com/sbhrwl/system_design/blob/main/docs/deployment/containerisation/Kubernetes/k8scommands/README.md)
- [Push images to Docker registry](#push-images-to-docker-registry)
- [Deployment files](#deployment-files)
- [Cleanup containers created during development](#cleanup-containers-created-during-development)
- [Apply](#apply)
- [Verify deployment](#verify-deployment)
- [Access services](#access-services)
- [Cleanup](#cleanup)
## Push images to Docker registry
- [Docker repository](https://hub.docker.com/repositories/sbhrwldocker)
```
docker push sbhrwldocker/task-orchestrator:latest
docker push sbhrwldocker/command-orchestrator:latest
docker push sbhrwldocker/protocol-gateway:latest
docker push sbhrwldocker/sensor-simulator:latest
```
## Deployment files 
- We need to configue deployment files such that MongoDB, Kafka and Keycloak are accessible from kubernetes
  - Set the right **`hostname/IP`** in the Deployment env vars so that Kubernetes pod can reach MongoDB, Kafka and Keycloak
  - Check IP address of your laptop where these are running as docker containers: `ipconfig` - **192.168.0.102**
  - **mongo-test**: `kubectl run mongo-test --rm -it --image=busybox --restart=Never -- sh`
    - `nc -zv 192.168.0.102 27017`
  - **kafka-test**: `kubectl run kafka-test --rm -it --image=busybox --restart=Never -- sh`
    - `nc -zv 192.168.0.102 29092`
  - **keycloak-test**: `kubectl run keycloak-test --rm -it --image=busybox --restart=Never -- sh`
    - `nc -zv 192.168.0.102 8080`
- [`task-orchestrator.yaml`](task-orchestrator.yaml)
- [`command-orchestrator.yaml`](command-orchestrator.yaml)
- [`protocol-gateway.yaml`](protocol-gateway.yaml)
- [`sensor-simulator.yaml`](sensor-simulator.yaml)
- Combined: [`orchestrate-command-services.yaml`](orchestrate-command-services.yaml)

## Cleanup containers created during development
- Stop running containers
```
docker ps --format "{{.Names}}" | grep -E 'command-orchestrator|task-orchestrator|protocol-gateway|sensor-simulator' | xargs -r docker stop
```
- Remove stopped containers
```
docker ps -a --format "{{.Names}}" | grep -E 'command-orchestrator|task-orchestrator|protocol-gateway|sensor-simulator' | xargs -r docker rm
```
- Prune dangling images
```
docker image prune -f
```
## Apply
```bash
kubectl apply -f task-orchestrator.yaml
kubectl apply -f command-orchestrator.yaml
kubectl apply -f protocol-gateway.yaml
kubectl apply -f sensor-simulator.yaml
kubectl apply -f orchestrate-command-services.yaml

# Cleanup
kubectl delete -f orchestrate-command-services.yaml
kubectl delete -f task-orchestrator.yaml
kubectl delete -f command-orchestrator.yaml
kubectl delete -f protocol-gateway.yaml
kubectl delete -f sensor-simulator.yaml
```
## Verify deployment
```
PS C:\Git\microservices\commandorchestration\kubernetes> kubectl apply -f orchestrate-command-services.yaml
deployment.apps/task-orchestrator created
service/task-orchestrator created
deployment.apps/command-orchestrator created
service/command-orchestrator created
deployment.apps/protocol-gateway created
deployment.apps/sensor-simulator created
service/sensor-simulator created
PS C:\Git\microservices\commandorchestration\kubernetes> kubectl get pods
NAME                                   READY   STATUS    RESTARTS   AGE
command-orchestrator-8589d7f68-7q6bd   1/1     Running   0          28s
protocol-gateway-f468cfc55-j9d6z       1/1     Running   0          27s
sensor-simulator-5b7686d8c-qqmt5       1/1     Running   0          27s
task-orchestrator-7cb78c878-88khr      1/1     Running   0          28s
PS C:\Git\microservices\commandorchestration\kubernetes> kubectl get services
NAME                   TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
command-orchestrator   ClusterIP   10.104.94.116   <none>        9082/TCP         36s
kubernetes             ClusterIP   10.96.0.1       <none>        443/TCP          35d
sensor-simulator       ClusterIP   10.98.42.15     <none>        9084/TCP         35s
task-orchestrator      NodePort    10.107.144.94   <none>        9081:30081/TCP   36s
```

- [Check status and perform other Kubernetes operations](https://github.com/sbhrwl/microservices/blob/main/motivation/generatemessage/kubernetes/README.md#deploy-docker-images-on-kubernetes)
## Access services
* List of exposed URLs for your current services, assuming typical **NodePort** or **port-forwarding** access mappings for local development:
  * `http://localhost:30081/` → `task-orchestrator`
  * ClusterIP service → `command-orchestrator`
  * Kafka listener → `protocol-gateway`
  * ClusterIP service → `sensor-simulator`
## Cleanup
```
kubectl delete -f orchestrate-command-services.yaml
```
