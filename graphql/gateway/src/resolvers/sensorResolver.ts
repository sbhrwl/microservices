import { grpcClient } from '../grpc/client';
import { mapGrpcError } from '../utils/errorMapping';
import {
  RegisterSensorRequest,
  GetSensorRequest,
  ListSensorsByUserRequest,
  UpdatePostcodeRequest,
  SensorResponse
} from '../grpc/generated/sensor';

// Helper to convert proto timestamp to ISO string
function toISOString(epochMillis: number): string {
  return new Date(epochMillis).toISOString();
}

// Helper to convert SensorResponse to GraphQL format
function toGraphQLSensor(response: SensorResponse) {
  return {
    sensorId: response.sensorId,
    userEmail: response.userEmail,
    postcode: response.postcode,
    status: response.status,
    registeredAt: toISOString(response.registeredAt),
    lastUpdatedAt: toISOString(response.lastUpdatedAt)
  };
}

export const resolvers = {
  Query: {
    sensor: async (_: any, { sensorId }: { sensorId: string }) => {
      return new Promise((resolve, reject) => {
        const request: GetSensorRequest = { sensorId };
        
        grpcClient.getClient().getSensor(request, (error, response) => {
          if (error) {
            reject(mapGrpcError(error));
          } else if (response) {
            resolve(toGraphQLSensor(response));
          } else {
            reject(new Error('No response from gRPC service'));
          }
        });
      });
    },

    sensorsByUser: async (_: any, { userEmail }: { userEmail: string }) => {
      return new Promise((resolve, reject) => {
        const request: ListSensorsByUserRequest = { userEmail };
        
        grpcClient.getClient().listSensorsByUser(request, (error, response) => {
          if (error) {
            reject(mapGrpcError(error));
          } else if (response) {
            resolve(response.sensors.map(toGraphQLSensor));
          } else {
            reject(new Error('No response from gRPC service'));
          }
        });
      });
    }
  },

  Mutation: {
    registerSensor: async (
      _: any,
      { sensorId, userEmail, postcode }: {
        sensorId: string;
        userEmail: string;
        postcode: string;
      }
    ) => {
      return new Promise((resolve, reject) => {
        const request: RegisterSensorRequest = {
          sensorId,
          userEmail,
          postcode
        };
        
        grpcClient.getClient().registerSensor(request, (error, response) => {
          if (error) {
            reject(mapGrpcError(error));
          } else if (response) {
            resolve(toGraphQLSensor(response));
          } else {
            reject(new Error('No response from gRPC service'));
          }
        });
      });
    },

    updateSensorPostcode: async (
      _: any,
      { sensorId, newPostcode }: {
        sensorId: string;
        newPostcode: string;
      }
    ) => {
      return new Promise((resolve, reject) => {
        const request: UpdatePostcodeRequest = {
          sensorId,
          newPostcode
        };
        
        grpcClient.getClient().updateSensorPostcode(request, (error, response) => {
          if (error) {
            reject(mapGrpcError(error));
          } else if (response) {
            resolve(toGraphQLSensor(response));
          } else {
            reject(new Error('No response from gRPC service'));
          }
        });
      });
    }
  }
};