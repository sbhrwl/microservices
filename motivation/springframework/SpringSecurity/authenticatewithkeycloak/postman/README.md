# Postman 
- [Get the access token from Keycloak](#get-the-access-token-from-keycloak)
  - [Token](#token)
- [Call your secured endpoint](#call-your-secured-endpoint)
  - [Result](#result)
- [Refresh token](#refresh-token)
- [Decoded token](#decoded-token)
## Get the access token from Keycloak
- Open Postman.
- Create a **new `POST` request**.
- Enter this URL:
  ```
  http://localhost:8080/realms/<your-realm>/protocol/openid-connect/token
  ```
- Replace `<your-realm>` with your actual realm name (e.g. `demo` or `master`): `http://localhost:8080/realms/master/protocol/openid-connect/token`
- In the **Body tab**, select **x-www-form-urlencoded** and add these fields:

| Key            | Value                       |
| -------------- | --------------------------- |
| grant_type    | password                    |
| client_id     | your-client-id              |
| client_secret | your-client-secret (if set) |
| username       | your test user's username   |
| password       | your test user's password   |

- Click **Send**.
- Copy the value of `access_token` from the response.
### Token
```json
{
    "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJHcGFDU2NWUVZqU1Q3RmpEVWh3T042ZVFGYjVVellDZkVhci1kVDh4MnJNIn0.eyJleHAiOjE3NDczODM3ODksImlhdCI6MTc0NzM4MzcyOSwianRpIjoiNGVmNDBjODYtNWZkZS00ZWIxLTk3NDgtMTBmOWVlNDMwYjEzIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9tYXN0ZXIiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiYzc3YjQ4M2MtM2E2Yy00YTBmLWE5NjItMmRlZTAwMjA4ZDFmIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiQ2xpZW50Rm9yU2VjdXJlZEVuZHBvaW50QXBwIiwic2Vzc2lvbl9zdGF0ZSI6ImM1Y2QzMWRjLTA5NTgtNDU0OS05YWQ5LWFlMjczOTgwNzM0ZSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZW5kcG9pbnRhY2Nlc3Nyb2xlIiwiZGVmYXVsdC1yb2xlcy1tYXN0ZXIiLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiYzVjZDMxZGMtMDk1OC00NTQ5LTlhZDktYWUyNzM5ODA3MzRlIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJlbmRwb2ludGFjY2Vzc3VzZXIifQ.HBzRlaiiwhh5sZxtIuZ4_ulYbTX8ET_vinYANKDCdq3781PqioapszBuNLdDFzYf1WNzRIl35bmxr1_Z2TzXKDisRe1VN8UQF5jDqi8fV9ccsjZ_mbLoly42KslQfkq7ZY4D9MZVTp2uMuWi54iuzPLdZYvD0JowE_paENyegZB9BTmMkzVf8ZbTdhFTUD5545_J4vj6UAqMIqm4JB8F8TO2XCzP84nIv4xd20zCyte9bsWSe9TkHWiM7lrdsPgcDDIqSK80AKKQDXpFrOBqG4nWSStk_jFiv6p_I9D8uZH0jph8kveKIsXHh7ahTZrBHJphQhYMgWOl3lpvzOMwfw",
    "expires_in": 60,
    "refresh_expires_in": 1800,
    "refresh_token": "eyJhbGciOiJIUzUxMiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIyMjY1NWNjNy1lZGQzLTQ3NWUtYTg5OC0yMjMxYzYxYjMwYTMifQ.eyJleHAiOjE3NDczODU1MjksImlhdCI6MTc0NzM4MzcyOSwianRpIjoiMDg3NWVjNDEtMmNmNy00ZTQ4LWE2YzItMDk0Y2IwYzBkNzhmIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9tYXN0ZXIiLCJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvcmVhbG1zL21hc3RlciIsInN1YiI6ImM3N2I0ODNjLTNhNmMtNGEwZi1hOTYyLTJkZWUwMDIwOGQxZiIsInR5cCI6IlJlZnJlc2giLCJhenAiOiJDbGllbnRGb3JTZWN1cmVkRW5kcG9pbnRBcHAiLCJzZXNzaW9uX3N0YXRlIjoiYzVjZDMxZGMtMDk1OC00NTQ5LTlhZDktYWUyNzM5ODA3MzRlIiwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiYzVjZDMxZGMtMDk1OC00NTQ5LTlhZDktYWUyNzM5ODA3MzRlIn0.SKTacX0ka8SfDC2_sMaRkumHVfkTr14sgCbuTIu3HA5G81G3v07IL1OiOb_YzPOQYJK1DDbaZaONLcIQclGGkQ",
    "token_type": "Bearer",
    "not-before-policy": 0,
    "session_state": "c5cd31dc-0958-4549-9ad9-ae273980734e",
    "scope": "profile email"
}
```

## Call your secured endpoint
- Create a **new `GET` request** in Postman.
- URL of secured endpoint `/hello`:
  ```
  http://localhost:8081/hello
  ```
- In the **Authorization tab**, select **Bearer Token**.
- Paste the `access_token` you copied earlier.
- Click **Send**
### Result
- `Hello, secured world!`
## Refresh token
- **Use the refresh token to renew access tokens**
- When your access token expires (`expires_in: 60` seconds), you can use the `refresh_token` to get a new one:
  ```
  POST http://localhost:8080/realms/master/protocol/openid-connect/token
  Body (x-www-form-urlencoded):
  - grant_type: refresh_token
  - client_id: ClientForSecuredEndpointApp
  - client_secret: <your client secret>
  - refresh_token: <your refresh token>
  ```
- **Increase `access_token` lifetime (optional)**
  -  If 60 seconds is too short:
     * Go to **Realm Settings → Tokens**
     * Adjust **Access Token Lifespan** (e.g., 5 minutes, 10 minutes)
## Decoded token 
- The JSON Web Token (JWT) in its decoded form (Decoded from Base64URL).
### Header
- not shown explicitly in your input, but typically part of a JWT
- `alg`: The algorithm used for signing (likely "HS256" or "RS256", but not shown here)
- `typ`: "JWT"

### Payload containing claims
- `exp`: 1747383789 (Expiration time - Tue Jan 14 2025 16:23:09 GMT+0000)
- `iat`: 1747383729 (Issued at - Tue Jan 14 2025 16:22:09 GMT+0000)
- `jti`: "4ef40c86-5fde-4eb1-9748-10f9ee430b13" (Unique identifier for the token)
- `iss`: "http://localhost:8080/realms/master" (Issuer - Keycloak server)
- `aud`: "account" (Audience)
- `sub`: "c77b483c-3a6c-4a0f-a962-2dee0208d1f" (Subject - user ID)
- `typ`: "Bearer" (Token type)
- `azp`: "ClientForSecureEndpointApp" (Authorized party - client ID)
- `session_state`: "c5cd31dc-0958-4549-9ad9-ae273980734e"
- `acr`: "1" (Authentication Context Class Reference)
- `allowed-origins`: ["*"] (CORS allowed origins)
- **`realm_access`**: Contains roles:
  - "endpointaccessrole"
  - "default-roles-master"
  - "offline_access"
  - "uma_authorization"
- `resource_access`: Contains account roles:
  - "manage-account"
  - "manage-account-links"
  - "view-profile"
- `scope`: "profile email"
- `sid`: "c5cd31dc-0958-4549-9ad9-ae273980734e" (Session ID)
- `email_verified`: false
- `preferred_username`: "endpointaccessuser"

### Signature
- The signature would verify the token's authenticity.
### Summary
- This appears to be an access token issued by a Keycloak server for a user "endpointaccessuser" with various roles and permissions. 
- The token is valid until January 14, 2025.

