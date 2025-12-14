# Troubleshooting
- [Problem](#problem)
- [Conclusion](#conclusion)
## Problem
- After deploying via Helm, the `flexibility-hub-simulator-service` (NodePort 30881) was unreachable from the host, even though it worked before manual deployment.
- **Approach to diagnose:**
1. **Check pods and services** — confirm all running in the `default` namespace:
   ```bash
   kubectl get pods
   kubectl get svc
   ```
2. **Get ClusterIP of the service** (used for internal pod access):
   ```bash
   kubectl get svc flexibility-hub-simulator-service -o jsonpath='{.spec.clusterIP}'
   ```
   * This gives the internal service IP (`10.x.x.x`).
3. **Get Node IP of the cluster node** (used for NodePort access inside the cluster):
   ```bash
   kubectl get nodes -o wide
   ```
   * This shows the internal node IP (`192.168.65.3` for Docker Desktop).
4. **Check NodePort mapping in service YAML** (ensure `targetPort` matches container port):
   ```bash
   kubectl get svc flexibility-hub-simulator-service -o yaml
   ```
   * Confirms `port: 8081` → `nodePort: 30881`.
5. **Check internal pod connectivity** using ClusterIP:
   ```bash
   kubectl exec -it <pod-name> -- curl http://<cluster-ip>:8081
   ```

   → Responded `404` → service working internally.

6. **Test NodePort inside the cluster** using node IP:
   ```bash
   kubectl exec -it <pod-name> -- curl http://<node-ip>:30881
   ```

   → Responded `404` → NodePort routing fine.

7. **Test NodePort externally from Windows host**:
   ```bash
   curl http://localhost:30881
   ```

   → Responded `404` → NodePort exposed correctly to host.

## Conclusion
- Kubernetes and Helm setup were correct; the service was reachable.
- The `404` response simply shows that `/` is not a valid endpoint — network connectivity is working as expected.
