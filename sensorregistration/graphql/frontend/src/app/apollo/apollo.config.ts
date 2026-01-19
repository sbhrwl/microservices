import { ApolloClientOptions, InMemoryCache } from '@apollo/client/core';
import { HttpLink } from 'apollo-angular/http';

export function createApollo(httpLink: HttpLink): ApolloClientOptions<any> {
  return {
    link: httpLink.create({
      uri: 'http://localhost:4000/graphql', // GraphQL gateway URL
    }),
    cache: new InMemoryCache(),
  };
}
