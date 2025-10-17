# Containerization
* **Docker**: each service runs in its own container.
* **Artifact Registry**: CI pushes built images (versioned).
* **Helm Charts**:

  * Define K8s manifests.
  * Handle config separation (ConfigMaps, Secrets).
  * Manage service dependencies and upgrades.
* **Infrastructure as Code**: mention Terraform for cloud setup.
* Deployment Flow:
  1. Build →
  2. Push artifact →
  3. Deploy via Helm →
  4. Monitor rollout.
---

Section 4: Containerization & Cloud Deployment (15 min)


---

🐳 Slide 1 – From Code to Container

Visual: Developer → Dockerfile → Image → Container icons.

Key points:
Each service packaged as a Docker image.
Ensures consistent runtime across environments.
Lightweight, fast to deploy, easy to version.


Speaker note:

> “Containers make ‘it works on my machine’ a thing of the past — the same image runs everywhere.”
---

🏗️ Slide 2 – Artifact Registry Integration

Visual: Pipeline pushing Docker images to Artifact Registry.

Key points:
CI pipeline builds and tags images (e.g., bridge:v1.0).
Pushed to secure registry (GCR, ECR, or private).
Versioning allows rollbacks and controlled releases.


Speaker note:

> “Every successful build becomes an artifact — a reusable, traceable snapshot of your code.”
---

⚙️ Slide 3 – Deploying with Helm Charts

Visual: Helm chart box → Kubernetes cluster (multiple pods).

Key points:
Helm manages Kubernetes manifests.
Separates configuration (values.yaml) from templates.
Simplifies upgrades and rollbacks.


Speaker note:

> “Helm is like a package manager for Kubernetes — one command can deploy your whole ecosystem.”
---

🔐 Slide 4 – Secrets, Certificates & Secure Configs

Visual: Locks around ConfigMaps, Secrets, TLS certificates.

Key points:
Secrets managed via Kubernetes Secrets or Vault.
Certificates auto-managed with cert-manager.
TLS and HTTPS ensure encrypted communication.


Speaker note:

> “Every service must assume the network is untrusted — encryption and secret management make it safe.”
---

🚀 Slide 5 – Deployment Flow Overview

Visual: CI/CD flow diagram
Build → Push → Helm Deploy → Monitor.

Key points:
Build artifacts via CI.
Push to registry.
Helm deploys to K8s.
Monitoring ensures rollout health.


Speaker note:
> “This end-to-end automation closes the loop — from developer code to production containers in minutes.”
