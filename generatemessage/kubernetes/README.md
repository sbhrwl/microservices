# Deploying Docker image to Kubernetes
- [Build and tag Docker image](#build-and-tag-docker-image)
- [Push image to Docker registry](#push-image-to-docker-registry)
- [Deploy Docker images on Kubernetes](#deploy-docker-images-on-kubernetes)
- [Logs](#logs)
- [Test](#test)
- [Delete deployment](#delete-deployment)
- [Updating deployment](#updating-deployment)
## Build and tag Docker image
- [Docker commands](https://github.com/sbhrwl/system_design/blob/main/docs/deployment/containerisation/Docker/commands/README.md)
  ```bash
  docker build -t image_name:tag .
  
  docker build -t microservice-get-message:v1.0.0 .
  docker build -t microservice-get-message:latest .
  
  # For pushing image to Docker registry, build the image with the full tag directly
  docker build -t sbhrwldocker/microservice-get-message:v1.0.0 .
  ```

## Push image to Docker registry
- [Docker repository](https://hub.docker.com/repositories/sbhrwldocker)
  ```
  docker push your-username-on-docker/microservice-get-message:v1.0.0
  
  docker push sbhrwldocker/microservice-get-message:v1.0.0
  ```
- [Other options for publishing images](options/README.md)
- Logs
  ```
  PS C:\Git\spring\SpringBoot\GreetingsMessage> docker push sbhrwldocker/microservice-get-message:v1.0.0
  The push refers to repository [docker.io/sbhrwldocker/microservice-get-message]
  bcd6defb6807: Pushed
  6be690267e47: Mounted from library/openjdk
  13a34b6fff78: Mounted from library/openjdk
  9c1b6dd6c1e6: Mounted from library/openjdk
  v1.0.0: digest: sha256:fe592eff3d7759ee65554658b2f254a6daebf18ecd9ad72caee27c2e2dc05cba size: 1165
  ```
## Deploy Docker images on Kubernetes
- [Kubernetes commands](https://github.com/sbhrwl/system_design/blob/main/docs/deployment/containerisation/Kubernetes/k8scommands/README.md)
- Write a [`deployment-microservice-get-message.yaml`](deployment-microservice-get-message.yaml)
  - [`deployment.yaml`](deployment.yaml)
  - [`service.yaml`](service.yaml)
- Deploy to Kubernetes
  ```bash
  kubectl apply -f deployment-microservice-get-message.yaml
  
  kubectl apply -f deployment.yaml
  kubectl apply -f service.yaml
  ```
- Check status
  ```bash
  kubectl get pods
  kubectl get services
  ```
## Logs
```
PS C:\Git\kubernetes> kubectl apply -f deployment-microservice-get-message.yaml
deployment.apps/my-app created
service/my-app-service created
PS C:\Git\kubernetes> kubectl get pods
NAME                     READY   STATUS    RESTARTS   AGE
my-app-6496c86b4-kncd5   1/1     Running   0          6s
my-app-6496c86b4-nqk6r   1/1     Running   0          6s
PS C:\Git\kubernetes> kubectl get services
NAME             TYPE        CLUSTER-IP     EXTERNAL-IP   PORT(S)          AGE
kubernetes       ClusterIP   10.96.0.1      <none>        443/TCP          3h6m
my-app-service   NodePort    10.96.75.133   <none>        9999:30080/TCP   9s
PS C:\Git\kubernetes> docker ps
CONTAINER ID   IMAGE          COMMAND                CREATED              STATUS              PORTS     NAMES
d69cc750d567   a125ff44039c   "java -jar /app.jar"   About a minute ago   Up About a minute             k8s_my-app_my-app-6496c86b4-kncd5_default_f99d9ea3-ee8e-4ef8-90f9-bc775c1d0f80_0
cfca32df0dbe   a125ff44039c   "java -jar /app.jar"   About a minute ago   Up About a minute             k8s_my-app_my-app-6496c86b4-nqk6r_default_136d83e1-7a5c-4bfc-95f5-a0df988c9c48_0
PS C:\Git\kubernetes>
```
## Test
- When using `NodePort` service:
  - `http://localhost:30080/message/generate`
- When using `port forwarding`:
  - `kubectl port-forward` for local access
- When using `LoadBalancer` service:
  - Use `kubectl get svc` to get the external IP
    - On Docker Desktop, it may expose it on localhost with a high port

## Delete deployment
- Delete whole deployment at once
  ```
  kubectl delete -f deployment-microservice-get-message.yaml
  ```
  - This deletes `Deployment` (which controls the pods), `Service` (NodePort or LoadBalancer) and `pods` created via the deployment
- Delete each resource individually
  ```
  kubectl delete deployment microservice-get-message
  kubectl delete service microservice-get-message-service
  ```
- Check what’s running
  ```
  kubectl get all
  ```
## Updating deployment
- After changing your `deployment.yaml` files
  - `kubectl apply -f your-file.yaml`
  - `kubectl rollout restart deployment command-orchestration-deployment`
  - `kubectl rollout restart deployment task-orchestration-deployment`