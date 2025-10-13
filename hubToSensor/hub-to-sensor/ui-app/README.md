# UI app
- [Building new Angular app](https://github.com/sbhrwl/system_design/blob/main/docs/webapplication/new-app/README.md)
- [Run the app](#run-the-app)
## Run the app
- `ng serve`
- `http://localhost:4200/`

## Kubernetes deployment
### Get the IP for cluster
- `kubectl get nodes -o wide`: `192.168.65.3`
- Port:`30880/`
- App Url: `http://192.168.65.3:30880/`

### Port forward
- Run app locally using `ng serve`
- Perform Port forwarding
  - `kubectl port-forward service/ui-app-service 4200:8080`
  - `kubectl port-forward service/data-api-service 8085:8085`
- Run the app `http://localhost:4200/`
  - This connects to **data-api running on kubernetes**