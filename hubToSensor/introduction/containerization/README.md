# Containerization and cloud deployment
- [From code to container](#from-code-to-container)
- [Artifact registry integration](#artifact-registry-integration)
- [Deploying with helm charts](#deploying-with-helm-charts)
- [Secrets certificates and secure configs](#secrets-certificates-and-secure-configs)
- [Deployment flow overview](#deployment-flow-overview)
## From code to container
- Each microservice is packaged as a Docker image.
- Ensures consistent runtime across environments.
- Lightweight, versioned, and fast to deploy.
- *Speaker note:* Containers eliminate environment inconsistencies — the same image runs everywhere.
## Artifact registry integration
- CI pipeline builds and tags Docker images (e.g., `bridge:v1.0`).
- Images are pushed to a secure Artifact Registry (GCR, ECR, or private).
- Versioning supports rollbacks and controlled releases.
- *Speaker note:* Each successful build becomes a traceable, reusable artifact of your codebase.
## Deploying with helm charts
- Helm manages Kubernetes manifests and configurations.
- Separates static templates from dynamic values (`values.yaml`).
- Simplifies deployment, upgrades, and rollbacks.
- *Speaker note:* Helm acts as a package manager for Kubernetes — enabling one-command deployments across services.
## Secrets certificates and secure configs
- Secrets managed using Kubernetes Secrets or Vault.
- Certificates issued and renewed automatically with cert-manager.
- TLS and HTTPS enforce encrypted communication.
- *Speaker note:* Every service treats the network as untrusted — encryption and secret management ensure secure operation.
## Deployment flow overview
- **Build:** CI generates versioned Docker images.
- **Push:** Artifacts are stored in the registry.
- **Deploy:** Helm deploys services to Kubernetes.
- **Monitor:** Observability tools track rollout and health.
- *Speaker note:* This automated flow enables rapid, reliable deployments from code commit to production containers.
