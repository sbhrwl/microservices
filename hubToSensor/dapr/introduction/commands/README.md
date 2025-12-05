# Commands
* [Inspect pods and services](#inspect-pods--services)
* [Describe pod and list containers](#describe-pod--list-containers)
* [View logs — app and Dapr sidecar](#view-logs-—-app-and-dapr-sidecar)
* [Exec into pod and call the sidecar API](#exec-into-pod-and-call-the-sidecar-api)
* [Port-forward to access app or sidecar locally](#port-forward-to-access-app-or-sidecar-locally)
* [Restart / recycle safely](#restart--recycle-safely)
* [Useful cluster / dapr commands](#useful-cluster--dapr-commands)
* [Quick troubleshooting checklist](#quick-troubleshooting-checklist)
## Inspect pods and services
```bash
# list pod(s) (you already ran)
kubectl get pods -n <ns>

# check service details
kubectl get svc storage-service storage-service-dapr -n <ns> -o wide

# view endpoints to ensure pod is behind the service
kubectl get endpoints storage-service -n <ns>
kubectl get endpoints storage-service-dapr -n <ns>
```
## Describe pod and list containers
```bash
# full pod description (events, conditions, volumes, init containers)
kubectl describe pod storage-service-7bd7db66f9-pk67j -n <ns>

# list container names inside the pod (to know exact sidecar container name)
kubectl get pod storage-service-7bd7db66f9-pk67j -n <ns> -o jsonpath='{.spec.containers[*].name}'; echo
```
* Typical Dapr sidecar container name is `daprd` — but confirm with the jsonpath command above before using `-c`.
## View logs — app and Dapr sidecar
```bash
# app container logs (replace <app-container-name> with the container name from above)
kubectl logs storage-service-7bd7db66f9-pk67j -n <ns> -c <app-container-name>

# Dapr sidecar logs (common name: daprd)
kubectl logs storage-service-7bd7db66f9-pk67j -n <ns> -c daprd

# tail both (follow) — two terminals or use kubectl plugin `stern`/`kail`
kubectl logs -f storage-service-7bd7db66f9-pk67j -n <ns> -c <app-container-name>
kubectl logs -f storage-service-7bd7db66f9-pk67j -n <ns> -c daprd
```
* Look in the daprd logs for registration messages, pubsub bindings, configuration loads, health check failures, or gRPC listen errors.
## Exec into pod and call the sidecar API
```bash
# open a shell inside the pod
kubectl exec -it storage-service-7bd7db66f9-pk67j -n <ns> -- /bin/sh
# or bash if available
kubectl exec -it storage-service-7bd7db66f9-pk67j -n <ns> -- /bin/bash

# from inside pod: check sidecar HTTP health (default Dapr HTTP port is 3500)
# (some clusters may map different ports — see container ports in `describe pod`)
curl http://localhost:3500/v1.0/healthz
curl http://localhost:3500/v1.0/metadata

# check the gRPC port (cannot curl) — but you can verify listening sockets if tools available:
# e.g., inside the pod:
netstat -tulpen | grep 3500 || ss -ltnp | grep daprd
```
* If `curl` to `localhost:3500` fails, check actual sidecar ports in pod spec (`kubectl describe pod`).
## Port-forward to access app or sidecar locally
```bash
# port-forward the app port (if you want to call the app directly)
kubectl port-forward pod/storage-service-7bd7db66f9-pk67j 8086:8086 -n <ns>
# then locally: http://localhost:8086/...

# port-forward the Dapr HTTP sidecar (if you want to call Dapr APIs from your machine)
kubectl port-forward pod/storage-service-7bd7db66f9-pk67j 3500:3500 -n <ns>
# then locally:
curl http://localhost:3500/v1.0/invoke/<target-service>/method/<path>
```
* Useful to reproduce invocations from your laptop and to isolate cluster network issues.
## Restart / recycle safely
```bash
# restart the deployment (recommended vs deleting pod directly)
kubectl rollout restart deployment storage-service -n <ns>

# or delete the pod to recreate it (Deployment will recreate)
kubectl delete pod storage-service-7bd7db66f9-pk67j -n <ns>
```
* Rolling restart gives controlled recovery; deleting pod forces new pod with fresh sidecar.
## Useful cluster / dapr commands
```bash
# check Dapr control plane (k8s) status (if you have Dapr CLI and kubeconfig)
dapr status -k

# view sidecar annotations on the pod (shows app-id, enabled components)
kubectl get pod storage-service-7bd7db66f9-pk67j -n <ns> -o yaml | yq '.metadata.annotations'

# show pods with Dapr sidecar enabled quickly
kubectl get pods -n <ns> -l app.kubernetes.io/managed-by=dapr -o wide
# (label varies; check your Dapr injection label or annotation)
```
* If you don’t have `yq`, use `kubectl get pod ... -o jsonpath='{.metadata.annotations}'`.
## Quick troubleshooting checklist
* Confirm the pod is **Running** and containers are **Ready** (you showed 2/2 — good).
* Check `daprd` logs for errors (startup, component loading, certificate/sidecar auth).
* Confirm app is listening on the expected port inside the pod (use `kubectl exec` + `ss`/`netstat`).
* Confirm the Dapr HTTP port (3500) is listening and `v1.0/healthz` returns OK.
* If cross-service invocation fails, `kubectl port-forward` to sidecar and try `v1.0/invoke/...` locally to reproduce.
* If config/secret/component loading fails, Dapr logs will show which component path or error to fix.
* Use `kubectl describe` events to catch image pull errors, CrashLoopBackOff reasons, or OOM kills.
