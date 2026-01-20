import * as grpc from '@grpc/grpc-js';
import * as protoLoader from '@grpc/proto-loader';
import { join } from 'path';

const GRPC_SERVER = process.env.GRPC_SERVER || 'localhost:9090';
const PROTO_PATH = join(__dirname, 'proto/sensor.proto');

class GrpcClient {
  private client: any;

  constructor() {
    const packageDefinition = protoLoader.loadSync(PROTO_PATH, {
      keepCase: true,
      longs: String,
      enums: String,
      defaults: true,
      oneofs: true
    });

    const protoDescriptor = grpc.loadPackageDefinition(packageDefinition) as any;
    const SensorService = protoDescriptor.sensor.SensorService;

    this.client = new SensorService(
      GRPC_SERVER,
      grpc.credentials.createInsecure()
    );
  }

  getClient(): any {
    return this.client;
  }

  shutdown(): void {
    this.client.close();
  }
}

export const grpcClient = new GrpcClient();