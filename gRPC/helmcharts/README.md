# [Helm charts](https://github.com/sbhrwl/system_design/blob/main/docs/deployment/containerisation/Kubernetes/deploymentstrategies/README.md)
- [Setup](setup/README.md)
- [Create Helm chart structure](#create-helm-chart-structure)
- [Clean up the default templates](#clean-up-the-default-templates)
- [Convert deployment YAMLs into a Helm template](#convert-deployment-yamls-into-a-helm-template)
- [Chart structure](#chart-structure)
- [Cleanup existing Kubernetes deployment](#cleanup-existing-kubernetes-deployment)
- [Install Helm release](#install-helm-release)
- [Verify deployment](#verify-deployment)
- [Access services](#access-services)
- [Uninstall Helm release](#uninstall-helm-release)
## Create Helm chart structure
- Generate the basic `Helm chart directory`. 
- Run this in your terminal:
  ```
  helm create orchestrate-ingestion-services
  ```
- This creates a directory called [**orchestrate-ingestion-services**](orchestrate-ingestion-services) with default templates and values.
<img src="images/directorystructure.jpg">

## Clean up the default templates
- Helm’s `create` command generates a bunch of example templates we don’t need. Let’s simplify.
- Go to the `templates` folder:
  ```
  cd orchestrate-ingestion-services/templates
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
## Convert deployment YAMLs into a Helm template
- [**ingestion-deployment**](orchestrate-ingestion-services/templates/ui-deployment.yaml)
- [**ingestion-service**](orchestrate-ingestion-services/templates/ui-service.yaml)
- [**`values.yaml`**](orchestrate-ingestion-services/values.yaml)
### Chart structure
```pgsql
orchestrate-ingestion-services/
├── templates/
│   ├── ingestion-deployment.yaml
│   ├── ingestion-service.yaml
│   └── _helpers.tpl
├── Chart.yaml
├── values.yaml
├── .helmignore
```
## Cleanup existing Kubernetes deployment 
```
kubectl delete -f ingestion-services.yaml
```
## Install Helm release
- Go to Helm chart folder [**orchestrate-ingestion-services**](orchestrate-ingestion-services)
```powershell
helm install orchestrate-ingestion-services-release .
```
- **`orchestrate-ingestion-services-release`** is the name you're assigning to this Helm release (you can change it if you like).
- `.` means Helm will *install using the chart in the current directory*.
## Verify deployment
```
PS C:\Git\microservices\analytics\helmcharts\orchestrate-ingestion-services> helm install orchestrate-ingestion-services-release .
NAME: orchestrate-ingestion-services-release
LAST DEPLOYED: Tue Jun  3 13:43:28 2025
NAMESPACE: default
STATUS: deployed
REVISION: 1
TEST SUITE: None
PS C:\Git\microservices\analytics\helmcharts\orchestrate-ingestion-services> helm list
NAME                                    NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                           APP VERSION
orchestrate-ingestion-services-release  default         1               2025-06-03 13:43:28.7138803 +0300 EEST  deployed        ingestion-service-chart-0.1.0   1.0        
PS C:\Git\microservices\analytics\helmcharts\orchestrate-ingestion-services> helm list -A
NAME                                    NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                           APP VERSION
orchestrate-ingestion-services-release  default         1               2025-06-03 13:43:28.7138803 +0300 EEST  deployed        ingestion-service-chart-0.1.0   1.0        
PS C:\Git\microservices\analytics\helmcharts\orchestrate-ingestion-services> kubectl get pods 
NAME                                                              READY   STATUS    RESTARTS   AGE
orchestrate-ingestion-services-release-ingestion-service-7kzkns   1/1     Running   0          22s
PS C:\Git\microservices\analytics\helmcharts\orchestrate-ingestion-services> kubectl get svc
NAME                                                       TYPE        CLUSTER-IP     EXTERNAL-IP   PORT(S)          AGE
kubernetes                                                 ClusterIP   10.96.0.1      <none>        443/TCP          29d
orchestrate-ingestion-services-release-ingestion-service   NodePort    10.98.242.98   <none>        9081:30091/TCP   29s
```
- [Check status and perform other Kubernetes operations](https://github.com/sbhrwl/microservices/blob/main/motivation/generatemessage/kubernetes/README.md#deploy-docker-images-on-kubernetes)
## Access services
* List of exposed URLs for your current services, assuming typical **NodePort** or **port-forwarding** access mappings for local development:
  * `localhost:30081/api/powerquality/generate` → `ingestion-service`
## Uninstall Helm release
- Delete all Kubernetes resources (Deployments, Services, etc.) that were created by the Helm release named `orchestrate-ingestion-services-release`
```
helm uninstall orchestrate-ingestion-services-release
``` 
