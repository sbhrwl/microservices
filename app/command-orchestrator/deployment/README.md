# Deployment
- [Overview](#overview)
- [Self-hosted dapr deployment](#self-hosted-dapr-deployment)
- [Runnable jar execution](#runnable-jar-execution)
- [Kubernetes restart example](#kubernetes-restart-example)
## Overview
- The service is designed to run alongside a Dapr sidecar in self-hosted mode
- It is packaged as an executable JAR
- A Kubernetes restart procedure for the deployed service is provided
## Self-hosted dapr deployment
- Set a non-loopback host ip
```
$env:DAPR_HOST_IP=192.168.0.7
```
- Start the dapr sidecar
```
dapr run --resources-path ./components --app-id device-hub --app-port 3501 --dapr-grpc-port 50011 --log-level debug --scheduler-host-address ""
```
## Runnable jar execution
- Set dapr grpc port to match the sidecar
```
$env:DAPR_GRPC_PORT=50011
```
- Run the application jar with provided configs
```
java -D"config.file=src/main/dist/etc/application.conf" -D"logback.configurationFile=src/main/dist/etc/logback.xml" -D"log.appender"=STDOUT -jar target/device-hub-1.0.jar
```
## Kubernetes restart example
- Authenticate to the cluster
```
gcloud container clusters get-credentials dev-smoc-cluster-01 --region europe-west4 --project cpet-d-smoc-c01-srv-aah-01
```
- Restart the deployment
```
kubectl rollout restart deployment device-hub --namespace gfc-01;
```
