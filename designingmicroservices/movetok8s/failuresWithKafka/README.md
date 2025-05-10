# Failures with Kafka
## Problem
- `host.docker.internal` **does not work inside Kubernetes pods** (even on Docker Desktop).
- While it works with containers in **Docker Compose**, Kubernetes **pods are isolated** from Docker’s internal network and **cannot resolve `host.docker.internal`**.
  - That’s why your Spring Boot app in the Kubernetes pod cannot connect to Kafka running on your host.

## Fix
- Expose Kafka to your Kubernetes cluster
### Option 1
- Run Kafka in Kubernetes
- If you move Kafka to Kubernetes using something like [Bitnami Kafka Helm chart](https://bitnami.com/stack/kafka/helm), then pods will discover and connect via Kubernetes services like `kafka:9092`.
### Option 2
- Keep Kafka in Docker and expose it to Kubernetes
  - You can **expose Kafka running in Docker** to your Kubernetes pods like this:
#### Steps
- **Expose Kafka on your host's IP (Docker Compose)**
  - Update your Kafka Docker Compose config (or Docker run) to advertise your **host IP**:
    ```yaml
    # docker-compose.yml (Kafka service section)
    environment:
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://<YOUR_HOST_IP>:29092
      KAFKA_LISTENERS: PLAINTEXT://0.0.0.0:29092
    ```
  - Replace `<YOUR_HOST_IP>` with your local IP address (run `ipconfig` and look under "Ethernet adapter vEthernet (WSL)" or your main interface).
    - Example: `192.168.1.100`

- **Update Kubernetes deployment**
  - Update your pod’s `KAFKA_BOOTSTRAP_SERVERS` value with the **host IP**:
    ```yaml
    - name: KAFKA_BOOTSTRAP_SERVERS
      value: 192.168.1.100:29092
    ```
- **Restart pods**
  ```bash
  kubectl rollout restart deployment command-orchestration-deployment
  ```
- **Test** from inside a pod
  - You can verify network access from inside your pod.
    - You won’t get an HTTP response (Kafka uses binary protocol), but **no error = port reachable**.
    ```bash
    kubectl exec -it <pod-name> -- /bin/sh
    # inside pod
    apk add curl
    curl 192.168.1.100:29092
    ```
