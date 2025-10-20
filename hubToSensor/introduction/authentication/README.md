# Authentication

## Scenario
- You host Keycloak yourself in Google Cloud.
- Keycloak acts as the identity provider (not just a broker).
- All users are created directly in Keycloak.
- Each user has:
  - Roles (for RBAC)
  - Tenant ID (matches tenant_id in your database)
- The app will enforce data isolation based on tenant_id from the token.

## Approach
- Shared realm is the right choice here.
- You control all users, roles, and claims centrally.
- Adding new tenants is just creating new users with tenant_id and roles.
- This scales well to 100 tenants without creating multiple realms.
- Use custom claim or attribute for tenant_id in the access token.