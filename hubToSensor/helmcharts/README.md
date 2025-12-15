# [Helm charts](https://github.com/sbhrwl/system_design/blob/main/docs/devops/containerisation/Kubernetes/deploymentstrategies/README.md)
- [Setting up docker images](docs/containers/README.md)
- [Kubernetes](docs/kubernetes/README.md)
- [Chart setup](https://github.com/sbhrwl/microservices/blob/main/sensorregistration/helmcharts/docs/setup/README.md)
- [Create templates YAMLs](#create-helm-templates-yamls)
- [Chart structure](#chart-structure)
- [Install Helm release](#install-helm-release)
- [Verify deployment](#verify-deployment)
- [Access services](#access-services)
- [Verify HPA](#verify-hpa)
- [HPA simulations](https://github.com/sbhrwl/microservices/blob/main/sensorregistration/helmcharts/docs/hpa/README.md)
- [Update Helm release](#update-helm-release)
- [Uninstall Helm release](#uninstall-helm-release)
- [Deployment across environments](https://github.com/sbhrwl/microservices/blob/main/sensorregistration/helmcharts/docs/deploymentacrossenv/README.md)
## Create templates YAMLs
- [**flexibility-hub-simulator-deployment**](orchestrate-hubtosensor-services/templates/flexibility-hub-simulator-deployment.yaml)
- [**flexibility-hub-simulator-service**](orchestrate-hubtosensor-services/templates/flexibility-hub-simulator-service.yaml)
- [**storage-service-deployment**](orchestrate-hubtosensor-services/templates/storage-service-deployment.yaml)
- [**storage-service-service**](orchestrate-hubtosensor-services/templates/storage-service-service.yaml)
- [**flexibility-bridge-deployment**](orchestrate-hubtosensor-services/templates/flexibility-bridge-deployment.yaml)
- [**protocol-adapter-deployment**](orchestrate-hubtosensor-services/templates/protocol-adapter-deployment.yaml)
- [**hes-simulator-deployment**](orchestrate-hubtosensor-services/templates/hes-simulator-deployment.yaml)
- [**data-api-deployment**](orchestrate-hubtosensor-services/templates/data-api-deployment.yaml)
- [**data-api-service**](orchestrate-hubtosensor-services/templates/data-api-service.yaml)
- [**ui-app-deployment**](orchestrate-hubtosensor-services/templates/ui-app-deployment.yaml)
- [**ui-app-service**](orchestrate-hubtosensor-services/templates/ui-app-service.yaml)
- [**`values.yaml`**](orchestrate-hubtosensor-services/values.yaml)
## Chart structure
- [orchestrate-hubtosensor-services](orchestrate-hubtosensor-services)
- Each service has its own Deployment, Service, and Horizontal Pod Autoscaler (HPA) configuration.
  ```
  orchestrate-hubtosensor-services/
  ├── templates/
  │   ├── flexibility-hub-simulator-deployment.yaml
  │   ├── flexibility-hub-simulator-service.yaml
  │   ├── flexibility-hub-simulator-hpa.yaml
  │   ├── storage-service-deployment.yaml
  │   ├── storage-service-service.yaml
  │   ├── flexibility-bridge-deployment.yaml
  │   ├── protocol-adapter-deployment.yaml
  │   ├── hes-simulator-deployment.yaml
  │   ├── data-api-deployment.yaml
  │   ├── data-api-service.yaml
  │   ├── data-api-hpa.yaml
  │   ├── ui-app-deployment.yaml
  │   ├── ui-app-service.yaml
  │   └── _helpers.tpl
  ├── Chart.yaml
  ├── values.yaml
  ├── values-staging.yaml
  ├── values-prod.yaml
  ├── .helmignore
  ```
## Install Helm release
- Cleanup existing Kubernetes deployment 
```
kubectl delete -f orchestrate-hubtosensor-services.yaml
```
- Go to Helm chart folder [**orchestrate-hubtosensor-services**](orchestrate-hubtosensor-services)
```powershell
helm install ocs-h2s-release .
```
- **`ocs-h2s-release`** is the name you're assigning to this Helm release (you can change it if you like).
- `.` means Helm will *install using the chart in the current directory*.
## Verify deployment
```
C:\Git\microservices\hubToSensor\helmcharts\orchestrate-hubtosensor-services>helm install ocs-h2s-release .
Error: INSTALLATION FAILED: template: orchestrate-hubtosensor-services/templates/ui-app-service.yaml:4:11: executing "orchestrate-hubtosensor-services/templates/ui-app-service.yaml" at <include "uiApp.name" .>: error calling include: template: no template "uiApp.name" associated with template "gotpl"

C:\Git\microservices\hubToSensor\helmcharts\orchestrate-hubtosensor-services>helm install ocs-h2s-release .
Error: INSTALLATION FAILED: template: orchestrate-hubtosensor-services/templates/protocol-adapter-deployment.yaml:19:26: executing "orchestrate-hubtosensor-services/templates/protocol-adapter-deployment.yaml" at <.Values.protocolAdapter.image>: nil pointer evaluating interface {}.image

C:\Git\microservices\hubToSensor\helmcharts\orchestrate-hubtosensor-services>helm install ocs-h2s-release .
NAME: ocs-h2s-release
LAST DEPLOYED: Sun Oct 12 11:31:07 2025
NAMESPACE: default
STATUS: deployed
REVISION: 1
TEST SUITE: None

C:\Git\microservices\hubToSensor\helmcharts\orchestrate-hubtosensor-services>helm list
NAME            NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                                   APP VERSION
ocs-h2s-release default         1               2025-10-12 11:31:07.1780478 +0300 EEST  deployed        orchestrate-hubtosensor-services-0.1.0  1.16.0

C:\Git\microservices\hubToSensor\helmcharts\orchestrate-hubtosensor-services>helm list -A
NAME            NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                                   APP VERSION
ocs-h2s-release default         1               2025-10-12 11:31:07.1780478 +0300 EEST  deployed        orchestrate-hubtosensor-services-0.1.0  1.16.0

C:\Git\microservices\hubToSensor\helmcharts\orchestrate-hubtosensor-services>kubectl get pods
NAME                                                    READY   STATUS    RESTARTS     AGE
data-api-65c7b7b9d7-n5nnv                               1/1     Running   0            36s
flexibility-bridge-deployment-54884f5cc4-qrpqg          1/1     Running   1 (9s ago)   36s
flexibility-hub-simulator-deployment-6d4c545887-7rgkt   1/1     Running   0            36s
hes-simulator-deployment-6f8f6b66-5vnhh                 1/1     Running   0            36s
protocol-adapter-deployment-5b499cb96c-bkxnf            1/1     Running   1 (9s ago)   36s
storage-service-deployment-6f99954b-8tdmn               1/1     Running   1 (4s ago)   36s
ui-app-676567d78f-smk9m                                 1/1     Running   0            36s

C:\Git\microservices\hubToSensor\helmcharts\orchestrate-hubtosensor-services>kubectl get svc
NAME                                TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
flexibility-hub-simulator-service   NodePort    10.102.128.78    <none>        8081:30881/TCP   47s
storage-service-service             ClusterIP   10.98.125.63     <none>        9090/TCP         47s
data-api-service                    NodePort    10.109.147.145   <none>        8085:30885/TCP   47s
ui-app-service                      NodePort    10.106.186.119   <none>        8080:30880/TCP   47s
```
- [Check status and perform other Kubernetes operations](https://github.com/sbhrwl/microservices/blob/main/motivation/generatemessage/kubernetes/README.md#deploy-docker-images-on-kubernetes)
## Access services
* List of exposed URLs for your current services, assuming typical **NodePort** or **port-forwarding** access mappings for local development:
  * `flexibility-hub-simulator` → `http://localhost:30881/api/messages`
  * `data-api-service` → `http://localhost:30885/api/v1/requests/<requestID>/tracker`
  * [`ui-app`](https://github.com/sbhrwl/microservices/blob/main/hubToSensor/hub-to-sensor/ui-app/README.md) → `http://localhost:4200/`
* Database verification
```sql
PS C:\Users\sabharwalr> psql -h localhost -U myuser -d mydatabase
Password for user myuser:

psql (17.6, server 16.9 (Debian 16.9-1.pgdg120+1))
WARNING: Console code page (850) differs from Windows code page (1252)
         8-bit characters might not work correctly. See psql reference
         page "Notes for Windows users" for details.
Type "help" for help.

mydatabase=# select * from control_requests;
mydatabase=# select * from request_change_log;
```
## Verify HPA
- **`kubectl get hpa`**
```
C:\Git\microservices\hubToSensor\hpa\orchestrate-hubtosensor-services>kubectl get hpa
NAME                            REFERENCE                                         TARGETS                                     MINPODS   MAXPODS   REPLICAS   AGE
data-api-hpa                    Deployment/data-api-deployment                    cpu: <unknown>/50%, memory: <unknown>/60%   1         2         0          77s
flexibility-hub-simulator-hpa   Deployment/flexibility-hub-simulator-deployment   cpu: <unknown>/50%, memory: <unknown>/60%   1         2         1          77s
```
- Describe a specific HPA: **`kubectl describe hpa registration-hpa`**
```
C:\Git\microservices\hubToSensor\hpa\orchestrate-hubtosensor-services>kubectl describe hpa flexibility-hub-simulator-hpa
Name:                                                     flexibility-hub-simulator-hpa
Namespace:                                                default
Labels:                                                   app.kubernetes.io/managed-by=Helm
Annotations:                                              meta.helm.sh/release-name: ocs-h2shpa-release
                                                          meta.helm.sh/release-namespace: default
CreationTimestamp:                                        Mon, 13 Oct 2025 10:51:35 +0300
Reference:                                                Deployment/flexibility-hub-simulator-deployment
Metrics:                                                  ( current / target )
  resource cpu on pods  (as a percentage of request):     <unknown> / 50%
  resource memory on pods  (as a percentage of request):  <unknown> / 60%
Min replicas:                                             1
Max replicas:                                             2
Deployment pods:                                          1 current / 0 desired
Conditions:
  Type           Status  Reason                   Message
  ----           ------  ------                   -------
  AbleToScale    True    SucceededGetScale        the HPA controller was able to get the target's current scale
  ScalingActive  False   FailedGetResourceMetric  the HPA was unable to compute the replica count: failed to get cpu utilization: unable to get metrics for resource cpu: unable to fetch metrics from resource metrics API: the server could not find the requested resource (get pods.metrics.k8s.io)
Events:
  Type     Reason                        Age               From                       Message
  ----     ------                        ----              ----                       -------
  Warning  FailedGetResourceMetric       4s (x2 over 64s)  horizontal-pod-autoscaler  failed to get cpu utilization: unable to get metrics for resource cpu: unable to fetch metrics from resource metrics API: the server could not find the requested resource (get pods.metrics.k8s.io)
  Warning  FailedGetResourceMetric       4s (x2 over 64s)  horizontal-pod-autoscaler  failed to get memory utilization: unable to get metrics for resource memory: unable to fetch metrics from resource metrics API: the server could not find the requested resource (get pods.metrics.k8s.io)
  Warning  FailedComputeMetricsReplicas  4s (x2 over 64s)  horizontal-pod-autoscaler  invalid metrics (2 invalid out of 2), first error is: failed to get cpu resource metric value: failed to get cpu utilization: unable to get metrics for resource cpu: unable to fetch metrics from resource metrics API: the server could not find the requested resource (get pods.metrics.k8s.io)
```
## Update Helm release
```
helm upgrade --install ocs-h2s-release .
```
## Uninstall Helm release
- Delete all Kubernetes resources (Deployments, Services, etc.) that were created by the Helm release named `ocs-h2s-release`
```
helm uninstall ocs-h2s-release
``` 
## Troubleshooting
**Problem:**
- After deploying via Helm, the `flexibility-hub-simulator-service` (NodePort 30881) was unreachable from the host, even though it worked before manual deployment.
**Approach to diagnose:**
1. **Check pods and services** — confirm all running in the `default` namespace:
   ```bash
   kubectl get pods
   kubectl get svc
   ```
2. **Get ClusterIP of the service** (used for internal pod access):
   ```bash
   kubectl get svc flexibility-hub-simulator-service -o jsonpath='{.spec.clusterIP}'
   ```

   * This gives the internal service IP (`10.x.x.x`).
3. **Get Node IP of the cluster node** (used for NodePort access inside the cluster):
   ```bash
   kubectl get nodes -o wide
   ```

   * This shows the internal node IP (`192.168.65.3` for Docker Desktop).
4. **Check NodePort mapping in service YAML** (ensure `targetPort` matches container port):
   ```bash
   kubectl get svc flexibility-hub-simulator-service -o yaml
   ```

   * Confirms `port: 8081` → `nodePort: 30881`.

5. **Check internal pod connectivity** using ClusterIP:
   ```bash
   kubectl exec -it <pod-name> -- curl http://<cluster-ip>:8081
   ```

   → Responded `404` → service working internally.

6. **Test NodePort inside the cluster** using node IP:
   ```bash
   kubectl exec -it <pod-name> -- curl http://<node-ip>:30881
   ```

   → Responded `404` → NodePort routing fine.

7. **Test NodePort externally from Windows host**:
   ```bash
   curl http://localhost:30881
   ```

   → Responded `404` → NodePort exposed correctly to host.

** Conclusion:**
- Kubernetes and Helm setup were correct; the service was reachable.
 The `404` response simply shows that `/` is not a valid endpoint — network connectivity is working as expected.
