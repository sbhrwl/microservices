# Multi-tenancy in Keycloak
* [Overview](#overview)
* [Problem](#problem)
* [Motivation](#motivation)
* [Evolution](#evolution)
* [Option 1 Tenant-based multi-tenancy using custom attributes](#option-1-tenant-based-multi-tenancy-using-custom-attributes)
* [Option 2 Multi-tenancy using organizations](#option-2-multi-tenancy-using-organizations)
* [Option 3 Multi-tenancy using multiple realms](#option-3-multi-tenancy-using-multiple-realms)
* [Common implementation checklist](#common-implementation-checklist)
* [Takeaway](#takeaway)
## Overview
- SaaS platforms often need to serve multiple customers (tenants) through a shared infrastructure while ensuring strict data isolation.
- Keycloak provides several ways to model multi-tenancy depending on the desired level of isolation and scalability.
## Problem
- A single-tenant design limits scalability as each customer requires a dedicated environment.
- The challenge is to introduce tenant-level data and access separation without duplicating infrastructure or Keycloak instances.
## Motivation
* Simplify customer onboarding and management.
* Support future growth (hundreds of customers).
* Maintain clear tenant isolation in both authentication and authorization.
* Ensure backend services can validate and restrict access by tenant.

## Evolution
- Three main patterns emerged for handling multi-tenancy in Keycloak:
1. **Custom attributes** to tag users with tenant information.
2. **Organizations** to group users logically within a realm.
3. **Multiple realms** for full isolation where required.

## Option 1 Tenant-based multi-tenancy using custom attributes
* **Concept**
  * Each user in a single realm has a `tenant_id` custom attribute.
  * This value is added to tokens via a **protocol mapper** and used by backend services to filter tenant data.
  * `super_admin` users can access all tenants.
* **Setup**
  * Add custom user attribute: `tenant_id`
  * Create a client scope (e.g., `tenant-scope`)
  * Add protocol mapper:
    * Type: *User Attribute*
    * User attribute: `tenant_id`
    * Token claim: `tenant_id`
    * Include in access and ID tokens
  * Attach the scope to clients
  * Define roles: `viewer`, `editor`, `admin`, `super_admin`
* **Token examples**
```json
// Tenant user
{
  "tenant_id": "tenant-001",
  "realm_access": { "roles": ["viewer"] },
  "aud": "app-backend"
}
```

```json
// Super admin
{
  "tenant_id": "all",
  "realm_access": { "roles": ["super_admin"] }
}
```
* **Backend validation**
```python
token = decode_jwt(request.headers["Authorization"])
tenant = token["tenant_id"]
if "super_admin" not in token["realm_access"]["roles"]:
    query = f"SELECT * FROM data WHERE tenant_id='{tenant}'"
else:
    query = "SELECT * FROM data"
```

* **Best practices**
  * Keep claim name consistent (`tenant_id`)
  * Validate the `aud` claim
  * Use short-lived tokens
  * Automate tenant provisioning via Admin REST API or Terraform
## Option 2 Multi-tenancy using organizations
* **Concept**
  * Each customer is a realm; within it, **organizations** represent tenants.
  * Users belong to organizations; membership defines access scope.
  * Organization data is added to tokens via protocol mappers.
* **Setup**
  * Enable **Organizations** under *Realm Settings → Feature toggle*
  * Create organizations (e.g., `CustomerA_Tenant1`, `CustomerA_Tenant2`)
  * Add users as members and assign roles (`org-admin`, `org-viewer`)
  * Add **Organization Membership** mapper:
    * Token claim: `organization`
    * JSON type: `String`
    * Include in tokens
* **Token example**
```json
{
  "organization": "CustomerA_Tenant2",
  "realm_access": { "roles": ["org-viewer"] },
  "organization_roles": ["org-viewer"]
}
```
* **Backend validation**
```python
token = decode_jwt(request.headers["Authorization"])
org = token["organization"]
query = f"SELECT * FROM data WHERE organization='{org}'"
```
* **Best practices**
  * Map only organization name or ID (not both)
  * Use organization roles for tenant-level access
  * Use a global super admin realm for analytics or shared data
  * Automate org creation with the REST API during onboarding
## Option 3 Multi-tenancy using multiple realms
* **Concept**
  * Each customer gets its own Keycloak realm.
  * Provides complete isolation for users, configurations, and integrations.
* **Setup**
  * Create separate realms (`customerA-realm`, `customerB-realm`)
  * Configure unique users, roles, mappers per realm
  * Optionally integrate with customer-specific identity providers
* **Pros**
  * Strong isolation for compliance or regulated industries
  * Easier per-customer customization and theming
  * Supports customer-managed identity providers
* **Cons**
  * Complex to manage and scale (100+ realms)
  * No centralized user visibility
  * Frontend and backend need realm discovery logic
  * Upgrades and configuration drift increase overhead
* **When to use**
  * Strictly isolated enterprise environments
  * Regulatory or data residency constraints
  * Customer-managed authentication scenarios

## Common implementation checklist

| Area                    | Recommendation                                    |
| ----------------------- | ------------------------------------------------- |
| **Token validation**    | Verify `aud`, `tenant_id` / `organization` claims |
| **Naming convention**   | Use lowercase, underscores                        |
| **Automation**          | Use REST API or Terraform for onboarding          |
| **Super admin**         | Realm-level role or dedicated admin realm         |
| **Mapping consistency** | Always link mappers to client or client scope     |
| **Role management**     | Use composite roles for complex access patterns   |

## Takeaway
* **Option 1** (custom attributes) is simplest and scalable for most SaaS models.
* **Option 2** (organizations) offers structured, tenant-aware access control.
* **Option 3** (multiple realms) provides strong isolation but poor scalability.
* Choose the model balancing **isolation, scalability,** and **manageability** for your SaaS environment.

```
smoc app => multiple realms, no organisation => backend => FE (SSO link with realm)

smoc app => one realm, multiple organisation => backend => FE (we are using SMOC like this, difficult to do SSO here)
SSO with organisation
```
