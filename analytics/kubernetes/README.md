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
docker push sbhrwldocker/ingestion-service:latest
```
## Deployment files 
- We need to configue deployment files such that ActiveMQ and InfluxDB are accessible from kubernetes
  - Set the right **`hostname/IP`** in the Deployment env vars so that Kubernetes pod can reach ActiveMQ and InfluxDB
  - Check IP address of your laptop where these are running as docker containers: `ipconfig` - **192.168.0.102**
  - **mongo-test**: `kubectl run mongo-test --rm -it --image=busybox --restart=Never -- sh`
    - `nc -zv 192.168.0.102 27017`
  - **kafka-test**: `kubectl run kafka-test --rm -it --image=busybox --restart=Never -- sh`
    - `nc -zv 192.168.0.102 29092`
  - **keycloak-test**: `kubectl run keycloak-test --rm -it --image=busybox --restart=Never -- sh`
    - `nc -zv 192.168.0.102 8080`
- [`ui-service.yaml`](ui-service.yaml)

## Cleanup containers created during development
- Stop running containers
```
docker ps --format "{{.Names}}" | grep -E 'ingestion-service' | xargs -r docker stop
```
- Remove stopped containers
```
docker ps -a --format "{{.Names}}" | grep -E 'ingestion-service' | xargs -r docker rm
```
- Prune dangling images
```
docker image prune -f
```
## Apply
```bash
kubectl apply -f ingestion-service.yaml

# Cleanup
kubectl delete -f ingestion-service.yaml
```
## Verify deployment
```
PS C:\Git\microservices\sensorregistration\kubernetes> kubectl apply -f ingestion-service.yaml
deployment.apps/ui-service created
service/ui-service created
PS C:\Git\microservices\sensorregistration\kubernetes> kubectl get pods
NAME                                    READY   STATUS    RESTARTS   AGE
ui-service-dfbb8d9df-jclm2              1/1     Running   0          17s
PS C:\Git\microservices\sensorregistration\kubernetes> kubectl get services
NAME                   TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
kubernetes             ClusterIP   10.96.0.1        <none>        443/TCP          23d
ui-service             NodePort    10.110.234.13    <none>        9081:30081/TCP   21s
PS C:\Git\microservices\sensorregistration\kubernetes>
```

- [Check status and perform other Kubernetes operations](https://github.com/sbhrwl/microservices/blob/main/motivation/generatemessage/kubernetes/README.md#deploy-docker-images-on-kubernetes)
## Access services
* List of exposed URLs for your current services, assuming typical **NodePort** or **port-forwarding** access mappings for local development:
  * `localhost:30081/api/powerquality/generate` → `ingestion-service`
## Cleanup
```
kubectl delete -f ingestion-service.yaml
```
