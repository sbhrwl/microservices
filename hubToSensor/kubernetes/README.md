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
docker push sbhrwldocker/flexibility-hub-simulator:latest
docker push sbhrwldocker/storage-service:latest
docker push sbhrwldocker/flexibility-bridge:latest
docker push sbhrwldocker/hes-simulator:latest
```
## Deployment files 
- We need to configue deployment files such that rabbitmq and postgresql are accessible from kubernetes
  - Set the right **`hostname/IP`** in the Deployment env vars so that Kubernetes pod can reach rabbitmq and postgresql
  - Check IP address of your laptop where these are running as docker containers: `ipconfig` - **192.168.0.103**
  - **rabbitmq-test**: `kubectl run rabbitmq-test --rm -it --image=busybox --restart=Never -- sh`
    - `nc -zv 192.168.0.103 15672`
  - **postgresql-test**: `kubectl run postgresql-test --rm -it --image=busybox --restart=Never -- sh`
    - `nc -zv 192.168.0.103 5432`
- [`flexibility-hub-simulator.yaml`](flexibility-hub-simulator.yaml)
- [`storage-service.yaml`](storage-service.yaml)
- [`flexibility-bridge.yaml`](flexibility-bridge.yaml)
- [`protocol-adapter.yaml`](protocol-adapter.yaml)
- [`hes-simulator.yaml`](hes-simulator.yaml)
- [`data-api.yaml`](data-api.yaml)
- [`ui-app.yaml`](ui-app.yaml)
- Combined: [`orchestrate-hubtosensor-services.yaml`](orchestrate-hubtosensor-services.yaml)

## Cleanup containers created during development
- Stop running containers
```
docker ps --format "{{.Names}}" | grep -E 'flexibility-hub-simulator|storage-service|flexibility-bridge|protocol-adapter|hes-simulator|data-api|ui-app' | xargs -r docker stop
```
- Remove stopped containers
```
docker ps -a --format "{{.Names}}" | grep -E 'flexibility-hub-simulator|storage-service|flexibility-bridge|protocol-adapter|hes-simulator|data-api|ui-app' | xargs -r docker rm
```
- Prune dangling images
```
docker image prune -f
```
## Apply
```bash
kubectl apply -f flexibility-hub-simulator.yaml
kubectl apply -f storage-service.yaml
kubectl apply -f flexibility-bridge.yaml
kubectl apply -f protocol-adapter.yaml
kubectl apply -f hes-simulator.yaml
kubectl apply -f data-api.yaml
kubectl apply -f ui-app.yaml
kubectl apply -f orchestrate-hubtosensor-services.yaml

# Cleanup
kubectl delete -f orchestrate-hubtosensor-services.yaml
kubectl delete -f flexibility-hub-simulator.yaml
kubectl delete -f storage-service.yaml
kubectl delete -f flexibility-bridge.yaml
kubectl delete -f protocol-adapter.yaml
kubectl delete -f hes-simulator.yaml
kubectl delete -f data-api.yaml
kubectl delete -f ui-app.yaml
```
## Verify deployment
```
PS C:\Git\microservices\hubToSensor\kubernetes> kubectl apply -f orchestrate-hubtosensor-services.yaml
deployment.apps/flexibility-hub-simulator created
service/flexibility-hub-simulator created
deployment.apps/storage-service created
service/storage-service created
deployment.apps/flexibility-bridge created
deployment.apps/hes-simulator created
service/hes-simulator created
PS C:\Git\microservices\hubToSensor\kubernetes> kubectl get pods
NAME                                   READY   STATUS    RESTARTS   AGE
storage-service-8589d7f68-7q6bd   1/1     Running   0          28s
flexibility-bridge-f468cfc55-j9d6z       1/1     Running   0          27s
hes-simulator-5b7686d8c-qqmt5       1/1     Running   0          27s
flexibility-hub-simulator-7cb78c878-88khr      1/1     Running   0          28s
PS C:\Git\microservices\hubToSensor\kubernetes> kubectl get services
NAME                   TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
storage-service   ClusterIP   10.104.94.116   <none>        9082/TCP         36s
kubernetes             ClusterIP   10.96.0.1       <none>        443/TCP          35d
hes-simulator       ClusterIP   10.98.42.15     <none>        9084/TCP         35s
flexibility-hub-simulator      NodePort    10.107.144.94   <none>        9081:30081/TCP   36s
```

- [Check status and perform other Kubernetes operations](https://github.com/sbhrwl/microservices/blob/main/motivation/generatemessage/kubernetes/README.md#deploy-docker-images-on-kubernetes)
## Access services
* List of exposed URLs for your current services, assuming typical **NodePort** or **port-forwarding** access mappings for local development:
  * `http://localhost:30081/` → `flexibility-hub-simulator`
  * ClusterIP service → `storage-service`
  * rabbitmq listener → `flexibility-bridge`
  * ClusterIP service → `hes-simulator`
## Cleanup
```
kubectl delete -f orchestrate-hubtosensor-services.yaml
```
