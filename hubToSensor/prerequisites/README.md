# Pre-requisites
- [RabbitMq setup](#rabbitmq-setup)
  - [Create queues](#create-queues)
## RabbitMq setup
- Create a [`docker-compose.yml`](rabbitmq/docker-compose.yml)
- Run docker compose: `docker-compose up -d`
- Verify: `docker-compose logs rabbitmq`
- Then go to `http://localhost:15672`
  - user: admin
  - password: admin
### Create queues
- Go to `Queues and Streams`
  - `flexhub.request` and `flexhub.response`
  - `connector.request` and `connector.response`
  - `hes.request` and `hes.response`
