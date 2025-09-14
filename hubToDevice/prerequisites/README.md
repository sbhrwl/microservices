# Pre-requisites
- [RabbitMq setup](#rabbitmq-setup)
## RabbitMq setup
- Create a [`docker-compose.yml`](rabbitmq/docker-compose.yml)
- Run docker compose: `docker-compose up -d`
- Verify: `docker-compose logs rabbitmq`
- Then go to `http://localhost:15672`
  - user: admin
  - pass: admin
  - Verify Queues
