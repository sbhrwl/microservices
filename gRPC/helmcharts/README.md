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
- [**ingestion-deployment**](orchestrate-ingestion-grpc-services/templates/ingestion-grpc-deployment.yaml)
- [**ingestion-service**](orchestrate-ingestion-grpc-services/templates/ingestion-grpc-service.yaml)
- [**hub-deployment**](orchestrate-ingestion-grpc-services/templates/hub-deployment.yaml)
- [**hub-service**](orchestrate-ingestion-grpc-services/templates/hub-service.yaml)
- [**`values.yaml`**](orchestrate-ingestion-grpc-services/values.yaml)
### Chart structure
```pgsql
orchestrate-ingestion-grpc-services/
├── templates/
│   ├── ingestion-grpc-deployment.yaml
│   ├── ingestion-grpc-service.yaml
│   ├── hub-grpc-deployment.yaml
│   ├── hub-grpc-service.yaml
│   └── _helpers.tpl
├── Chart.yaml
├── values.yaml
├── .helmignore
```
## Cleanup existing Kubernetes deployment 
```
kubectl delete -f ingestion-grpc-service.yaml
kubectl delete -f hub-service.yaml
```
## Install Helm release
- Go to Helm chart folder [**orchestrate-ingestion-services**](orchestrate-ingestion-grpc-services)
```powershell
helm install ocs-grpc-release .
```
- **`ocs-grpc-release`** is the name you're assigning to this Helm release (you can change it if you like).
- `.` means Helm will *install using the chart in the current directory*.
## Verify deployment
```
PS C:\Git\microservices\gRPC\helmcharts\orchestrate-ingestion-grpc-services> helm install ocs-grpc-release .
NAME: ocs-grpc-release
LAST DEPLOYED: Fri Jun 13 12:26:40 2025
NAMESPACE: default
STATUS: deployed
REVISION: 1
TEST SUITE: None
PS C:\Git\microservices\gRPC\helmcharts\orchestrate-ingestion-grpc-services> helm list
NAME                    NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                           APP VERSION
ocs-grpc-release        default         1               2025-06-13 12:26:40.5940937 +0300 EEST  deployed        ingestion-grpc-service-0.1.0
PS C:\Git\microservices\gRPC\helmcharts\orchestrate-ingestion-grpc-services> kubectl get pods
NAME                                                       READY   STATUS    RESTARTS   AGE
ocs-grpc-release-hub-service-766d89bbfc-g9xbh              1/1     Running   0          7s
ocs-grpc-release-ingestion-grpc-service-86879f9db6-h5bc6   1/1     Running   0          7s
PS C:\Git\microservices\gRPC\helmcharts\orchestrate-ingestion-grpc-services> kubectl get svc
NAME                                      TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)              AGE
kubernetes                                ClusterIP   10.96.0.1        <none>        443/TCP              39d
ocs-grpc-release-hub-service              ClusterIP   10.102.37.156    <none>        9082/TCP,50051/TCP   13s
ocs-grpc-release-ingestion-grpc-service   NodePort    10.105.126.205   <none>        9081:30081/TCP       13s
```
- [Check status and perform other Kubernetes operations](https://github.com/sbhrwl/microservices/blob/main/motivation/generatemessage/kubernetes/README.md#deploy-docker-images-on-kubernetes)
## Access services
* List of exposed URLs for your current services, assuming typical **NodePort** or **port-forwarding** access mappings for local development:
  * `localhost:30081/generate/registration` → `ingestion-service`
## Uninstall Helm release
- Delete all Kubernetes resources (Deployments, Services, etc.) that were created by the Helm release named `ocs-grpc-release`
```
helm uninstall ocs-grpc-release
``` 
