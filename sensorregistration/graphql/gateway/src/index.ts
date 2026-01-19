import { ApolloServer } from 'apollo-server';
import { readFileSync } from 'fs';
import { join } from 'path';
import { resolvers } from './resolvers/sensorResolver';
import { grpcClient } from './grpc/client';

const typeDefs = readFileSync(
  join(__dirname, 'schema.graphql'),
  'utf-8'
);

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
  console.log(`🚀 Gateway ready at ${url}`);
  console.log(`📡 Connected to gRPC backend at ${process.env.GRPC_SERVER || 'localhost:9090'}`);
});

// Graceful shutdown
process.on('SIGTERM', async () => {
  console.log('SIGTERM received, shutting down gracefully...');
  await grpcClient.shutdown();
  await server.stop();
  process.exit(0);
});