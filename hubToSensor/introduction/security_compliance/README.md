# Security and compliance
* **Authentication:** Keycloak securing UI & APIs (OIDC).
* **Service-to-service security:** TLS + mTLS for internal comms.
* **Secrets management:** Vault or K8s Secrets with rotation.
* **Certificates:** managed via cert-manager (K8s).
* **HTTPS exposure:** Ingress controller + certs.
* **Zero trust mention:** identity-based verification between services.
* **Audit logs:** every request/response traceable.
