import { ApolloServer } from 'apollo-server';
import { readFileSync } from 'fs';
import { join } from 'path';
import { resolvers } from './resolvers/sensorResolver';
import { grpcClient } from './grpc/client';

const typeDefs = readFileSync(join(__dirname, '../src/schema.graphql'), 'utf-8');

const server = new ApolloServer({
  typeDefs,
  resolvers,
  context: () => ({
    grpcClient: grpcClient.getClient()
  }),
  formatError: (error) => {
    console.error('GraphQL Error:', error);
    return error;
  }
});

const PORT = process.env.PORT || 4000;

server.listen(PORT).then(({ url }) => {
  console.log(`🚀 GraphQL Gateway ready at ${url}`);
});

// Graceful shutdown
process.on('SIGTERM', async () => {
  console.log('SIGTERM signal received: closing HTTP server');
  await server.stop();
  grpcClient.shutdown();
  process.exit(0);
});

process.on('SIGINT', async () => {
  console.log('SIGINT signal received: closing HTTP server');
  await server.stop();
  grpcClient.shutdown();
  process.exit(0);
});