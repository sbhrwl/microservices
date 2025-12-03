# UI app
- [Building new Angular app](https://github.com/sbhrwl/system_design/blob/main/docs/webapplication/new-app/README.md)
- [Run the app](#run-the-app)
  - [Port forward](#port-forward)
    - [Port forward for a namespace](#port-forward-for-a-namespace)
  - [App running locally and connected to data api service on running on kubernetes](#app-running-locally-and-connected-to-data-api-service-on-running-on-kubernetes)
  - [App running on kubernetes](#app-running-on-kubernetes)
- [Staging release](#staging-release)
## Run the app
- `ng serve`
- `http://localhost:4200/`
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
#### Port forward for a namespace
- `kubectl port-forward -n dev service/data-api-service 8085:8085`
- `kubectl port-forward -n staging service/data-api-service 8085:8085`
### App running locally and connected to data api service on running on kubernetes
- Run the app `http://localhost:4200/`
  - This connects to locally running `ui-app` to `data-api-service` **running on kubernetes**
### App running on kubernetes
- Run the app `http://localhost:30880/`
## Staging release
- Port forward: `kubectl port-forward service/data-api-service 8085:8085 -n staging`
- Run the app `http://localhost:30880/`
