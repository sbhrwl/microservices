# Securing APIs
- [Authentication with API keys](authenticatewithapikeys/README.md)
  - Verify app: `GET` `http://localhost:8081/public`
  - Verify app: `GET` `http://localhost:8081/secure`
  - Modify `Headers`
  ```
	X-API-KEY : my-secret-api-key
	```
- [Authentication and authorisation with Keycloak](authenticatewithkeycloak/README.md)
  - `docker run -p 8081:8081 sbhrwldocker/secured-endpoint:v1.0.0`
	- Verify app: `GET` `http://localhost:8081/public`
	- Verify app: `GET` `http://localhost:8081/secure`
  - Modify `Body -> x-wwww-form-url-encoded`
	```
	grant_type : password
	client_id  : ClientForSecuredEndpointApp (created in Keycloak)
	username  : endpointaccessuser
	password  : password123
	```	    
	- Verify Swagger UI: `http://localhost:8081/swagger-ui/index.html`
- [Introduce a reverse proxy](reverseproxy/README.md)
  - Verify app: `GET` `http://localhost:8085/api/public`
  - Verify app: `GET` `http://localhost:8085/api/secure`
  - Verify Swagger UI: `http://localhost:8085/api/swagger-ui/index.html`
- [Introduce an API gateway](apigateway/README.md)
  - Verify app: `GET` `http://localhost:8080/public`
  - Verify app: `GET` `http://localhost:8080/secure`
