# Istio setup
- [Istio installation](#istio-installation)
- [Install base components](#install-base-components)
- [Install control plane](#install-control-plane)
## Istio installation
* Terminal/WSL Command: Install Istio using Helm.
```
helm repo add istio https://istio-release.storage.googleapis.com/charts
helm repo update
kubectl create namespace istio-system
```
## Install base components
- `Istio CRDs/base components`
```
helm install istio-base istio/base -n istio-system --set defaultRevision=default
```

## Install control plane
- `Istio Control Plane (Istiod)`
```
helm install istiod istio/istiod -n istio-system --wait
```