# Istio gateway configuration
* [Problem](#problem)
* [Motivation](#motivation)
* [Evolution](#evolution)
* [Solution](#solution)
* [Implementation](#implementation)
* [Takeaway](#takeaway)
## Problem
* Multiple Kubernetes services were exposed externally via distinct NodePorts.
* Each service had its own external port, creating fragmented ingress management.
* Example:
  * `ui-app-service` → NodePort 30880
  * `data-api-service` → NodePort 30885
  * `flexibility-hub-simulator-service` → NodePort 30881
## Motivation
* Simplify ingress configuration by consolidating access to all externally exposed services.
* Replace multiple NodePort entries with a unified Istio ingress point.
* Enable path-based routing through the Istio Gateway for better scalability and control.
## Evolution
* Transition from direct NodePort access to a service mesh-managed ingress.
* Introduced Istio Gateway and VirtualService resources in the `staging` namespace.
* Enabled centralized control of HTTP traffic with a single external IP and port.
## Solution
* **Istio Gateway (`staging-ingress-gateway`)**
  * Configures Istio ingress controller to listen on port 80.
  * Accepts HTTP traffic from any hostname (`*`).
  * Acts as the unified entry point for external requests.
### Istio ingress gateway setup
* **Terminal/WSL Command (Install the Gateway Helm Chart):** 
  * We will install the official Istio Gateway chart into the `istio-system` namespace.
  ```bash
  helm install istio-ingressgateway istio/gateway -n istio-system --wait
  ```
* *Note: We are naming the release `istio-ingressgateway` to match the selector `istio: ingressgateway` used in your `Gateway` YAML.*
* Verify the Gateway Service
  * **Terminal/WSL Command (Check for the new Service):** 
  * Run this command to confirm the `istio-ingressgateway` service is now running and check its exposure type and ports.
    ```bash
    kubectl get svc -n istio-system
    ```
  * Logs
    ```
    PS C:\Git\microservices\hubToSensor\servicemesh\externalaccess> kubectl get svc -n istio-system
    NAME                   TYPE           CLUSTER-IP       EXTERNAL-IP   PORT(S)                                      AGE
    istio-ingressgateway   LoadBalancer   10.105.222.190   localhost     15021:32054/TCP,80:31989/TCP,443:32607/TCP   38s
    istiod                 ClusterIP      10.109.186.128   <none>        15010/TCP,15012/TCP,443/TCP,15014/TCP        6h32m  
    ```
### Istio VirtualService setup
* **Istio VirtualService (`staging-external-routes`)**
  * Binds routing rules to the above Gateway.
  * Implements URI path–based routing:
    * `/ui-app/` → `ui-app-service:8080`
    * `/data-api/` → `data-api-service:8085`
    * `/flex-sim/` → `flexibility-hub-simulator-service:8081`
## Implementation
* **YAML configuration:** [`istio-config/app-ingress.yaml`](istio-config/app-ingress.yaml)
* Go to `\servicemesh\externalaccess`
* **Apply command:**
  ```bash
  kubectl apply -f istio-config/app-ingress.yaml
  ```
* **Validate ingress port:**
  ```bash
  kubectl get svc istio-ingressgateway -n istio-system -o jsonpath='{.spec.ports[?(@.name=="http2")].nodePort}'
  ```
* Logs
```
PS C:\Git\microservices\hubToSensor\servicemesh\externalaccess> kubectl get svc -n istio-system
NAME                   TYPE           CLUSTER-IP       EXTERNAL-IP   PORT(S)                                      AGE
istio-ingressgateway   LoadBalancer   10.105.222.190   localhost     15021:32054/TCP,80:31989/TCP,443:32607/TCP   38s
istiod                 ClusterIP      10.109.186.128   <none>        15010/TCP,15012/TCP,443/TCP,15014/TCP        6h32m
PS C:\Git\microservices\hubToSensor\servicemesh\externalaccess> kubectl get svc istio-ingressgateway -n istio-system -o jsonpath='{.spec.ports[?(@.name=="http2")].nodePort}'
error: error executing jsonpath "{.spec.ports[?(@.name==http2)].nodePort}": Error executing template: unrecognized identifier http2. Printing more information for debugging the template:
        template was:
                {.spec.ports[?(@.name==http2)].nodePort}
        object given to jsonpath engine was:
                map[string]interface {}{"apiVersion":"v1", "kind":"Service", "metadata":map[string]interface {}{"annotations":map[string]interface {}{"meta.helm.sh/release-name":"istio-ingressgateway", "meta.helm.sh/release-namespace":"istio-system"}, "creationTimestamp":"2025-10-23T18:30:00Z", "labels":map[string]interface {}{"app":"istio-ingressgateway", "app.kubernetes.io/instance":"istio-ingressgateway", "app.kubernetes.io/managed-by":"Helm", "app.kubernetes.io/name":"istio-ingressgateway", "app.kubernetes.io/part-of":"istio", "app.kubernetes.io/version":"1.27.3", "helm.sh/chart":"gateway-1.27.3", "istio":"ingressgateway", "istio.io/dataplane-mode":"none"}, "managedFields":[]interface {}{map[string]interface {}{"apiVersion":"v1", "fieldsType":"FieldsV1", "fieldsV1":map[string]interface {}{"f:metadata":map[string]interface {}{"f:annotations":map[string]interface {}{".":map[string]interface {}{}, "f:meta.helm.sh/release-name":map[string]interface {}{}, "f:meta.helm.sh/release-namespace":map[string]interface {}{}}, "f:labels":map[string]interface {}{".":map[string]interface {}{}, "f:app":map[string]interface {}{}, "f:app.kubernetes.io/instance":map[string]interface {}{}, "f:app.kubernetes.io/managed-by":map[string]interface {}{}, "f:app.kubernetes.io/name":map[string]interface {}{}, "f:app.kubernetes.io/part-of":map[string]interface {}{}, "f:app.kubernetes.io/version":map[string]interface {}{}, "f:helm.sh/chart":map[string]interface {}{}, "f:istio":map[string]interface {}{}, "f:istio.io/dataplane-mode":map[string]interface {}{}}}, "f:spec":map[string]interface {}{"f:allocateLoadBalancerNodePorts":map[string]interface {}{}, "f:externalTrafficPolicy":map[string]interface {}{}, "f:internalTrafficPolicy":map[string]interface {}{}, "f:ports":map[string]interface {}{".":map[string]interface {}{}, "k:{\"port\":15021,\"protocol\":\"TCP\"}":map[string]interface {}{".":map[string]interface {}{}, "f:name":map[string]interface {}{}, "f:port":map[string]interface {}{}, "f:protocol":map[string]interface {}{}, "f:targetPort":map[string]interface {}{}}, "k:{\"port\":443,\"protocol\":\"TCP\"}":map[string]interface {}{".":map[string]interface {}{}, "f:name":map[string]interface {}{}, "f:port":map[string]interface {}{}, "f:protocol":map[string]interface {}{}, "f:targetPort":map[string]interface {}{}}, "k:{\"port\":80,\"protocol\":\"TCP\"}":map[string]interface {}{".":map[string]interface {}{}, "f:name":map[string]interface {}{}, "f:port":map[string]interface {}{}, "f:protocol":map[string]interface {}{}, "f:targetPort":map[string]interface {}{}}}, "f:selector":map[string]interface {}{}, "f:sessionAffinity":map[string]interface {}{}, "f:type":map[string]interface {}{}}}, "manager":"helm", "operation":"Update", "time":"2025-10-23T18:30:00Z"}, map[string]interface {}{"apiVersion":"v1", "fieldsType":"FieldsV1", "fieldsV1":map[string]interface {}{"f:status":map[string]interface {}{"f:loadBalancer":map[string]interface {}{"f:ingress":map[string]interface {}{}}}}, "manager":"kube-vpnkit-forwarder", "operation":"Update", "subresource":"status", "time":"2025-10-23T18:30:00Z"}}, "name":"istio-ingressgateway", "namespace":"istio-system", "resourceVersion":"2392777", "uid":"4ca26836-8565-4a3c-976a-cac3c751c442"}, "spec":map[string]interface {}{"allocateLoadBalancerNodePorts":true, "clusterIP":"10.105.222.190", "clusterIPs":[]interface {}{"10.105.222.190"}, "externalTrafficPolicy":"Cluster", "internalTrafficPolicy":"Cluster", "ipFamilies":[]interface {}{"IPv4"}, "ipFamilyPolicy":"SingleStack", "ports":[]interface {}{map[string]interface {}{"name":"status-port", "nodePort":32054, "port":15021, "protocol":"TCP", "targetPort":15021}, map[string]interface {}{"name":"http2", "nodePort":31989, "port":80, "protocol":"TCP", "targetPort":80}, map[string]interface {}{"name":"https", "nodePort":32607, "port":443, "protocol":"TCP", "targetPort":443}}, "selector":map[string]interface {}{"app":"istio-ingressgateway", "istio":"ingressgateway"}, "sessionAffinity":"None", "type":"LoadBalancer"}, "status":map[string]interface {}{"loadBalancer":map[string]interface {}{"ingress":[]interface {}{map[string]interface {}{"hostname":"localhost"}}}}}
```
* The HTTP NodePort is definitively **31989**
* **Access after Istio configuration:**

  | Service                   | Before Istio                                          | After Istio                                                             |
  | ------------------------- | ----------------------------------------------------- | ----------------------------------------------------------------------- |
  | flexibility-hub-simulator | `http://localhost:30881/api/messages`                 | `http://localhost:<INGRESS_PORT>/flex-sim/api/messages`                 |
  | data-api-service          | `http://localhost:30885/api/v1/requests/<ID>/tracker` | `http://localhost:<INGRESS_PORT>/data-api/api/v1/requests/<ID>/tracker` |
  | ui-app                    | `http://localhost:30880/`                             | `http://localhost:<INGRESS_PORT>/ui-app/`                               |

* **Key change:** single ingress IP/port replaces all NodePorts.
* Requests are distinguished using the URI prefix.

## Takeaway
* Consolidates external traffic routing under one Gateway.
* Reduces port management complexity and improves observability.
* Enables future scalability with Istio traffic policies and security features.
* All external access now flows through Istio’s managed ingress path structure.
