# Istio setup

## Plan

| Step | Focus/Tool | Goal | Implementation Change |
|---|---|---|---|
| 1. Istio Installation | Helm (istio/base, istio/istiod) | Install the Istio Control Plane (Istiod) using the official Helm charts. | Use helm repo add and helm install commands to deploy to istio-system namespace. |
| 2. Application Onboarding | Helm Chart Values / Namespace | Enable automatic Envoy sidecar injection for your application. | Add the label istio-injection: enabled to your application's namespace (or Deployment annotations) and re-deploy/upgrade your service via Helm. |
| 3. External Access (Ingress) | Istio Gateway | Define the mesh's public entry point using an Istio resource instead of a standard Ingress. | Folder/File: Create a separate manifest for the Gateway resource, pointing to the default istio-ingressgateway Service. |
| 4. Traffic Routing | Istio VirtualService | Route external traffic from the Gateway to your application service. | Folder/File: Create a VirtualService to link the new Gateway to your existing Kubernetes Service. |
| 5. Observability | Istio Add-ons / istioctl | Access the Service Mesh visualizer (Kiali). | Use kubectl apply -f to deploy the add-ons and istioctl dashboard kiali to launch the UI. |
Implementation Details: Step 2 (The Next Change)
We will start with the Istio installation (Step 1) and then move to application onboarding (Step 2).
Step 1 & 2: Istio Installation and Onboarding
 * Terminal/WSL Command: Install Istio using Helm.
   helm repo add istio https://istio-release.storage.googleapis.com/charts
helm repo update
kubectl create namespace istio-system

# Install the Istio CRDs/Base components
helm install istio-base istio/base -n istio-system --set defaultRevision=default

# Install the Istio Control Plane (Istiod)
helm install istiod istio/istiod -n istio-system --wait

 * Onboarding Change (Application Namespace): Label the namespace where your services are deployed.
   * Terminal/WSL Command:
     kubectl label namespace <YOUR_APP_NAMESPACE> istio-injection=enabled

   * Note: Replace <YOUR_APP_NAMESPACE> with the actual namespace your Helm charts target (e.g., default, dev, etc.).
 * Redeploy Services: Upgrade your existing service Helm charts to trigger the injection.
   * Terminal/WSL Command:
     helm upgrade <YOUR_RELEASE_NAME> <YOUR_CHART_PATH> -n <YOUR_APP_NAMESPACE>

   * Wait for the new pods to be ready. They should now show 2/2 containers (your app + Envoy sidecar).
