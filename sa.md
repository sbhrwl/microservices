# Service accounts
* **Node operations & cluster management:** `gke-node-01`
* **Workload access (pods to secrets, GCS, Cloud SQL):** `gke-wi-secrets-01`, `gke-wi-c0X`
* **Config sync / CI-CD / artifacts / container security:** `gke-wi-acs-01`, `gke-wi-acs-sa-01`
* **Default Compute Engine operations:** default compute service account

| API / Service            | Required service account                                 | Purpose                                                                                                                            |
| ------------------------ | -------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------- |
| Compute Engine           | `828752755010-compute@developer.gserviceaccount.com`     | Default service account for VM operations, node creation, and GKE control plane interactions.                                      |
| Google Kubernetes Engine | `gke-node-01@...`                                        | Node operations, pod runtime, and cluster management.                                                                              |
| Cloud SQL                | Dedicated Cloud SQL service account or Workload Identity | Pods accessing Cloud SQL need a service account with `Cloud SQL Client` role; could map to a WI service account like `gke-wi-c0X`. |
| Google Cloud Storage     | `gke-wi-secrets-01@...`                                  | Pods reading/writing buckets; use Workload Identity for access.                                                                    |
| Secret Manager           | `gke-wi-secrets-01@...`                                  | Access to secrets from applications running in GKE.                                                                                |
| Artifact Registry        | `gke-wi-acs-01@...` or `gke-wi-acs-sa-01@...`            | Pull/push container images for deployments via Anthos Config Sync or CI/CD pipelines.                                              |
| Cloud KMS                | `gke-wi-acs-01@...` or dedicated WI SA                   | Encrypt/decrypt secrets or application data.                                                                                       |
| Cloud Logging            | `gke-node-01@...`                                        | Nodes and pods write logs to Cloud Logging.                                                                                        |
| Cloud Monitoring         | `gke-node-01@...`                                        | Nodes and pods emit metrics to Cloud Monitoring.                                                                                   |
| Container Analysis       | `gke-wi-acs-01@...`                                      | Scan container images in Artifact Registry.                                                                                        |
| Container Security       | `gke-wi-acs-01@...`                                      | Security scanning / vulnerability management of container images.                                                                  |

