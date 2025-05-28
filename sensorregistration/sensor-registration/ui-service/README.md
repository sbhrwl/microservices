# UI service for registring sensors
- [Properties](#properties)
- [How to run](#how-to-run)
- [Test](#test)
- [Keycloak parameters](#keycloak-parameters)
## Properties
- [application.properties](src/main/resources/application.properties)
## How to run
- Create spring boot app with [spring initialiser](https://start.spring.io/)
- Build 
```bash
mvn clean install

mvn clean install -DskipTests
```
- Run
```bash
mvn spring-boot:run
```

## Test
- `localhost:9081/`
  - username: `endpointaccessuser`
  - password: `password123`
# Keycloak parameters
```json
Issuer: http://localhost:8080/realms/master
Claims:
{
  "exp": 1748417587,
  "iat": 1748413987,
  "auth_time": 1748413987,
  "jti": "990e1e59-0b32-488a-bc0a-f7d18a896135",
  "iss": "http://localhost:8080/realms/master",
  "aud": "account",
  "sub": "c77b483c-3a6c-4a0f-a962-2dee00208d1f",
  "typ": "Bearer",
  "azp": "sensor-service",
  "nonce": "ad4d0263-62e7-4d9f-836b-b35c401eb6d2",
  "session_state": "19307566-5a9e-4af9-877b-1ca580973e0e",
  "acr": "1",
  "allowed-origins": [
    "*"
  ],
  "realm_access": {
    "roles": [
      "endpointaccessrole",
      "default-roles-master",
      "offline_access",
      "uma_authorization"
    ]
  },
  "resource_access": {
    "account": {
      "roles": [
        "manage-account",
        "manage-account-links",
        "view-profile"
      ]
    }
  },
  "scope": "openid profile email",
  "sid": "19307566-5a9e-4af9-877b-1ca580973e0e",
  "email_verified": false,
  "preferred_username": "endpointaccessuser"
}
```
