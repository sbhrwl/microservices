# [Helm charts](https://github.com/sbhrwl/system_design/blob/main/docs/deployment/containerisation/Kubernetes/deploymentstrategies/README.md)
- [Setup](setup/README.md)
- [Deploy](deploy/README.md)
- [Why Helm charts](#why-helm-charts)
- [Creating environments](creatingenvs/README.md)

## Why Helm charts
- **Reusability and Templating**
  * With Helm, you write templates (with `{{ }}`) instead of hardcoding values.
  * You can reuse the same chart across environments (dev, test, prod) just by changing `values.yaml`.
  * **Example:**
    ```yaml
    image: {{ .Values.command.image }}
    ```
    vs
    ```yaml
    image: sbhrwldocker/command-orchestration:latest
    ```
- **Configuration Management**
  * Helm lets you override values easily using:
    * `values.yaml`
    * `--set key=value` from CLI
    * environment-specific files
- **Packaging and Versioning**
  * Helm charts are versioned and can be stored in Helm repositories.
  * Makes CI/CD pipelines cleaner, repeatable, and easier to rollback.
- **Lifecycle Management**
  * Helm tracks installations and revisions (like a mini package manager).
  * You can:
    * **Upgrade** with `helm upgrade`
    * **Rollback** with `helm rollback`
    * **Uninstall** cleanly with `helm uninstall`
- **DRY Principle (Don't Repeat Yourself)**
  * Shared logic and values (like `env`, labels, or resource limits) are defined once and reused across multiple resources.
- **Organized Directory Structure**
  * Everything lives in a well-structured folder: deployments, services, values, templates, notes, etc.
- **Ecosystem & Community**
  * Most major open-source Kubernetes tools (like Prometheus, NGINX Ingress, ArgoCD) offer Helm charts—saves tons of time.
