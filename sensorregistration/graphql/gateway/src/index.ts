import { ApolloServer } from 'apollo-server';
import { readFileSync } from 'fs';
import { join } from 'path';
import { resolvers } from './resolvers/sensorResolver';
import { grpcClient } from './grpc/client';

// Load GraphQL schema
const typeDefs = readFileSync(
    join(__dirname, '../src/schema.graphql'),
    'utf-8'
);

const PORT = process.env.PORT || 4000;

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

server.listen(PORT).then(({ url }) => {
  console.log(`🚀 GraphQL Gateway ready at ${url}`);
});