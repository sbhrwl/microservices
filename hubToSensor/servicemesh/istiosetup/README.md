# Istio setup

## Plan

| Step | Focus/Tool | Goal | Implementation Change |
|---|---|---|---|
| 1. Istio Installation | Helm (istio/base, istio/istiod) | Install the Istio Control Plane (Istiod) using the official Helm charts. | Use helm repo add and helm install commands to deploy to istio-system namespace. |
| 2. Application Onboarding | Helm Chart Values / Namespace | Enable automatic Envoy sidecar injection for your application. | Add the label istio-injection: enabled to your application's namespace (or Deployment annotations) and re-deploy/upgrade your service via Helm. |
| 3. External Access (Ingress) | Istio Gateway | Define the mesh's public entry point using an Istio resource instead of a standard Ingress. | Folder/File: Create a separate manifest for the Gateway resource, pointing to the default istio-ingressgateway Service. |
| 4. Traffic Routing | Istio VirtualService | Route external traffic from the Gateway to your application service. | Folder/File: Create a VirtualService to link the new Gateway to your existing Kubernetes Service. |
| 5. Observability | Istio Add-ons / istioctl | Access the Service Mesh visualizer (Kiali). | Use kubectl apply -f to deploy the add-ons and istioctl dashboard kiali to launch the UI. |

## Istio installation
* Terminal/WSL Command: Install Istio using Helm.
```
helm repo add istio https://istio-release.storage.googleapis.com/charts
helm repo update
kubectl create namespace istio-system
```
* Install the Istio CRDs/Base components
```
helm install istio-base istio/base -n istio-system --set defaultRevision=default
```

* Install the Istio Control Plane (Istiod)
```
helm install istiod istio/istiod -n istio-system --wait
```

## Application onboarding
### Automatic envoy sidecar injection
* Label the namespace where your services are deployed.
```
kubectl label namespace <YOUR_APP_NAMESPACE> istio-injection=enabled
```
  * Note: Replace <YOUR_APP_NAMESPACE> with the actual namespace your Helm charts target (e.g., default, dev, etc.).
* Redeploy Services: Upgrade your existing service Helm charts to trigger the injection.
```
helm upgrade <YOUR_RELEASE_NAME> <YOUR_CHART_PATH> -n <YOUR_APP_NAMESPACE>
```
* Wait for the new pods to be ready. 
  * They should now show `2/2 containers` (your app + Envoy sidecar).
## Istio gateway
