# Pre-requisites
- [Installation](#installation)
- [Control plane components](#control-plane-components)
- [Pub Sub and State store components](#pub-sub-and-state-store-components)
- [Stop or remove Dapr system pods](#stop-or-remove-dapr-system-pods)
- [DAPR behaviour with queues](#dapr-behaviour-with-queues)
  - [Pub sub component config](#pub-sub-component-config)
## Installation
* Version
```
C:\Git\microservices\hubToSensor\hpa\orchestrate-hubtosensor-services>dapr --version
CLI version: 1.12.0
Runtime version: 1.12.2
```
## Control plane components
* Setup **Control plane components**
 * `dapr-operator` manages sidecar injection and component configs.
 * `dapr-sentry` handles mTLS (can be disabled, but still part of setup).
 * `dapr-placement` is needed for actor placement, but gets installed anyway.
```
dapr init -k

C:\Git\microservices\hubToSensor\hpa\orchestrate-hubtosensor-services>dapr init -k
Making the jump to hyperspace...
Note: To install Dapr using Helm, see here: https://docs.dapr.io/getting-started/install-dapr-kubernetes/#install-with-helm-advanced
Container images will be pulled from Docker Hub
Deploying the Dapr control plane with latest version to your cluster...
Deploying the Dapr dashboard with latest version to your cluster...
Success! Dapr has been installed to namespace dapr-system. To verify, run `dapr status -k' in your terminal. To get started, go here: https://aka.ms/dapr-getting-started
```

* Verify
```
C:\Git\microservices\hubToSensor\hpa\orchestrate-hubtosensor-services>dapr status -k
  NAME                   NAMESPACE    HEALTHY  STATUS   REPLICAS  VERSION  AGE  CREATED
  dapr-dashboard         dapr-system  True     Running  1         0.15.0   2m   2025-10-27 18:17.14
  dapr-sentry            dapr-system  True     Running  1         1.16.1   2m   2025-10-27 18:17.10
  dapr-placement-server  dapr-system  True     Running  1         1.16.1   2m   2025-10-27 18:17.10
  dapr-operator          dapr-system  True     Running  1         1.16.1   2m   2025-10-27 18:17.10
  dapr-sidecar-injector  dapr-system  True     Running  1         1.16.1   2m   2025-10-27 18:17.10

C:\Git\microservices\hubToSensor\hpa\orchestrate-hubtosensor-services>kubectl get pods -n dapr-system
NAME                                    READY   STATUS    RESTARTS      AGE
dapr-dashboard-5cb455db6f-2g9jf         1/1     Running   0             2m29s
dapr-operator-c6458b8bf-zqmsl           1/1     Running   2 (86s ago)   2m33s
dapr-placement-server-0                 1/1     Running   0             2m33s
dapr-scheduler-server-0                 1/1     Running   0             2m33s
dapr-scheduler-server-1                 1/1     Running   0             2m33s
dapr-scheduler-server-2                 1/1     Running   0             2m33s
dapr-sentry-79d87f5fc7-xcvgg            1/1     Running   0             2m33s
dapr-sidecar-injector-f5b58c7c7-p6rgz   1/1     Running   0             2m33s
```
<img src="images/setup.jpg">

## Pub Sub and State store components
* Setup **Pub Sub and State store components**
- [rabbitmq-pubsub.yaml](config-files/rabbitmq-pubsub.yaml)
  - `Undo`: `kubectl delete -f rabbitmq-pubsub.yaml`
- [postgres-statestore.yaml](config-files/postgres-statestore.yaml)
  - `Undo`: `kubectl delete -f postgres-statestore.yaml`
```
C:\Git\microservices\hubToSensor\dapr\config-files>kubectl apply -f rabbitmq-pubsub.yaml
component.dapr.io/rabbitmq-pubsub created

C:\Git\microservices\hubToSensor\dapr\config-files>kubectl apply -f postgres-statestore.yaml
component.dapr.io/postgres-statestore created

C:\Git\microservices\hubToSensor\dapr\config-files>kubectl get components
NAME                  AGE
postgres-statestore   108s
rabbitmq-pubsub       2m18s
```

## Stop or remove Dapr system pods
* Unistall
  * This deletes all pods in `dapr-system` and the Dapr CRDs.
```bash
dapr uninstall -k
```
* **Start them again later**
  * Reinstalls the control plane.
```bash
dapr init -k
```
* **Check status anytime**
```bash
kubectl get pods -n dapr-system
```
* Safe to uninstall — it doesn’t affect your app YAMLs or Dapr components (`rabbitmq-pubsub`, `postgres-statestore`).
  * They’ll still exist and reconnect when you reinstall.
## DAPR behaviour with queues
- As our setup is asymmetric
  - Request queue (`flexibility-hub.request`) → the service publishes messages here to downstream systems. 
    - Our app is not a subscriber, so **Dapr won’t log any queue declaration**.
  - Response queue (`flexibility-hub.response`) → the service subscribes to this topic to receive replies. 
    - That’s why we shall see the queue **`flexibility-hub-simulator-flexibility-hub.response`** [`appName`.`queueName`]
### Pub sub component config
```yaml
apiVersion: dapr.io/v1alpha1
kind: Component
metadata:
  name: rabbitmq-pubsub
  namespace: default
spec:
  type: pubsub.rabbitmq
  version: v1
  metadata:
    # Standard RabbitMQ connection URI: amqp://user:password@host:port/vhost
    - name: connectionString
      value: "amqp://admin:admin@localhost:5672"

    # Ensure message persistence across restarts
    - name: durable
      value: "true"

    # Avoid deleting queues when unused
    - name: deletedWhenUnused
      value: "false"

    # Explicit message acknowledgements handled by Dapr
    - name: autoAck
      value: "false"

    # Ensure the Dapr pubsub creates non-temporary queues (required for most bridge-like flows)
    - name: requeueInFailure
      value: "true"

    # Optional: set prefetch count to tune performance for your workload
    - name: prefetchCount
      value: "10"

    # Optional: fine-tune reliability — disables transient queues for Dapr topics
    - name: exclusive
      value: "false"

scopes:
  - flexibility-bridge-service
```
### ✅ Explanation of improvements
* **`requeueInFailure: true`** — makes sure failed messages are requeued for retry rather than lost.
* **`prefetchCount: 10`** — ensures better throughput without overloading one consumer.
* **`exclusive: false`** — ensures multiple services can bind to the same exchange if needed.
* **`durable` + `deletedWhenUnused`** keep queues persistent across restarts.
* **`scopes`** restricts this component to only `flexibility-bridge-service` (good multicomponent hygiene).
