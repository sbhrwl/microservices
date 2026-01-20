import { mapGrpcError } from '../utils/errorMapping';
import {
  RegisterSensorRequest,
  GetSensorRequest,
  ListSensorsByUserRequest,
  UpdatePostcodeRequest,
  SensorResponse,
  ListSensorsResponse
} from '../grpc/generated/sensor';
import { grpcClient } from '../grpc/client';
import { ServiceError } from '@grpc/grpc-js';

// Helper to convert proto timestamp to ISO string
function toISOString(epochMillis: number | string): string {
  // Convert string to number if needed
  const timestamp = typeof epochMillis === 'string' ? parseInt(epochMillis, 10) : epochMillis;
  
  // Validate the timestamp
  if (isNaN(timestamp) || timestamp <= 0) {
    console.warn('Invalid timestamp:', epochMillis);
    return new Date().toISOString(); // Return current time as fallback
  }
  
  return new Date(timestamp).toISOString();
}

// Helper to convert SensorResponse to GraphQL format
function toGraphQLSensor(response: any) {
  console.log('Converting response to GraphQL:', response);
  
  return {
    sensorId: response.sensor_id || response.sensorId,
    userEmail: response.user_email || response.userEmail,
    postcode: response.postcode,
    status: response.status,
    registeredAt: toISOString(response.registered_at || response.registeredAt),
    lastUpdatedAt: toISOString(response.last_updated_at || response.lastUpdatedAt)
  };
}

export const resolvers = {
  Query: {
    sensor: async (_: any, { sensorId }: { sensorId: string }) => {
      console.log('=== GET SENSOR REQUEST ===');
      console.log('Received sensorId:', sensorId);
      console.log('Type:', typeof sensorId);
      
      return new Promise((resolve, reject) => {
        const request = { sensor_id: sensorId } as any;
        console.log('gRPC request object:', JSON.stringify(request));
        console.log('Request keys:', Object.keys(request));
        
        grpcClient.getClient().getSensor(request, (error: ServiceError | null, response?: SensorResponse) => {
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
      console.log('=== LIST SENSORS BY USER REQUEST ===');
      console.log('Received userEmail:', userEmail);
      console.log('Type:', typeof userEmail);
      
      return new Promise((resolve, reject) => {
        const request = { user_email: userEmail } as any;
        console.log('gRPC request object:', JSON.stringify(request));
        
        grpcClient.getClient().listSensorsByUser(request, (error: ServiceError | null, response?: ListSensorsResponse) => {
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
      console.log('=== REGISTER SENSOR REQUEST ===');
      console.log('Received sensorId:', sensorId);
      console.log('Received userEmail:', userEmail);
      console.log('Received postcode:', postcode);
      console.log('Types:', {
        sensorId: typeof sensorId,
        userEmail: typeof userEmail,
        postcode: typeof postcode
      });
      
      return new Promise((resolve, reject) => {
        const request = {
          sensor_id: sensorId,
          user_email: userEmail,
          postcode: postcode
        } as any;
        
        console.log('gRPC request object (snake_case):', JSON.stringify(request));
        console.log('Request keys:', Object.keys(request));
        
        grpcClient.getClient().registerSensor(request, (error: ServiceError | null, response?: SensorResponse) => {
          if (error) {
            console.error('gRPC Error:', error);
            reject(mapGrpcError(error));
          } else if (response) {
            console.log('Success! Response:', JSON.stringify(response));
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
      console.log('=== UPDATE SENSOR POSTCODE REQUEST ===');
      console.log('Received sensorId:', sensorId);
      console.log('Received newPostcode:', newPostcode);
      console.log('Types:', {
        sensorId: typeof sensorId,
        newPostcode: typeof newPostcode
      });
      
      return new Promise((resolve, reject) => {
        const request = {
          sensor_id: sensorId,
          new_postcode: newPostcode
        } as any;
        
        console.log('gRPC request object:', JSON.stringify(request));
        
        grpcClient.getClient().updateSensorPostcode(request, (error: ServiceError | null, response?: SensorResponse) => {
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