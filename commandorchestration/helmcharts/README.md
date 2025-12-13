# [Helm charts](https://github.com/sbhrwl/system_design/blob/main/docs/deployment/containerisation/Kubernetes/deploymentstrategies/README.md)
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
## Create Helm chart structure
- Generate the basic `Helm chart directory`. 
- Run this in your terminal:
  ```
  helm create orchestrate-sensor-services
  ```
- This creates a directory called [**orchestrate-command-services**](orchestrate-command-services) with default templates and values.
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
- [**task-orchestrator-deployment**](orchestrate-command-services/templates/task-orchestrator-deployment.yaml)
- [**task-orchestrator-service**](orchestrate-command-services/templates/task-orchestrator-service.yaml)
- [**command-orchestrator-deployment**](orchestrate-command-services/templates/command-orchestrator-deployment.yaml)
- [**command-orchestrator-service**](orchestrate-command-services/templates/command-orchestrator-service.yaml)
- [**protocol-gateway-deployment**](orchestrate-command-services/templates/protocol-gateway-deployment.yaml)
- [**sensor-simulator-deployment**](orchestrate-command-services/templates/sensor-simulator-deployment.yaml)
- [**sensor-simulator-service**](orchestrate-command-services/templates/sensor-simulator-service.yaml)
- [**`values.yaml`**](orchestrate-command-services/values.yaml)
### Chart structure
```pgsql
orchestrate-command-services/
├── templates/
│   ├── task-orchestrator-deployment.yaml
│   ├── task-orchestrator-service.yaml
│   ├── command-orchestrator-deployment.yaml
│   ├── command-orchestrator-service.yaml
│   ├── protocol-gateway-deployment.yaml
│   ├── sensor-simulator-deployment.yaml
│   ├── sensor-simulator-service.yaml
│   └── _helpers.tpl
├── Chart.yaml
├── values.yaml
├── .helmignore
```
## Cleanup existing Kubernetes deployment 
```
kubectl delete -f orchestrate-command-services.yaml
```
## Install Helm release
- Go to Helm chart folder [**orchestrate-command-services**](orchestrate-command-services)
```powershell
helm install ocs-release .
```
- **`ocs-release`** is the name you're assigning to this Helm release (you can change it if you like).
- `.` means Helm will *install using the chart in the current directory*.
## Verify deployment
```
PS C:\Git\microservices\commandorchestration\helmcharts\orchestrate-command-services> helm install ocs-release .
NAME: ocs-release
LAST DEPLOYED: Mon Jun  9 15:52:31 2025
NAMESPACE: default
STATUS: deployed
REVISION: 1
TEST SUITE: None
PS C:\Git\microservices\commandorchestration\helmcharts\orchestrate-command-services> helm list
NAME            NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                   APP VERSION
ocs-release     default         1               2025-06-09 15:52:31.1892375 +0300 EEST  deployed        microservices-0.1.0     1.0
PS C:\Git\microservices\commandorchestration\helmcharts\orchestrate-command-services> helm list -A
NAME            NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                   APP VERSION
ocs-release     default         1               2025-06-09 15:52:31.1892375 +0300 EEST  deployed        microservices-0.1.0     1.0
PS C:\Git\microservices\commandorchestration\helmcharts\orchestrate-command-services> kubectl get pods
NAME                                                              READY   STATUS    RESTARTS   AGE
ocs-release-microservices-command-orchestrator-789c6d7877-sxt82   1/1     Running   0          15s
ocs-release-microservices-protocol-gateway-7f86cfb765-hzdd4       1/1     Running   0          15s
ocs-release-microservices-sensor-simulator-5b7686d8c-xrkqd        1/1     Running   0          15s
ocs-release-microservices-task-orchestrator-69555b676c-jnqt2      1/1     Running   0          15s
PS C:\Git\microservices\commandorchestration\helmcharts\orchestrate-command-services> kubectl get svc
NAME                                             TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
kubernetes                                       ClusterIP   10.96.0.1        <none>        443/TCP          35d
ocs-release-microservices-command-orchestrator   ClusterIP   10.110.54.37     <none>        9082/TCP         19s
ocs-release-microservices-sensor-simulator       ClusterIP   10.106.33.26     <none>        9084/TCP         19s
ocs-release-microservices-task-orchestrator      NodePort    10.106.218.124   <none>        9081:31200/TCP   19s
```
- [Check status and perform other Kubernetes operations](https://github.com/sbhrwl/microservices/blob/main/motivation/generatemessage/kubernetes/README.md#deploy-docker-images-on-kubernetes)
## Access services
* List of exposed URLs for your current services, assuming typical **NodePort** or **port-forwarding** access mappings for local development:
  * `http://localhost:30081/` → `task-orchestrator`
  * ClusterIP service → `command-orchestrator`
  * Kafka listener → `protocol-gateway`
  * ClusterIP service → `sensor-simulator`
## Verify HPA
- **`kubectl get hpa`**
```
PS C:\Git\microservices\commandorchestration\hpa\orchestrate-command-services-with-hpa> kubectl get hpa
NAME                       REFERENCE                         TARGETS              MINPODS   MAXPODS   REPLICAS   AGE
command-orchestrator-hpa   Deployment/command-orchestrator   cpu: <unknown>/80%   1         5         0          49s
protocol-gateway-hpa       Deployment/protocol-gateway       cpu: <unknown>/80%   1         5         0          49s
sensor-simulator-hpa       Deployment/sensor-simulator       cpu: <unknown>/80%   1         5         0          49s
task-orchestrator-hpa      Deployment/task-orchestrator      cpu: <unknown>/80%   1         5         0          49s
```
- Describe a specific HPA: **`kubectl describe hpa registration-hpa`**
```
PS C:\Git\microservices\commandorchestration\hpa\orchestrate-command-services-with-hpa> kubectl describe hpa task-orchestrator-hpa
Name:                                                  task-orchestrator-hpa
Namespace:                                             default
Labels:                                                app=task-orchestrator
                                                       app.kubernetes.io/managed-by=Helm
Annotations:                                           meta.helm.sh/release-name: ocs-hpa-release
                                                       meta.helm.sh/release-namespace: default
CreationTimestamp:                                     Mon, 09 Jun 2025 20:33:05 +0300
Reference:                                             Deployment/task-orchestrator
Metrics:                                               ( current / target )
  resource cpu on pods  (as a percentage of request):  <unknown> / 80%
Min replicas:                                          1
Max replicas:                                          5
Deployment pods:                                       0 current / 0 desired
Conditions:
  Type         Status  Reason          Message
  ----         ------  ------          -------
  AbleToScale  False   FailedGetScale  the HPA controller was unable to get the target's current scale: deployments/scale.apps "task-orchestrator" not found
Events:
  Type     Reason          Age   From                       Message
  ----     ------          ----  ----                       -------
  Warning  FailedGetScale  13s   horizontal-pod-autoscaler  deployments/scale.apps "task-orchestrator" not found
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
