# DevOps
* GitLab/Jenkins CI pipeline demo flow:
  * Code push triggers build/test.
  * Docker image built and pushed to registry.
  * Helm deploys to staging → production via approval gates.
* Branching strategy: `feature/*`, `develop`, `main`.
* Automated testing: unit + integration + performance.
* Logging & metrics aggregation (ELK, Prometheus, Grafana).
* Continuous feedback loops: quality gates, alerts, dashboards.
