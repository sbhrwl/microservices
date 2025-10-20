# Keycloak
- [Setup](https://github.com/sbhrwl/microservices/blob/main/sensorregistration/prerequisites/README.md#keycloak-setup)

## Realm
- A **realm in Keycloak** is an isolated administrative and security domain.
- It manages a distinct set of:
  * **Users** and their credentials.
  * **Roles** (permissions) and **Groups**.
  * **Clients** (applications or services) that use Keycloak for security.
  * Specific **configurations** (like login themes, required actions, Identity Providers, and security policies).
## Realm as a SaaS Tenant
- A Keycloak realm is often used as a way to implement a tenant in the context of a SaaS application.
- However, it's more accurate to say that using one realm per tenant is a **common multi-tenancy model** in Keycloak, but not the only one.
  - For many applications, especially B2B SaaS where strong separation is critical, the `"one realm per tenant"` model is the most straightforward, as the isolation is enforced directly by Keycloak.
- [Keycloak realms (Part -2). configuring realms](https://medium.com/@gauravswarankar/keycloak-realms-part-2-6f8170003add)
- This video is relevant because it discusses Keycloak realms and their configuration, which is a key concept in understanding how they map to SaaS tenants.

| Model | Description | Isolation | Scalability |
| :--- | :--- | :--- | :--- |
| **One Realm per Tenant** | Each customer/tenant gets their own dedicated realm. | **Strong** (users, roles, and configuration are completely separate). | **Limited.** Keycloak performance degrades as the number of realms increases (e.g., beyond ~100). |
| **One Realm, Multiple Clients** | All tenants share a single realm, but each application/tenant is a separate **Client**. | **Weaker.** Isolation must be enforced by the application's code and careful role/group management within the single realm. | **High.** Can scale to thousands of tenants. |
| **One Realm, Multiple Organizations** | Using the newer (Keycloak v25+) **Organizations** feature to group tenants within a single realm. | **Moderate/High.** Provides a layer of isolation without the performance issues of multiple realms. | **High.** Aims to be the most scalable approach for B2B SaaS. |

