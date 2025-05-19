# API gateway
- [Introduction](#introduction)
- [Benefits](#benefits)
- [Setup](#setup)
  - [Kong setup](#kong-setup)
  - [Register your API service](#register-your-api-service)
  - [Create routes](#create-routes)
- [Service discovery](#service-discovery)
## Introduction
```
[ Postman ] ──▶ [ Kong Gateway (localhost:8000) ]
                        │
                        ├── /public  ─▶  http://localhost:8081/public (no auth)
                        └── /secure ─▶  http://localhost:8081/secure (JWT auth)
```
## Benefits
| Feature               | Reverse Proxy (Nginx) | API Gateway (Kong) |
|----------------------|------------------|-----------------|
| Logging        | Needs external tools (e.g., ELK) | Built-in logging & analytics |
| Request transformation | Limited | Modify headers, query params, and response body |
| Basic DNS discovery  | Yes (manually)      | Yes (automatic or via plugins)       |
| Auto scaling support | No (without reload) | Yes (e.g., with K8s or service mesh) |
| Plugin support       | Limited             | Rich ecosystem                       |
| Centralized policies | No                  | Yes (auth, rate-limit, CORS, etc.)   |

## Setup
### Kong setup
- Create [`docker-compose.yaml`](nginx-reverse-proxy/docker-compose.yaml)
  - This spins up:
    - Kong Gateway
    - Kong Admin API
    - Postgres (for Kong’s config DB)
- Run the container
  - `docker-compose up -d`
    * Kong Admin API at `http://localhost:8001`
	* **Kong Proxy** at `http://localhost:8000`
  - Verify: 
    - Once it starts successfully, visit: `http://localhost:8001/status`
	- You should get a **JSON** with `Kong’s status`
### Register your API service
- Set the request to:
  * **Method:** `POST`
  * **URL:** `http://localhost:8001/services`
- In the **Body** tab:
  * Choose `x-www-form-urlencoded`
  * Add two key-value pairs:
    * `name` = **`my-api`**
    * `url` = `http://host.docker.internal:8081`
- Click **Send**
  - You should see a `201 Created` response.
	```json
	{
	    "write_timeout": 60000,
	    "retries": 5,
	    "path": null,
	    "port": 8081,
	    "client_certificate": null,
	    "connect_timeout": 60000,
	    "tls_verify": null,
	    "read_timeout": 60000,
	    "tls_verify_depth": null,
	    "tags": null,
	    "host": "host.docker.internal",
	    "enabled": true,
	    "ca_certificates": null,
	    "created_at": 1747557077,
	    "updated_at": 1747557077,
	    "protocol": "http",
	    "id": "754264af-ec1f-47da-a5a1-f58b3a87214c",
	    "name": "my-api"
	}	
	```

### Create routes
- Create **`/public`** route
  - When someone calls `/public`, forward it to the **`my-api`** service.
  - `POST`: URL: `http://localhost:8001/services/my-api/routes`
  - In the **Body** tab:
    * Choose `x-www-form-urlencoded`
    * Add two key-value pairs:
      * `paths[]` = `/public`
      * `name` = `public-route`
  - Click **Send**
    - You should get `201 Created`.
	```json
	{
		"protocols": [
			"http",
			"https"
		],
		"paths": [
			"/public"
		],
		"strip_path": true,
		"service": {
			"id": "754264af-ec1f-47da-a5a1-f58b3a87214c"
		},
		"hosts": null,
		"preserve_host": false,
		"regex_priority": 0,
		"headers": null,
		"destinations": null,
		"methods": null,
		"snis": null,
		"id": "6cd1b64a-835f-44d4-bc66-48db421a4fee",
		"request_buffering": true,
		"response_buffering": true,
		"tags": null,
		"sources": null,
		"created_at": 1747557551,
		"path_handling": "v0",
		"updated_at": 1747557551,
		"https_redirect_status_code": 426,
		"name": "public-route"
	}
	```
  - Verify: `http://localhost:8000/public`
    - It should call your backend app's `/public` endpoint.

- Create **`/secure`** route
  - When someone calls `/secure`, forward it to the **`my-api`** service.
  - `POST`: URL: `http://localhost:8001/services/my-api/routes`
  - In the **Body** tab:
    * Choose `x-www-form-urlencoded`
    * Add two key-value pairs:
      * `paths[]` = `/secure`
      * `name` = `secure-route`
  - Click **Send**
    - You should get `201 Created`.
	```json
	{
		"protocols": [
			"http",
			"https"
		],
		"paths": [
			"/secure"
		],
		"strip_path": true,
		"service": {
			"id": "754264af-ec1f-47da-a5a1-f58b3a87214c"
		},
		"hosts": null,
		"preserve_host": false,
		"regex_priority": 0,
		"headers": null,
		"destinations": null,
		"methods": null,
		"snis": null,
		"id": "6fb3736e-a9aa-403c-bce1-cf5cc85ba545",
		"request_buffering": true,
		"response_buffering": true,
		"tags": null,
		"sources": null,
		"created_at": 1747557679,
		"path_handling": "v0",
		"updated_at": 1747557679,
		"https_redirect_status_code": 426,
		"name": "secure-route"
	}
	```
  - Verify: `http://localhost:8000/secure`
    - It should call your backend app's `/secure` endpoint `http://localhost:8081/secure`
## Service discovery
- **Automatically locating backend services instead of hardcoding URLs**.
- Currently (without service discovery):
  - You manually configure Kong like this:
	* Service URL: `http://localhost:8081` (your app)
	* You define a route: `/secure`
	* Kong forwards requests to this fixed address
- With **service discovery**, Kong would:
	* Dynamically find your Spring Boot app by **name**, not IP or port
	* This is useful in **Kubernetes**, **Docker**, or **cloud-native** environments where:
	  * Services come and go
	  * IPs and ports change
  * In **Kubernetes**
    - Instead of hardcoding: `--data url=http://localhost:8081`
    - You’d use: `--data url=http://my-app.my-namespace.svc.cluster.local`
    - Or even better:
      - Use Kong’s **built-in integration with Kubernetes Service Discovery** to route to services **automatically** by name.
