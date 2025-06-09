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

kubectl apply -f orchestrate-command-orchestrators.yaml

# Cleanup
kubectl delete -f orchestrate-command-orchestrators.yaml
```
## Verify deployment
```
PS C:\Git\microservices\sensorregistration\kubernetes> kubectl apply -f orchestrate-command-orchestrators.yaml
configmap/sensor-simulator-config created
deployment.apps/sensor-simulator created
service/sensor-simulator created
deployment.apps/protocol-gateway created
service/protocol-gateway created
deployment.apps/command-orchestrator created
service/command-orchestrator created
deployment.apps/task-orchestrator created
service/task-orchestrator created
PS C:\Git\microservices\sensorregistration\kubernetes> kubectl get pods
NAME                                    READY   STATUS    RESTARTS   AGE
sensor-simulator-7f5845c77c-dchcf   1/1     Running   0          17s
protocol-gateway-86d67ff64-tf8mh    1/1     Running   0          17s
command-orchestrator-74567d6548-c2fgp         1/1     Running   0          17s
task-orchestrator-dfbb8d9df-jclm2              1/1     Running   0          17s
PS C:\Git\microservices\sensorregistration\kubernetes> kubectl get services
NAME                   TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
kubernetes             ClusterIP   10.96.0.1        <none>        443/TCP          23d
sensor-simulator   ClusterIP   10.104.141.255   <none>        9084/TCP         21s
protocol-gateway   ClusterIP   10.97.150.246    <none>        9083/TCP         21s
command-orchestrator         NodePort    10.104.180.39    <none>        9082:30082/TCP   21s
task-orchestrator             NodePort    10.110.234.13    <none>        9081:30081/TCP   21s
PS C:\Git\microservices\sensorregistration\kubernetes>
```

- [Check status and perform other Kubernetes operations](https://github.com/sbhrwl/microservices/blob/main/motivation/generatemessage/kubernetes/README.md#deploy-docker-images-on-kubernetes)
## Access services
* List of exposed URLs for your current services, assuming typical **NodePort** or **port-forwarding** access mappings for local development:
  * `http://localhost:30081/` → `task-orchestrator`
  * `http://localhost:30082/api/register/sensor` → `command-orchestrator`
  * ClusterIP service → `protocol-gateway`
  * ClusterIP service → `sensor-simulator`
## Cleanup
```
kubectl delete -f orchestrate-command-orchestrators.yaml
```
