# [Helm charts](https://github.com/sbhrwl/system_design/blob/main/docs/deployment/containerisation/Kubernetes/deploymentstrategies/README.md)
- [Containers](containers/README.md)
- [Kubernetes](kubernetes/README.md)
- [Setup](setup/README.md)
- [Create Helm chart structure](#create-helm-chart-structure)
- [Clean up the default templates](#clean-up-the-default-templates)
- [Horizontal Pod Autoscalar](#horizontal-pod-autoscalar)
- [Convert deployment YAMLs into a Helm template](#convert-deployment-yamls-into-a-helm-template)
- [Chart structure](#chart-structure)
- [Cleanup existing Kubernetes deployment](#cleanup-existing-kubernetes-deployment)
- [Install Helm release](#install-helm-release)
- [Verify deployment](#verify-deployment)
- [Access services](#access-services)
- [Verify HPA](#verify-hpa)
- [HPA simulations](#hpa-simulations)
  - [Check if metrics server is running](#check-if-metrics-server-is-running)
  - [Force a CPU load to test autoscaling](#force-a-cpu-load-to-test-autoscaling)
  - [Watch pod scaling in real time](#watch-pod-scaling-in-real-time)
- [Uninstall Helm release](#uninstall-helm-release)
- [Deployment across environments](deploymentacrossenv/README.md)
## Create Helm chart structure
- Generate the basic `Helm chart directory`. 
- Run this in your terminal:
  ```
  helm create orchestrate-sensor-services
  ```
- This creates a directory called [**orchestrate-sensor-services**](orchestrate-sensor-services) with default templates and values.
<img src="images/directorystructure.jpg">

## Clean up the default templates
- Helm’s `create` command generates a bunch of example templates we don’t need. Let’s simplify.
- Go to the `templates` folder:
  ```
  cd orchestrate-sensor-services/templates
  ```
- Delete all the default templates *except* `_helpers.tpl`.
  - *(Use **`del`** if you’re in Command Prompt on Windows instead of Git Bash or PowerShell)*
  ```bash
  del deployment.yaml service.yaml hpa.yaml ingress.yaml serviceaccount.yaml tests\test-connection.yaml
  del tests\test-connection.yaml & rmdir tests & del NOTES.txt

  rm deployment.yaml service.yaml hpa.yaml ingress.yaml serviceaccount.yaml tests/test-connection.yaml
  ```
- You should only have this file left:
  ```
  _helpers.tpl
  ```
## Horizontal Pod Autoscalar 
- The Horizontal Pod Autoscaler (HPA) is a Kubernetes resource that **automatically scales** the number of pods in a deployment, replica set, or stateful set based on observed metrics like `CPU utilization`, `memory usage`, or `custom metrics`.
## Convert deployment YAMLs into a Helm template
- [ui-deployment](orchestrate-sensor-services/templates/ui-deployment.yaml)
- [ui-service](orchestrate-sensor-services/templates/ui-service.yaml)
- [ui-service-hpa.yaml](orchestrate-sensor-services/templates/ui-service-hpa.yaml)
- [sensor-deployment](orchestrate-sensor-services/templates/sensor-deployment.yaml)
- [sensor-service](orchestrate-sensor-services/templates/sensor-service.yaml)
- [sensor-service-hpa.yaml](orchestrate-sensor-services/templates/sensor-service-hpa.yaml)
- [registration-deployment](orchestrate-sensor-services/templates/registration-deployment.yaml)
- [registration-service](orchestrate-sensor-services/templates/registration-service.yaml)
- [registration-service-hpa.yaml](orchestrate-sensor-services/templates/registration-service-hpa.yaml)
- [notification-deployment](orchestrate-sensor-services/templates/notification-deployment.yaml)
- [notification-service](orchestrate-sensor-services/templates/notification-service.yaml)
- [notification-configmap](orchestrate-sensor-services/templates/notification-configmap.yaml)
- [notification-service-hpa.yaml](orchestrate-sensor-services/templates/notification-service-hpa.yaml)
- [`values.yaml`](orchestrate-sensor-services/values.yaml)
### Chart structure
```pgsql
orchestrate-sensor-services/
├── templates/
│   ├── ui-deployment.yaml
│   ├── ui-service.yaml
│   ├── ui-service-hpa.yaml
│   ├── registration-deployment.yaml
│   ├── registration-service.yaml
│   ├── registration-service-hpa.yaml
│   ├── sensor-deployment.yaml
│   ├── sensor-service.yaml
│   ├── sensor-service-hpa.yaml
│   ├── notification-deployment.yaml
│   ├── notification-service.yaml
│   ├── notification-service-hpa.yaml
│   └── _helpers.tpl
├── Chart.yaml
├── values.yaml
├── .helmignore
```
## Cleanup existing Kubernetes deployment 
```
kubectl delete -f orchestrate-sensor-services.yaml
```
## Install Helm release
- Go to Helm chart folder [**orchestrate-sensor-services**](orchestrate-sensor-services)
```powershell
helm install ocs-release . 
```
- **`ocs-release`** is the name you're assigning to this Helm release (you can change it if you like).
- `.` means Helm will *install using the chart in the current directory*.
## Verify deployment
```
PS C:\Git\microservices\sensorregistration\helmcharts\orchestrate-sensor-services> helm install ocs-release .
NAME: ocs-release
LAST DEPLOYED: Fri May 30 10:57:25 2025
NAMESPACE: default
STATUS: deployed
REVISION: 1
TEST SUITE: None
PS C:\Git\microservices\sensorregistration\helmcharts\orchestrate-sensor-services> helm list
NAME                                    NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                   APP VERSION
ocs-release     default         1               2025-05-30 10:57:25.8162546 +0300 EEST  deployed        sensor-app-chart-0.1.0  1.0
PS C:\Git\microservices\sensorregistration\helmcharts\orchestrate-sensor-services> helm list -A
NAME                                    NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                   APP VERSION
ocs-release     default         1               2025-05-30 10:57:25.8162546 +0300 EEST  deployed        sensor-app-chart-0.1.0  1.0
PS C:\Git\microservices\sensorregistration\helmcharts\orchestrate-sensor-services> kubectl get pods
NAME                                    READY   STATUS    RESTARTS   AGE
notification-service-7f5845c77c-cd2qr   1/1     Running   0          2m10s
registration-service-7c4555d588-65v4h   1/1     Running   0          2m10s
sensor-service-59b4d96b5-v9rjj          1/1     Running   0          2m10s
ui-service-55f94d6747-rfjnn             1/1     Running   0          2m10s
PS C:\Git\microservices\sensorregistration\helmcharts\orchestrate-sensor-services> kubectl get svc
NAME                   TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
kubernetes             ClusterIP   10.96.0.1       <none>        443/TCP          25d
notification-service   ClusterIP   10.105.187.69   <none>        9084/TCP         2m17s
registration-service   ClusterIP   10.109.85.233   <none>        9083/TCP         2m17s
sensor-service         NodePort    10.98.38.139    <none>        9082:30082/TCP   2m17s
ui-service             NodePort    10.98.31.231    <none>        9081:30081/TCP   2m17s
```
- [Check status and perform other Kubernetes operations](https://github.com/sbhrwl/microservices/blob/main/motivation/generatemessage/kubernetes/README.md#deploy-docker-images-on-kubernetes)
## Access services
* List of exposed URLs for your current services, assuming typical **NodePort** or **port-forwarding** access mappings for local development:
  * `http://localhost:30081/` → `ui-service`
  * `http://localhost:30082/api/register/sensor` → `sensor-service`
  * ClusterIP service → `registration-service`
  * ClusterIP service → `notification-service`
## Verify HPA
- **`kubectl get hpa`**
```
PS C:\Git\microservices\sensorregistration\hpa\orchestrate-sensor-services-with-hpa> kubectl get hpa
NAME               REFERENCE                 TARGETS              MINPODS   MAXPODS   REPLICAS   AGE
notification-hpa   Deployment/notification   cpu: <unknown>/80%   1         5         0          59s
registration-hpa   Deployment/registration   cpu: <unknown>/80%   1         5         0          59s
sensor-hpa         Deployment/sensor         cpu: <unknown>/80%   1         5         0          59s
ui-hpa             Deployment/ui             cpu: <unknown>/80%   1         5         0          59s
```
- Describe a specific HPA: **`kubectl describe hpa registration-hpa`**
```
PS C:\Git\microservices\sensorregistration\hpa\orchestrate-sensor-services-with-hpa> kubectl get hpa
NAME               REFERENCE                 TARGETS              MINPODS   MAXPODS   REPLICAS   AGE
notification-hpa   Deployment/notification   cpu: <unknown>/80%   1         5         0          59s
registration-hpa   Deployment/registration   cpu: <unknown>/80%   1         5         0          59s
sensor-hpa         Deployment/sensor         cpu: <unknown>/80%   1         5         0          59s
ui-hpa             Deployment/ui             cpu: <unknown>/80%   1         5         0          59s
PS C:\Git\microservices\sensorregistration\hpa\orchestrate-sensor-services-with-hpa> kubectl describe hpa registration-hpa
Name:                                                  registration-hpa
Namespace:                                             default
Labels:                                                app=registration
                                                       app.kubernetes.io/managed-by=Helm
Annotations:                                           meta.helm.sh/release-name: ocs-hpa-release
                                                       meta.helm.sh/release-namespace: default
CreationTimestamp:                                     Fri, 30 May 2025 11:01:11 +0300
Reference:                                             Deployment/registration
Metrics:                                               ( current / target )
  resource cpu on pods  (as a percentage of request):  <unknown> / 80%
Min replicas:                                          1
Max replicas:                                          5
Deployment pods:                                       0 current / 0 desired
Conditions:
  Type         Status  Reason          Message
  ----         ------  ------          -------
  AbleToScale  False   FailedGetScale  the HPA controller was unable to get the target's current scale: deployments/scale.apps "registration" not found
Events:
  Type     Reason          Age   From                       Message
  ----     ------          ----  ----                       -------
  Warning  FailedGetScale  11s   horizontal-pod-autoscaler  deployments/scale.apps "registration" not found
```
## HPA simulations
### Check if metrics server is running
- HPAs require the Kubernetes Metrics Server.
- Ensure it's running:
```sh
kubectl get deployment metrics-server -n kube-system
```
- If not installed, [follow these instructions to install it](https://github.com/kubernetes-sigs/metrics-server#installation).
### Force a CPU load to test autoscaling
- You can create a load generator pod to increase CPU usage and trigger scaling:
```sh
kubectl run -i --tty load-generator --rm \
  --image=busybox /bin/sh

# Inside the shell, run:
while true; do :; done
```
### Watch pod scaling in real time
- This lets you observe autoscaling behavior:
```sh
watch kubectl get hpa
```
- Or for the pods directly:
```sh
watch kubectl get pods -l app=registration
```
- replace `registration` with other service names as needed
## Uninstall Helm release
- Delete all Kubernetes resources (Deployments, Services, etc.) that were created by the Helm release named `ocs-release`
```
helm uninstall ocs-release
``` 