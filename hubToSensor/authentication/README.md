# Authentication
- [Keycloak](keycloak/README.md)
## Scenario 1
- Keycloak hosted in Google Cloud.
  - Keycloak acts as the identity provider (not just a broker).
  - All users are created directly in Keycloak.
- Each user has
  - Roles (for RBAC)
  - Tenant ID (matches tenant_id in your database)
- The app will enforce data isolation based on tenant_id from the token.

## Approach
- Shared realm is the right choice here.
- You control all users, roles, and claims centrally.
- Adding new tenants is just creating new users with tenant_id and roles.
- This scales well to 100 tenants without creating multiple realms.
- Use custom claim or attribute for tenant_id in the access token.

## Scenario 2
- Building a flexibility management app (SaaS app) with multi-tenancy.
- App connects to an external HES application, which itself supports multiple tenants per instance.
- Example hierarchy:
  - Customer 1 (flexibility management app) → connects to HES instance 1 → HES has 5 tenants
  - Customer 2 (flexibility management app) → connects to HES instance 2 → HES has 4 tenants
- This creates a hierarchical tenant model:
  - Top-level: your flexibility app tenant (customer)
  - Nested: HES tenant(s) under that customer
### Problem statement 
- How to handle this hierarchy in Keycloak and flexibility management app so that:
  - Users of your app can see and manage only the HES tenants they are allowed to
  - Tokens carry enough info for RBAC and data filtering
### Discussion items
1. How to represent this hierarchy in Keycloak (roles, groups, or custom claims)
2. How to propagate HES tenant access through your app securely
3. How to enforce multi-level isolation in your queries and APIs
