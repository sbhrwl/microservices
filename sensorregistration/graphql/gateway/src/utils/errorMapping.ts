import { ApolloError, UserInputError } from 'apollo-server';
import * as grpc from '@grpc/grpc-js';

export function mapGrpcError(error: any): Error {
  const code = error?.code;
  const message = error?.details || error?.message || 'Unknown error';

  switch (code) {
    case grpc.status.NOT_FOUND:
      return new ApolloError(message, 'NOT_FOUND');
    
    case grpc.status.ALREADY_EXISTS:
      return new UserInputError(message, {
        code: 'ALREADY_EXISTS'
      });
    
    case grpc.status.INVALID_ARGUMENT:
      return new UserInputError(message, {
        code: 'INVALID_ARGUMENT'
      });
    
    case grpc.status.FAILED_PRECONDITION:
      return new UserInputError(message, {
        code: 'FAILED_PRECONDITION'
      });
    
    case grpc.status.INTERNAL:
    default:
      return new ApolloError(
        'Internal server error',
        'INTERNAL_SERVER_ERROR'
      );
  }
}