# Application onboarding
- [Automatic envoy sidecar injection](#automatic-envoy-sidecar-injection)
- [Verify release](#verify-release)
- [Access services](#access-services)
- [Sidecar logs](#sidecar-logs)
- [Application offboarding](#application-offboarding)
## Automatic envoy sidecar injection
* Label the namespace where your services are deployed.
```
kubectl label namespace <YOUR_APP_NAMESPACE> istio-injection=enabled
kubectl label namespace staging istio-injection=enabled
```
* Redeploy Services: Upgrade your existing service Helm charts to trigger the injection.
```
helm upgrade <YOUR_RELEASE_NAME> <YOUR_CHART_PATH> -n <YOUR_APP_NAMESPACE>
helm upgrade ocs-staging . -f values-staging.yaml -n staging
```
* If upgrade doesnot helps, 
  * restart all deployments at once
```
kubectl rollout restart deployment -n staging --all
```

  * If `-all` does not work, restart each deployment
```
kubectl rollout restart deployment data-api -n staging
kubectl rollout restart deployment ui-app -n staging
kubectl rollout restart deployment flexibility-hub-simulator-deployment -n staging
kubectl rollout restart deployment flexibility-bridge-deployment -n staging
kubectl rollout restart deployment protocol-adapter-deployment -n staging
kubectl rollout restart deployment storage-service-deployment -n staging
kubectl rollout restart deployment hes-simulator-deployment -n staging
```
## Verify release
* Wait for the new pods to be ready. 
  * They should now show `2/2 containers` (your app + Envoy sidecar).
```
helm list -n staging
kubectl get pod -n staging
kubectl get svc -n staging
kubectl get all -n staging
```
* Console logs
```
C:\Git\microservices\hubToSensor\hpa\orchestrate-hubtosensor-services>kubectl get pod -n staging
NAME                                                   READY   STATUS    RESTARTS   AGE
data-api-64dcc47d86-7tfdv                              2/2     Running   0          13m
flexibility-bridge-deployment-bcc955b4-9qvnd           2/2     Running   0          97s
flexibility-hub-simulator-deployment-79555f4cd-q764z   2/2     Running   0          104s
hes-simulator-deployment-57df6dd479-7rt4r              2/2     Running   0          78s
protocol-adapter-deployment-8598c5ff75-2s8f7           2/2     Running   0          91s
storage-service-deployment-d95c4d875-z8lth             2/2     Running   0          85s
ui-app-6f86fd7bdc-plwsp                                2/2     Running   0          111s

C:\Git\microservices\hubToSensor\hpa\orchestrate-hubtosensor-services>kubectl get svc -n staging
NAME                                TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
data-api-service                    NodePort    10.107.22.78    <none>        8085:30885/TCP   30m
flexibility-hub-simulator-service   NodePort    10.110.227.18   <none>        8081:30881/TCP   30m
storage-service-service             ClusterIP   10.110.178.99   <none>        9090/TCP         30m
ui-app-service                      NodePort    10.104.19.178   <none>        8080:30880/TCP   30m
```
## Access services
* List of exposed URLs for your current services, assuming typical **NodePort** or **port-forwarding** access mappings for local development:
  * `flexibility-hub-simulator` → `http://localhost:30881/api/messages`
  * `data-api-service` → `http://localhost:30885/api/v1/requests/<requestID>/tracker`
  * `ui-app` → `http://localhost:30880/`
<img src="images/envoy-sidecars.jpg">

## Sidecar logs
```
2025-10-23T12:10:53.799751Z	info	Version 1.27.3-df538c2279a633544e6c0961a03ab772729933be-Clean
2025-10-23T12:10:53.800205Z	info	Proxy role	ips=[10.1.2.132] type=sidecar id=data-api-64dcc47d86-7tfdv.staging domain=staging.svc.cluster.local
2025-10-23T12:10:53.800309Z	info	Apply proxy config from env {}
2025-10-23T12:10:53.803374Z	info	cpu limit detected as 2, setting concurrency
2025-10-23T12:10:53.803724Z	info	Effective config: binaryPath: /usr/local/bin/envoy
concurrency: 2
configPath: ./etc/istio/proxy
controlPlaneAuthPolicy: MUTUAL_TLS
discoveryAddress: istiod.istio-system.svc:15012
drainDuration: 45s
proxyAdminPort: 15000
serviceCluster: istio-proxy
statNameLength: 189
statusPort: 15020
terminationDrainDuration: 5s
2025-10-23T12:10:53.803777Z	info	JWT policy is third-party-jwt
2025-10-23T12:10:53.803783Z	info	using credential fetcher of JWT type in cluster.local trust domain
2025-10-23T12:10:53.821436Z	info	Opening status port 15020
2025-10-23T12:10:53.821484Z	info	Starting default Istio SDS Server
2025-10-23T12:10:53.822860Z	info	CA Endpoint istiod.istio-system.svc:15012, provider Citadel
2025-10-23T12:10:53.822918Z	info	Using CA istiod.istio-system.svc:15012 cert with certs: var/run/secrets/istio/root-cert.pem
2025-10-23T12:10:53.826671Z	info	xdsproxy	Initializing with upstream address "istiod.istio-system.svc:15012" and cluster "Kubernetes"
2025-10-23T12:10:53.828477Z	info	sds	Starting SDS grpc server
2025-10-23T12:10:53.828603Z	info	sds	Starting SDS server for workload certificates, will listen on "var/run/secrets/workload-spiffe-uds/socket"
2025-10-23T12:10:53.832561Z	info	Pilot SAN: [istiod.istio-system.svc]
2025-10-23T12:10:53.840487Z	info	Starting proxy agent
2025-10-23T12:10:53.840693Z	info	Envoy command: [-c etc/istio/proxy/envoy-rev.json --drain-time-s 45 --drain-strategy immediate --local-address-ip-version v4 --file-flush-interval-msec 1000 --disable-hot-restart --allow-unknown-static-fields -l warning --component-log-level misc:error --skip-deprecated-logs --concurrency 2]
2025-10-23T12:10:53.984432Z	warning	envoy main external/envoy/source/server/server.cc:905	Usage of the deprecated runtime key overload.global_downstream_max_connections, consider switching to `envoy.resource_monitors.global_downstream_max_connections` instead.This runtime key will be removed in future.	thread=15
2025-10-23T12:10:53.985785Z	warning	envoy main external/envoy/source/server/server.cc:1001	There is no configured limit to the number of allowed active downstream connections. Configure a limit in `envoy.resource_monitors.global_downstream_max_connections` resource monitor.	thread=15
2025-10-23T12:10:54.000505Z	info	xdsproxy	connected to delta upstream XDS server: istiod.istio-system.svc:15012	id=1
2025-10-23T12:10:54.058935Z	info	ads	ADS: new connection for node:1
2025-10-23T12:10:54.061011Z	info	ads	ADS: new connection for node:2
2025-10-23T12:10:54.161315Z	info	cache	generated new workload certificate	resourceName=default latency=332.15651ms ttl=23h59m59.838695246s
2025-10-23T12:10:54.162186Z	info	cache	Root cert has changed, start rotating root cert
2025-10-23T12:10:54.162284Z	info	cache	returned workload certificate from cache	ttl=23h59m59.837727753s
2025-10-23T12:10:54.162295Z	info	cache	returned workload trust anchor from cache	ttl=23h59m59.837708665s
2025-10-23T12:10:54.162766Z	info	cache	returned workload trust anchor from cache	ttl=23h59m59.837241774s
2025-10-23T12:10:54.163391Z	info	cache	returned workload trust anchor from cache	ttl=23h59m59.836616183s
2025-10-23T12:10:54.827730Z	info	Readiness succeeded in 1.037725142s
2025-10-23T12:10:54.828134Z	info	Envoy proxy is ready
```
## Application offboarding
- To **completely and cleanly undo** the sidecar injection and prepare your environment for future deployments **without** the sidecar, the best practice is to **disable injection first**, and then proceed with either `helm uninstall` or `kubectl rollout restart`.
  - **Disable Injection First**
    ```bash
    kubectl label namespace staging istio-injection-
    ```
    - **Why:** This is the most crucial step for the *future*. 
      - It ensures that if you decide to immediately redeploy the application (or if the Helm uninstall fails and you need to restart/redeploy manually), any new pods created will **not** get the sidecar.
  - **Uninstall the Helm Release**
    ```bash
    helm uninstall ocs-staging -n staging
    ```
    - **Why:** This action removes the application pods, which in turn removes the running sidecar containers. 
      - Since you disabled injection in step 1, your namespace is now clean and ready for a non-sidecar deployment.