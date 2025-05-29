# Helm charts
- [Create Helm chart structure](#create-helm-chart-structure)
- [Clean up the default templates](#clean-up-the-default-templates)
- [Convert your existing YAML into a Helm template](#convert-your-existing-yaml-into-a-helm-template)
  - [Folder structure](#folder-structure)
- [Cleanup existing Kubernetes deployment](#cleanup-existing-kubernetes-deployment)
- [Install Helm release](#install-helm-release)
- [Verify deployment](#verify-deployment)
- [Access services](#access-services)
- [Uninstall Helm release](#uninstall-helm-release)
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
## Convert your existing YAML into a Helm template
- [**notification-deployment**](orchestrate-sensor-services/templates/notification-deployment.yaml)
- [**notification-service**](orchestrate-sensor-services/templates/notification-service.yaml)
- [**notification-configmap**](orchestrate-sensor-services/templates/notification-configmap.yaml)
- [**registration-deployment**](orchestrate-sensor-services/templates/registration-deployment.yaml)
- [**registration-service**](orchestrate-sensor-services/templates/registration-service.yaml)
- [**sensor-deployment**](orchestrate-sensor-services/templates/sensor-deployment.yaml)
- [**sensor-service**](orchestrate-sensor-services/templates/sensor-service.yaml)
- [**ui-deployment**](orchestrate-sensor-services/templates/ui-deployment.yaml)
- [**ui-service**](orchestrate-sensor-services/templates/ui-service.yaml)
- [**`values.yaml`**](orchestrate-sensor-services/values.yaml)
### Folder structure
```pgsql
orchestrate-sensor-services/
├── templates/
│   ├── notification-deployment.yaml
│   ├── notification-service.yaml
│   ├── notification-configmap.yaml
│   ├── registration-deployment.yaml
│   ├── registration-service.yaml
│   ├── sensor-deployment.yaml
│   ├── sensor-service.yaml
│   ├── ui-deployment.yaml
│   ├── ui-service.yaml
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
- Go to Helm chart folder (e.g., `orchestrate-sensor-services`), run this command:
  ```powershell
  helm install orchestrate-sensor-services-release . 
  ```
- **`orchestrate-sensor-services-release`** is the name you're assigning to this Helm release (you can change it if you like).
- `.` means Helm will *install using the chart in the current directory*.
- Helm will deploy both your services using the templates and values from `values.yaml`.
- Verify release `helm list`
  ```
  NAME                    NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                           APP VERSION
  orchestrate-sensor-services-release   default         1               2025-05-12 09:53:22.55453 +0300 EEST    deployed        orchestrate-sensor-services-0.1.0    1.16.0
  ```
## Verify deployment
- Helm list
  ```
  helm list -A
  ```
- Verify
```
C:\Git\microservices\sensorregistration\helmcharts\deploy\orchestrate-sensor-services>helm list
NAME                                    NAMESPACE       REVISION        UPDATED                                 STATUS  CHART                   APP VERSION
orchestrate-sensor-services-release     default         1               2025-05-28 22:20:01.3609859 +0300 EEST  deployedsensor-app-chart-0.1.0  1.0

C:\Git\microservices\sensorregistration\helmcharts\deploy\orchestrate-sensor-services>helm list -A
NAME                                    NAMESPACE       REVISION        UPDATED                                 STATUS  CHART                   APP VERSION
orchestrate-sensor-services-release     default         1               2025-05-28 22:20:01.3609859 +0300 EEST  deployedsensor-app-chart-0.1.0  1.0

C:\Git\microservices\sensorregistration\helmcharts\deploy\orchestrate-sensor-services>kubectl get pods
NAME                                    READY   STATUS    RESTARTS   AGE
notification-service-7f5845c77c-4fq8z   1/1     Running   0          26s
registration-service-86d67ff64-26kbp    1/1     Running   0          26s
sensor-service-7b4d75956f-dbt5j         1/1     Running   0          26s
ui-service-dfbb8d9df-rrnbp              1/1     Running   0          26s

C:\Git\microservices\sensorregistration\helmcharts\deploy\orchestrate-sensor-services>kubectl get svc
NAME                   TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
kubernetes             ClusterIP   10.96.0.1        <none>        443/TCP          23d
notification-service   ClusterIP   10.101.107.255   <none>        9084/TCP         34s
registration-service   ClusterIP   10.108.156.30    <none>        9083/TCP         34s
sensor-service         NodePort    10.109.161.68    <none>        9082:30082/TCP   34s
ui-service             NodePort    10.96.134.147    <none>        9081:30081/TCP   34s
```

- [Check status and perform other Kubernetes operations](https://github.com/sbhrwl/microservices/blob/main/motivation/generatemessage/kubernetes/README.md#deploy-docker-images-on-kubernetes)
## Access services
* List of exposed URLs for your current services, assuming typical **NodePort** or **port-forwarding** access mappings for local development:
  * `http://localhost:30081/...` → `ui-service`
  * `http://localhost:30082/api/register/sensor` → `sensor-service`
  * ClusterIP service → `registration-service`
  * ClusterIP service → `notification-service`
## Uninstall Helm release
- Delete all Kubernetes resources (Deployments, Services, etc.) that were created by the Helm release named `orchestrate-sensor-services-release`
  ```
  helm uninstall orchestrate-sensor-services-release
  ``` 
