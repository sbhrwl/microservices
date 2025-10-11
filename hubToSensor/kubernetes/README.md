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
- Make sure you are logged in via docker desktop to Docker registry and then push images to docker registry
```
docker push sbhrwldocker/flexibility-hub-simulator:latest
docker push sbhrwldocker/storage-service:latest
docker push sbhrwldocker/flexibility-bridge:latest
docker push sbhrwldocker/protocol-adapter:latest
docker push sbhrwldocker/hes-simulator:latest
docker push sbhrwldocker/data-api:latest
docker push sbhrwldocker/ui-app:latest
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
C:\Git\microservices\hubToSensor\kubernetes>kubectl apply -f orchestrate-hubtosensor-services.yaml
deployment.apps/storage-service-deployment created
service/storage-service-service created
deployment.apps/data-api-deployment created
service/data-api-service created
deployment.apps/ui-app-deployment created
service/ui-app-service created
deployment.apps/hes-simulator-deployment created
deployment.apps/flexibility-hub-simulator-deployment created
service/flexibility-hub-simulator-service created
deployment.apps/flexibility-bridge-deployment created
deployment.apps/protocol-adapter-deployment created
C:\Git\microservices\hubToSensor\kubernetes>kubectl get pods
NAME                                                    READY   STATUS    RESTARTS   AGE
data-api-deployment-79777d6b4-xg2vg                     1/1     Running   0          22s
flexibility-bridge-deployment-97b6d9955-zcgnj           1/1     Running   0          22s
flexibility-hub-simulator-deployment-5856885dc6-mp6bz   1/1     Running   0          22s
hes-simulator-deployment-977b4865c-lrs8t                1/1     Running   0          22s
protocol-adapter-deployment-5567c667f8-8r6hb            1/1     Running   0          22s
storage-service-deployment-6c896f58bc-pkjbg             1/1     Running   0          22s
ui-app-deployment-5467d4fbf8-pvf82                      1/1     Running   0          22s
C:\Git\microservices\hubToSensor\kubernetes>kubectl get services
NAME                                TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
data-api-service                    NodePort    10.108.126.10    <none>        8085:30411/TCP   16s
flexibility-hub-simulator-service   ClusterIP   10.104.94.2      <none>        8081/TCP         16s
kubernetes                          ClusterIP   10.96.0.1        <none>        443/TCP          159d
storage-service-service             ClusterIP   10.107.61.60     <none>        9090/TCP         16s
ui-app-service                      NodePort    10.106.194.160   <none>        8080:32711/TCP   16s
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
