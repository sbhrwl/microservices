# Microservices in action
* Each service = single responsibility.
* Decoupling via Message Broker (asynchronous communication).
* Database updates via Storage Service.
* Independent scaling (e.g., multiple Protocol Adapter pods).
* Observability: each service logs independently.
* Resilience: retry, dead-letter queues, and fallback patterns.
