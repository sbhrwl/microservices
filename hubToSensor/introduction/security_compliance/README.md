# Security and compliance
* **Authentication:** Keycloak securing UI & APIs (OIDC).
* **Service-to-service security:** TLS + mTLS for internal comms.
* **Secrets management:** Vault or K8s Secrets with rotation.
* **Certificates:** managed via cert-manager (K8s).
* **HTTPS exposure:** Ingress controller + certs.
* **Zero trust mention:** identity-based verification between services.
* **Audit logs:** every request/response traceable.
---


🔐 Section 6: Security & Compliance (15 min)


---

🧱 Slide 1 – Security by Design

Visual: Shield icon across entire architecture diagram.

Key points:
Security is embedded from the start, not bolted on later.
Every service communicates over secure channels.
Least privilege and isolation at every layer.


Speaker note:

> “We build security into the foundation — not as a patch, but as a principle.”
---

🧍‍♂️ Slide 2 – Authentication & Authorization

Visual: Keycloak icon near UI and API gateway.

Key points:
Keycloak manages users, tokens, and roles.
OAuth2.0 and OpenID Connect for identity.
Role-based access for APIs and UI.


Speaker note:

> “Keycloak ensures only the right users and services access the right data.”

---

🔒 Slide 3 – Secure Communication

Visual: Padlocks on message flows; TLS/mTLS labels on broker links.

Key points:
All service-to-service communication secured via TLS/mTLS.
HTTPS for UI and external access.
Certificates managed via cert-manager in Kubernetes.

Speaker note:

> “Every byte moving between services is encrypted — no plain-text traffic anywhere.”
---

🧰 Slide 4 – Secrets & Certificates Management

Visual: Vault or Kubernetes Secrets icon with rotation arrows.

Key points:
Sensitive data stored in Kubernetes Secrets or HashiCorp Vault.
Automatic rotation and revocation policies.
Environment isolation for secrets.


Speaker note:

> “Secrets are like passwords — they need vaults, not code comments.”
---

🧾 Slide 5 – Compliance & Auditability

Visual: Checklist with compliance icons (ISO 27001, GDPR, etc.).

Key points:
Audit logs capture every command and response.
Traceability across message flow (Broker → Bridge → Adapter → HES).
Aligns with enterprise standards (ISO, NERC CIP, IEC 62351).


Speaker note:

> “Security isn’t just about encryption — it’s about accountability and traceability.”
