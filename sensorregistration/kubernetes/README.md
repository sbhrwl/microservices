# Move to Kubernetes
- [Kubernetes commands](https://github.com/sbhrwl/system_design/blob/main/docs/deployment/containerisation/Kubernetes/k8scommands/README.md)
- [Push images to Docker registry](#push-images-to-docker-registry)
- [Deployment files](#deployment-files)
- [Cleanup](#cleanup)
- [Apply](#apply)
- [Verify deployment](#verify-deployment)
- [Access services](#access-services)
## Push images to Docker registry
- [Docker repository](https://hub.docker.com/repositories/sbhrwldocker)
```
docker push sbhrwldocker/ui-service:latest
docker push sbhrwldocker/sensor-service:latest
docker push sbhrwldocker/registration-service:latest
docker push sbhrwldocker/notification-service:latest
```
## Deployment files 
- We need to configue them such that MongoDB, Kafka and Keycloak are accessible from kubernetes
  - Set the right **`hostname/IP`** in the Deployment env vars so that Kubernetes pod can reach MongoDB, Kafka and Keycloak
  - Check IP address of your laptopn where these are running as docker containers: `ipconfig` - **192.168.0.102**
  - **mongo-test**: `kubectl run mongo-test --rm -it --image=busybox --restart=Never -- sh`
    - `nc -zv 192.168.0.102 27017`
  - **kafka-test**: `kubectl run kafka-test --rm -it --image=busybox --restart=Never -- sh`
    - `nc -zv 192.168.0.102 29092`
  - **keycloak-test**: `kubectl run keycloak-test --rm -it --image=busybox --restart=Never -- sh`
    - `nc -zv 192.168.0.102 8080`
- [`ui-service.yaml`](ui-service.yaml)
- [`sensor-service.yaml`](sensor-service.yaml)
- [`registration-service.yaml`](registration-service.yaml)
- [`notification-service.yaml`](notification-service.yaml)
- Combined: [`orchestrate-sensor-services.yaml`](orchestrate-sensor-services.yaml)

## Cleanup
- Stop running containers
```
docker ps --format "{{.Names}}" | grep -E 'sensor-service|ui-service|registration-service|notification-service' | xargs -r docker stop
```
- Remove stopped containers
```
docker ps -a --format "{{.Names}}" | grep -E 'sensor-service|ui-service|registration-service|notification-service' | xargs -r docker rm
```
- Prune dangling images
```
docker image prune -f
```
## Apply
```bash
kubectl apply -f ui-service.yaml
kubectl apply -f sensor-service.yaml
kubectl apply -f registration-service.yaml
kubectl apply -f notification-service.yaml

kubectl apply -f orchestrate-sensor-services.yaml

# Cleanup
kubectl delete -f orchestrate-sensor-services.yaml
```
## Verify
```
PS C:\Git\microservices\sensorregistration\kubernetes> kubectl apply -f orchestrate-sensor-services.yaml
configmap/notification-service-config created
deployment.apps/notification-service created
service/notification-service created
deployment.apps/registration-service created
service/registration-service created
deployment.apps/sensor-service created
service/sensor-service created
deployment.apps/ui-service created
service/ui-service created
PS C:\Git\microservices\sensorregistration\kubernetes> kubectl get pods
NAME                                    READY   STATUS    RESTARTS   AGE
notification-service-7f5845c77c-dchcf   1/1     Running   0          17s
registration-service-86d67ff64-tf8mh    1/1     Running   0          17s
sensor-service-74567d6548-c2fgp         1/1     Running   0          17s
ui-service-dfbb8d9df-jclm2              1/1     Running   0          17s
PS C:\Git\microservices\sensorregistration\kubernetes> kubectl get services
NAME                   TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
kubernetes             ClusterIP   10.96.0.1        <none>        443/TCP          23d
notification-service   ClusterIP   10.104.141.255   <none>        9084/TCP         21s
registration-service   ClusterIP   10.97.150.246    <none>        9083/TCP         21s
sensor-service         NodePort    10.104.180.39    <none>        9082:30082/TCP   21s
ui-service             NodePort    10.110.234.13    <none>        9081:30081/TCP   21s
PS C:\Git\microservices\sensorregistration\kubernetes>
```

- [Check status and perform other Kubernetes operations](https://github.com/sbhrwl/microservices/blob/main/motivation/generatemessage/kubernetes/README.md#deploy-docker-images-on-kubernetes)
## Access services
* List of exposed URLs for your current services, assuming typical **NodePort** or **port-forwarding** access mappings for local development:
  * `http://localhost:30081/...` → `ui-service`
  * `http://localhost:30082/api/register/sensor` → `sensor-service`
  * ClusterIP service → `registration-service`
  * ClusterIP service → `notification-service`
- [`Failures with Kafka`](failuresWithKafka/README.md)
  ```
  # Important: host.docker.internal does NOT work from inside Kubernetes pods.
  # Replace with your host machine IP (e.g., 192.168.1.100) if Kafka is running on Docker Desktop.
  # This allows the pod to reach the Kafka broker running on your host.
  ```
