# Automation
- [Using GitOps tools](#using-gitops-tools)
- [Script to create namespaces and install all 3 environments in one go](#script-to-create-namespaces-and-install-all-3-environments-in-one-go)
- [Uninstallation](#uninstallation)
- [Scripts that takes environment name as input parameter](#scripts-that-takes-environment-name-as-input-parameter)
- [Checking differences before applying changes](#checking-differences-before-applying-changes)
## Using GitOps tools
- This is Optional but consider tools like ArgoCD or Flux to deploy based on Git branches/folders for `dev`, `staging`, `prod`.

## Script to create namespaces and install all 3 environments in one go
- This PowerShell script ([`deploy-all.ps1`](https://github.com/sbhrwl/microservices/tree/main/sensorregistration/helmcharts/deploy/deploy-all.ps1)) creates the `dev`, `staging`, and `prod` namespaces and deploy Helm releases using the appropriate values files:
- Save this as `deploy-all.ps1` in your project root.
  ```
  project-root/
  ├── orchestrate-sensor-services/
  ├── envs/
  ├── deploy-all.ps1   <-- here
  ├── uninstall-all.ps1   <-- here
  ```
- Open PowerShell and run:
  ```powershell
  Set-ExecutionPolicy RemoteSigned -Scope Process
  ./deploy-all.ps1
  ```
## Uninstallation
- The PowerShell script ([`uninstall-all.ps1`](https://github.com/sbhrwl/microservices/tree/main/sensorregistration/helmcharts/deploy/uninstall-all.ps1)) **uninstalls** the Helm releases and delete the associated namespaces:
- Save this as `uninstall-all.ps1` in your project folder.
- Run it in PowerShell:
  ```powershell
  ./uninstall-all.ps1
  ```
## Scripts that takes environment name as input parameter
- Installation
  - From project root, Run: [`.\deploy.ps1 dev`](https://github.com/sbhrwl/microservices/tree/main/sensorregistration/helmcharts/deploy/deploy.ps1)
  - This will:
    - Use the `orchestrate-sensor-services` Helm chart.
    - Install or upgrade the release with name `orchestrate-sensor-services-release-dev`.
    - Apply the config from `envs/dev/values.yaml`
- Uninstallation
  - From project root, Run: [`.\uninstall.ps1 dev`](https://github.com/sbhrwl/microservices/tree/main/sensorregistration/helmcharts/deploy/uninstall.ps1)
  - This will:
    - Uninstall the Helm release `orchestrate-sensor-services-release-dev`.
## Checking differences before applying changes
- **Preview rendered Kubernetes YAML with Helm**
  - From project root, Run: [.\preview.ps1 dev](https://github.com/sbhrwl/microservices/tree/main/sensorregistration/helmcharts/deploy/preview.ps1)
  - This will show **what Helm will deploy**, without actually deploying it.
- **Diff changes before upgrading a release (requires `helm-diff` plugin)**
  - From project root, Run: [.\diff.ps1 dev](https://github.com/sbhrwl/microservices/tree/main/sensorregistration/helmcharts/deploy/diff.ps1)
  - To use the `helm diff` plugin, install it once:
    ```bash
    helm plugin install https://github.com/databus23/helm-diff
    ```
