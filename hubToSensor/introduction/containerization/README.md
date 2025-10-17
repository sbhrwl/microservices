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
