# Keycloak setup
* [Keycloak Documentation](https://www.keycloak.org/documentation)
* [`docker-compose.yaml`](docker-compose.yaml)
  * Keycloak V26: [`docker-compose.yaml`](https://github.com/sbhrwl/microservices/blob/main/sensorregistration/prerequisites/keycloak/keycloakv26/docker-compose.yaml)
* Start Keycloak
```bash
docker-compose up -d
```
* Access Keycloak
  * URL: [http://localhost:8080](http://localhost:8080)
  * Username: `admin`
  * Password: `admin`
* Stop Services
```bash
docker-compose down
```
