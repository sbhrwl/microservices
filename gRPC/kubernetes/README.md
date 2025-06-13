# Kubernetes deployment
## Plan
 * Containerize Applications (Docker):
   * Create a Dockerfile for both the Ingestion Service and the Hub Service.
   * Build Docker images for each application (e.g., ingestion-service:latest, hub-service:latest).
   * Push these images to a container registry (e.g., Docker Hub, Google Container Registry).
 * Develop Helm Charts for Applications:
   * Create a Helm chart for the Ingestion Service and another for the Hub Service.
   * Each chart will define the Kubernetes resources for its respective application, using values.yaml for configurable parameters.
   * Chart Components (templates directory):
     * Deployment: Define the application pods, container images, resource requests/limits.
     * Service: Expose the application within the cluster (e.g., ClusterIP for Hub gRPC, NodePort/LoadBalancer/Ingress for Ingestion REST).
     * ConfigMap: Manage application.properties and other non-sensitive configurations.
     * Secret: Manage sensitive data like database passwords and ActiveMQ credentials.
     * Horizontal Pod Autoscaler (HPA): Define HPA resources to automatically scale pods based on CPU/Memory utilization or custom metrics.
 * Deploy Dependencies using Helm:
   * Utilize existing Helm charts for ActiveMQ (e.g., from Bitnami or official sources) and PostgreSQL to deploy them within your Kubernetes cluster.
   * Configure these charts via values.yaml to meet your service's connectivity requirements.
 * Deploy Applications to Kubernetes Cluster via Helm:
   * Install the Hub Service Helm chart.
   * Install the Ingestion Service Helm chart.
   * Use helm install <release-name> <chart-path> or helm upgrade --install commands.
 * Configure Horizontal Pod Autoscaler (HPA) within Charts:
   * Within each service's Helm chart, define the HPA object.
   * Specify the target CPU utilization percentage or memory usage, and the minimum/maximum number of replicas for scaling.
   * HPA will then automatically adjust the number of pods to meet demand.
This plan integrates Helm for managing your applications and their dependencies, and HPA for robust autoscaling.
