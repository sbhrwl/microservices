# Scalability and cloud resilience
- [Horizontal scaling](#horizontal-scaling)
- [Load balancing](#load-balancing)
- [Fault tolerance](#fault-tolerance)
- [Deployment strategies](#deployment-strategies)
- [Cost optimization](#cost-optimization)
## Horizontal scaling
- Services scale independently based on CPU or message queue depth.  
- Autoscaling ensures consistent performance under heavy load.  
- *Speaker note:* Overloaded services can spin up additional instances while the rest of the system continues smoothly.
## Load balancing
- Internal load balancing for microservices.  
- External load balancing for UI and API endpoints.  
- Ensures even distribution of incoming requests.  
- *Speaker note:* Balancers prevent hotspots and avoid single-pod bottlenecks.
## Fault tolerance
- Kubernetes automatically restarts failed pods.  
- Dead-letter queues and retries handle message loss.  
- Circuit breakers isolate failing services.  
- *Speaker note:* System recovers automatically without human intervention.
## Deployment strategies
- Rolling updates for zero-downtime upgrades.  
- Blue-green or canary deployments for safe feature rollouts.  
- *Speaker note:* Updates are deployed safely without affecting users, essential for production systems.
## Cost optimization
- Idle services scale down to minimize cloud costs.  
- Dynamic scaling ensures optimal resource usage.  
- *Speaker note:* Scalability balances performance with efficient use of resources.
