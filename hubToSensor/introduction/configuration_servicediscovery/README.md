# Configuration and service discovery
- [Environment-specific configuration](#environment-specific-configuration)
- [Service discovery](#service-discovery)
- [Optional service mesh](#optional-service-mesh)
## Environment-specific configuration
- Configurations separated from code via Helm values or ConfigMaps.  
- Supports different setups for dev, staging, and production.  
- No hardcoded secrets or endpoints in the code.  
- *Speaker note:* This ensures safe and predictable transitions between environments.
## Service discovery
- Microservices locate each other using Kubernetes DNS or internal service registry.  
- No IP addresses are hardcoded.  
- New pods automatically register and are reachable.  
- *Speaker note:* Service discovery acts as a dynamic phonebook — any service can find another at runtime.
## Optional service mesh
- Handles routing, retries, load balancing, and security transparently.  
- Provides observability and telemetry without modifying service code.  
- *Speaker note:* While optional, service mesh adds resilience and operational insight in large-scale systems.
