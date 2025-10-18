# Service discovery 

- RabbitMQ and PostgreSQL → external (accessed via hostIp).
- All microservices inside the cluster → talk to them over that external IP.
- Only a few services (UI, data API, flexibility-hub-simulator) → need external exposure.
- That confirms you don’t need Kubernetes-based service discovery for internal communication — only clean **ingress routing** for the exposed services.

# Ingress 
- I stead of exposing each service with a NodePort, you can use a single Ingress (or Ingress Controller like NGINX) to handle all external routes cleanly.
- That means, 
  - only one public endpoint (e.g., `flex-hub.example.com`) and 
  - paths route to each exposed service, like:
    - `/api` → `data-api`
    - `/ui` → `ui-app`
    - `/simulator` → `flexibility-hub-simulator`
