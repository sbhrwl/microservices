# UI app
- [Building new Angular app](https://github.com/sbhrwl/system_design/blob/main/docs/webapplication/new-app/README.md)
- [Run the app](#run-the-app)
## Run the app
- `ng serve`
- `http://localhost:4200/`

## Kubernetes deployment
### Get the IP for cluster
- `kubectl get nodes -o wide`: `192.168.65.3`
- Port:`30880`
- [Build app](src/environments/environment.prod.ts): `ng build --configuration=production`
- App Url: `http://192.168.65.3:30880/`
- This is still failing, so refer section **Port forward**
### Port forward
- Run app locally using `ng serve`
- Perform Port forwarding
  - **`kubectl port-forward service/data-api-service 8085:8085`**
  - This command will create a temporary, secure connection between a port on your **local machine** and the **`data-api-service`** Service running inside your Kubernetes cluster.
  - `What it will do`
    - It opens **local port 8085** on your computer.
    - Any traffic you send to `http://localhost:8085` will be **tunnelled** through the Kubernetes API server.
    - This traffic is then automatically directed to **port 8085** on one of the **Pods** managed by the `data-api-service` Service inside the cluster.
    - In simple terms, it makes your remote service accessible locally at `localhost:8085`, allowing you to test or debug it as if it were running on your machine.
- Run the app `http://localhost:4200/`
  - This connects to **data-api running on kubernetes**
