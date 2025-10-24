# Restarting Ingress controller
- [Identify your ingress controller pod](#identify-your-ingress-controller-pod)
- [Bring down the ingress pod](#bring-down-the-ingress-pod)
- [Restart](#restart)
## Identify your ingress controller pod
- To find the pod name, run one of these commands:
```bash
kubectl get pods -n ingress-nginx
```
- if it’s in another namespace:
```
kubectl get pods -A | grep ingress
```
## Bring down the ingress pod
- To simulate stopping it (a soft restart), delete the pod 
- Kubernetes will recreate it automatically if it’s managed by a Deployment (which it usually is):
```
kubectl delete pod <pod-name> -n ingress-nginx
```
- If you want it fully down (a complete stop), scale its Deployment to 0:
```
kubectl scale deployment ingress-nginx-controller -n ingress-nginx --replicas=0
```
- That stops it completely — no ingress routing will work now.
## Restart
- When you’re ready to restart, scale the Deployment back to 1:
```
kubectl scale deployment ingress-nginx-controller -n ingress-nginx --replicas=1
```
- You can verify it came back up:
```
kubectl get pods -n ingress-nginx
```
- Check logs or status
To see if the controller is healthy
```
kubectl logs -n ingress-nginx -l app.kubernetes.io/name=ingress-nginx --tail=50
```