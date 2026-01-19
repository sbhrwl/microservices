import * as grpc from '@grpc/grpc-js';
import { SensorServiceClient } from './generated/sensor';

const GRPC_SERVER = process.env.GRPC_SERVER || 'localhost:9090';

class GrpcClient {
  private client: SensorServiceClient;

  constructor() {
    this.client = new SensorServiceClient(
      GRPC_SERVER,
      grpc.credentials.createInsecure()
    );
  }

  getClient(): SensorServiceClient {
    return this.client;
  }

  async shutdown(): Promise<void> {
    return new Promise((resolve) => {
      this.client.close();
      resolve();
    });
  }
}

export const grpcClient = new GrpcClient();