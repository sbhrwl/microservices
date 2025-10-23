Index
 * Problem: monolithic ingress challenge
 * Motivation: unified and flexible external access
 * Evolution: from nodeport to istio ingress
 * Solution: istio gateway and virtualservice
 * Implementation: configuration and deployment
 * Takeaway: verifying access changes
Problem: monolithic ingress challenge
 * Initial state: Services are exposed individually using distinct NodePorts for external access.
 * Service exposure:
   * ui-app-service: Exposed on Port 30880 (targets 8080).
   * data-api-service: Exposed on Port 30885 (targets 8085).
   * flexibility-hub-simulator-service: Exposed on Port 30881 (targets 8081).
 * Inefficiency: Requires managing multiple external ports and IP/port combinations for client access.
Motivation: unified and flexible external access
 * Centralization: Establish a single, controlled entry point for all external traffic into the service mesh.
 * Routing control: Enable sophisticated path-based routing to direct traffic to different services based on the URL.
 * Decoupling: Eliminate direct client dependency on individual service NodePorts.
Evolution: from nodeport to istio ingress
 * Before Istio: Direct access via http://<HOST>:<NODEPORT>/<PATH>.
 * After Istio: Unified access via http://<INGRESS_IP>:<INGRESS_PORT>/<PREFIX>/<PATH>.
 * Goal: Shift the responsibility of external exposure from individual services (NodePort) to the Istio Ingress Gateway.
Solution: istio gateway and virtualservice
 * Istio Gateway (staging-ingress-gateway):
   * Configures the default Istio Ingress Controller (istio: ingressgateway).
   * Listens on Port 80 (HTTP).
   * Accepts traffic for all hostnames (hosts: "*").
   * Acts as the new front door for the service mesh in the staging namespace.
 * Istio VirtualService (staging-external-routes):
   * Binds routing rules to the staging-ingress-gateway.
   * Implements path-based routing to internal services:
     * /ui-app/ prefix routes to ui-app-service:8080.
     * /data-api/ prefix routes to data-api-service:8085.
     * /flex-sim/ prefix routes to flexibility-hub-simulator-service:8081.
Implementation: configuration and deployment
 * File and location: The configuration is defined in a single YAML file: istio-config/ui-app-ingress.yaml.
 * Deployment command:
   * kubectl apply -f istio-config/ui-app-ingress.yaml
 * Access verification (Next Step):
   * Determine the external access port of the Ingress Gateway:
     * kubectl get svc istio-ingressgateway -n istio-system -o jsonpath='{.spec.ports[?(@.name=="http2")].nodePort}'
   * The output of this command replaces <INGRESS_PORT>.
Takeaway: verifying access changes
 * New access pattern: All three services are now accessed via a single external IP and port (the Istio Ingress Gateway's NodePort) followed by a path prefix.
 * Example (Flexibility Hub Simulator):
   * Old URL: http://localhost:30881/api/messages
   * New URL: http://localhost:<INGRESS_PORT>/flex-sim/api/messages
 * The setup centralizes ingress, enforces consistent routing rules, and is a prerequisite for more advanced traffic management features.
