# Moving to production
- **Migration from local NodePort → cloud LoadBalancer** for your setup:
## Update ingress controller
* Change Helm install for cloud:
```yaml
controller:
  service:
    type: LoadBalancer
```
* This lets the cloud provider assign a public/internal IP to the NGINX Ingress controller.
## Update `values.yaml` / environment-specific config
* Keep `ingress.host` as your **cloud domain**, e.g., `staging.fhs.example.com`.
* Enable TLS:
```yaml
ingress:
  tls: true
```
* Add TLS secret or use cloud-managed cert (ACM, Google Managed Cert, etc.).
## Internal service URLs
* Internal pod-to-pod communication stays the same (`data-api:8085`, `flexibility-hub-simulator:8081`).
* No NodePorts needed; services remain `ClusterIP`.
## DNS mapping
* Point your domain (`staging.fhs.example.com`) to the cloud LB IP.
* This replaces the local `hosts` mapping (`fhs.local → 127.0.0.1`).
## Test and validate
* Browser or curl:
```bash
curl https://staging.fhs.example.com/ui
curl https://staging.fhs.example.com/api/v1/requests/<requestID>/tracker
curl -X POST https://staging.fhs.example.com/simulator -H "Content-Type: application/json" -d '{"test":"ok"}'
```
* Ensure TLS works, Ingress routing is correct, and internal services respond.
## Best practices
* Use **cloud `internal` LB** if you want the cluster fully private (no public internet exposure).
* Keep `Ingress.host` consistent with your environment naming (`dev`, `staging`, `prod`).
* Enable autoscaling on both HPA and LB if traffic varies.

If you want, I can **draw a side-by-side diagram: local NodePort setup vs cloud LoadBalancer setup** for your services—it makes it very clear. Do you want me to do that?
