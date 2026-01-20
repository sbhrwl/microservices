import { mapGrpcError } from '../utils/errorMapping';

export const resolvers = {
  Query: {
    sensor: async (_: any, { sensorId }: { sensorId: string }, { grpcClient }: any) => {
      return new Promise((resolve, reject) => {
        grpcClient.getSensor({ sensorId }, (error: any, response: any) => {
          if (error) {
            reject(mapGrpcError(error));
          } else {
            resolve(response);
          }
        });
      });
    },

    sensorsByUser: async (_: any, { userEmail }: { userEmail: string }, { grpcClient }: any) => {
      return new Promise((resolve, reject) => {
        grpcClient.getSensorsByUser({ userEmail }, (error: any, response: any) => {
          if (error) {
            reject(mapGrpcError(error));
          } else {
            resolve(response.sensors || []);
          }
        });
      });
    },
  },

  Mutation: {
    registerSensor: async (
        _: any,
        { sensorId, userEmail, postcode }: { sensorId: string; userEmail: string; postcode: string },
        { grpcClient }: any
    ) => {
      return new Promise((resolve, reject) => {
        grpcClient.registerSensor(
            {
              sensorId: sensorId,
              userEmail: userEmail,
              postcode: postcode
            },
            (error: any, response: any) => {
              if (error) {
                console.error('gRPC Error:', error);
                reject(mapGrpcError(error));
              } else {
                resolve(response);
              }
            }
        );
      });
    },

    updateSensorPostcode: async (
        _: any,
        { sensorId, newPostcode }: { sensorId: string; newPostcode: string },
        { grpcClient }: any
    ) => {
      return new Promise((resolve, reject) => {
        grpcClient.updateSensorPostcode(
            {
              sensorId: sensorId,
              newPostcode: newPostcode
            },
            (error: any, response: any) => {
              if (error) {
                reject(mapGrpcError(error));
              } else {
                resolve(response);
              }
            }
        );
      });
    },
  },
};