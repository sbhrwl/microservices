# Helm charts
- [Create Helm chart structure](#create-helm-chart-structure)
- [Clean up the default templates](#clean-up-the-default-templates)
- [Convert your existing YAML into a Helm template](#convert-your-existing-yaml-into-a-helm-template)
  - [Folder structure](#folder-structure)
- [Cleanup existing Kubernetes deployment](#cleanup-existing-kubernetes-deployment)
- [Install Helm release](#install-helm-release)
- [Verify](#verify)
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
- **ui-service**
  - [`ui-deployment.yaml`](orchestrate-sensor-services/templates/ui-deployment.yaml)
  - [`ui-service.yaml`](orchestrate-sensor-services/templates/ui-service.yaml)
- **sensor-service**
  - [`sensor-deployment.yaml`](orchestrate-sensor-services/templates/sensor-deployment.yaml)
  - [`sensor-service.yaml`](orchestrate-sensor-services/templates/sensor-service.yaml)
- **registration-service**
  - [`registration-deployment.yaml`](orchestrate-sensor-services/templates/registration-deployment.yaml)
  - [`registration-service.yaml`](orchestrate-sensor-services/templates/registration-service.yaml)
- **notification-service**
  - [`notification-deployment.yaml`](orchestrate-sensor-services/templates/notification-deployment.yaml)
  - [`notification-service.yaml`](orchestrate-sensor-services/templates/notification-service.yaml)
- [**`values.yaml`**](orchestrate-sensor-services/values.yaml) should be inside `root` of the Helm chart folder, not inside the `templates/` folder
### Folder structure
```pgsql
orchestrate-sensor-services/
├── charts/
├── templates/
│   ├── ui-deployment.yaml
│   ├── ui-service.yaml
│   ├── sensor-deployment.yaml
│   ├── sensor-service.yaml
│   ├── registration-deployment.yaml
│   ├── registration-service.yaml
│   ├── notification-deployment.yaml
│   ├── notification-service.yaml
│   └── _helpers.tpl
├── Chart.yaml
├── values.yaml    ✅ should be here
```
## Cleanup existing Kubernetes deployment 
```
kubectl delete deployment ui-deployment
kubectl delete deployment sensor-deployment
kubectl delete deployment registration-deployment
kubectl delete deployment notification-deployment

kubectl delete service ui-service
kubectl delete service sensor-service
kubectl delete service registration-service
kubectl delete service notification-service

## OR
kubectl delete deployment ui-deployment sensor-deployment registration-deployment notification-deployment
kubectl delete service ui-service sensor-service registration-service notification-service
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
## Verify
- Helm list
  ```
  helm list -A
  ```
- **Check pods status**
  ```powershell
  kubectl get pods
  ```
- **Check services and exposed NodePorts**
  ```powershell
  kubectl get svc
  ```
  * `ui-service` on NodePort `30081`
  * `sensor-service` on ClusterIP `30082`
- **Access the services**
  * `http://localhost:30081/...` → `ui-service`
  * `http://localhost:30082/...` → `sensor-service`
  * `http://localhost:30083/...` → `registration-service`
  * `http://localhost:30084/...` → `notification-service`
## Uninstall Helm release
- Delete all Kubernetes resources (Deployments, Services, etc.) that were created by the Helm release named `orchestrate-sensor-services-release`
  ```
  helm uninstall orchestrate-sensor-services-release
  ``` 
