# Istio gateway
Yes, I can incorporate all services that require external exposure into the Istio configuration. Based on your kubectl get svc output, the following services are currently exposed externally via NodePort and need an Istio Gateway and VirtualService:
| Service Name | External Port (NodePort) | Internal Target Port |
|---|---|---|
| ui-app-service | 30880 | 8080 |
| data-api-service | 30885 | 8085 |
| flexibility-hub-simulator-service | 30881 | 8081 |
We will now modify the single YAML file to include routing for all three services using path-based routing on the same Istio Gateway.
Step 3: Apply Istio Resources (All Services) 🚀
3.4.1: Update ui-app-ingress.yaml
 * File to Edit: istio-config/ui-app-ingress.yaml (Overwrite the previous content).
   * Content: This single VirtualService routes based on the request URI path:
     * /ui-app/ routes to ui-app-service on port 8080.
     * /data-api/ routes to data-api-service on port 8085.
     * /flex-sim/ routes to flexibility-hub-simulator-service on port 8081.
   <!-- end list -->
   # istio-config/ui-app-ingress.yaml
apiVersion: networking.istio.io/v1beta1
kind: Gateway
metadata:
  name: staging-ingress-gateway
  namespace: staging
spec:
  selector:
    istio: ingressgateway # Selects the default Istio Ingress Gateway
  servers:
  - port:
      number: 80
      name: http
      protocol: HTTP
    hosts:
    - "*" # Accept traffic for any hostname
---
apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: staging-external-routes
  namespace: staging
spec:
  hosts:
  - "*"
  gateways:
  - staging-ingress-gateway
  http:
  # Route 1: UI App (on path /ui-app/)
  - match:
    - uri:
        prefix: /ui-app/
    route:
    - destination:
        host: ui-app-service
        port:
          number: 8080
  # Route 2: Data API (on path /data-api/)
  - match:
    - uri:
        prefix: /data-api/
    route:
    - destination:
        host: data-api-service
        port:
          number: 8085
  # Route 3: Flexibility Hub Simulator (on path /flex-sim/)
  - match:
    - uri:
        prefix: /flex-sim/
    route:
    - destination:
        host: flexibility-hub-simulator-service
        port:
          number: 8081

3.4.2: Apply the Configuration
 * Terminal/WSL Command (Apply the configuration):
   kubectl apply -f istio-config/ui-app-ingress.yaml

Please ensure the file is saved and run the kubectl apply command.


The command will apply an Istio Gateway and a VirtualService in your Kubernetes cluster, specifically in the staging namespace. These resources define the entry point and routing rules for external traffic trying to reach your microservices.
Here is a breakdown of what the command does:
Command
kubectl apply -f istio-config/ui-app-ingress.yaml

1. Istio Gateway: staging-ingress-gateway
 * What it is: The Gateway resource configures the Istio Ingress Controller (a specialized load balancer running as a Kubernetes Service/Deployment) to open specific ports and accept traffic for certain hostnames.
 * What it applies:
   * It tells the existing default istio-ingressgateway (in istio-system namespace) to listen on Port 80 (HTTP).
   * It sets the hosts to "*", meaning it will accept traffic regardless of the requested domain name.
   * It effectively makes the Istio Ingress Controller your new front door.
2. Istio VirtualService: staging-external-routes
 * What it is: The VirtualService resource defines the set of routing rules to be applied to traffic that has been accepted by the Gateway.
 * What it applies:
   * It binds the routing rules to the new staging-ingress-gateway.
   * It implements path-based routing to direct incoming traffic to your three services based on the URL path:
     * Traffic arriving at /ui-app/* is routed to the ui-app-service on port 8080.
     * Traffic arriving at /data-api/* is routed to the data-api-service on port 8085.
     * Traffic arriving at /flex-sim/* is routed to the flexibility-hub-simulator-service on port 8081.
In short, this command completely sets up the ingress configuration for your service mesh. After this, all external access to these services must go through the Istio Ingress Gateway using these defined paths.


That's an excellent question. Implementing the Istio Gateway changes how you access your services externally, shifting from direct NodePort access to path-based routing through the single Istio Ingress Gateway.
Here is the comparison table showing how your access process changes:
| Service | Access Before Istio (NodePort) | Access After Istio (Gateway/VirtualService) |
|---|---|---|
| flexibility-hub-simulator | http://localhost:30881/api/messages | http://localhost:<INGRESS_PORT>/flex-sim/api/messages |
| data-api-service | http://localhost:30885/api/v1/requests/<ID>/tracker | http://localhost:<INGRESS_PORT>/data-api/api/v1/requests/<ID>/tracker |
| ui-app | http://localhost:30880/ | http://localhost:<INGRESS_PORT>/ui-app/ |
Key Changes Explained
 * Single Port/IP: Instead of using a different NodePort for each service (30881, 30885, 30880), you will use the single external IP and port of the Istio Ingress Gateway (\text{<INGRESS\_PORT>}).
 * Path Prefix: You must now include the path prefix defined in the VirtualService (/flex-sim/, /data-api/, /ui-app/) to tell Istio which backend service to route your request to.
Next Step: Finding the Ingress Port
Before you can use the "After Istio" URLs, you must find the actual external IP and port of the default Istio Ingress Gateway service, which is running in the istio-system namespace.
 * Terminal/WSL Command (Find the Gateway Port):
   kubectl get svc istio-ingressgateway -n istio-system -o jsonpath='{.spec.ports[?(@.name=="http2")].nodePort}'

 * Note: This command will output the NodePort that Istio is listening on for HTTP traffic. You will use this port number in place of <INGRESS_PORT> in the table above.

