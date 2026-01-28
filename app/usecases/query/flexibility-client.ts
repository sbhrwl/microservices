import type { Metadata } from "@grpc/grpc-js";
import {
  type ConfirmUploadFlexibilitiesRequest,
  type ConfirmUploadFlexibilitiesResponse,
  FlexibilityServiceClient,
  type GetFlexibilityRequest,
  type GetFlexibilityResponse,
  type QueryFlexibilitiesRequest,
  type QueryFlexibilitiesResponse,
  type UploadCsvRequest,
  type UploadCsvResponse,
} from "../__generated__/core/api/flexibility/v1/flexibility.js";
import { createDaprMetadata, createDaprProxy } from "../common/dapr-client.js";

export class FlexibilityClient {
  private client: FlexibilityServiceClient | null = null;
  private initPromise: Promise<void> | null = null;
  private readonly appId: string;

  constructor() {
    this.appId = process.env.DAPR_FLEXIBILITY_APP_ID || "gfc-core";
  }

  /**
   * Initialize the gRPC client proxy through Dapr
   * This must be called before using any service methods
   */
  private async initialize(): Promise<void> {
    if (this.client) {
      return; // Already initialized
    }

    if (this.initPromise) {
      return this.initPromise; // Initialization in progress
    }

    this.initPromise = (async () => {
      try {
        console.log("Initializing Dapr proxy for FlexibilityServiceClient...");
        this.client = await createDaprProxy(FlexibilityServiceClient);
        console.log("FlexibilityServiceClient proxy created successfully");
      } catch (error) {
        console.error("Failed to create Dapr proxy:", error);
        this.initPromise = null; // Reset to allow retry
        throw error;
      }
    })();

    return this.initPromise;
  }

  /**
   * Get the initialized client proxy
   * Ensures the client is initialized before returning
   */
  private async getProxy(): Promise<FlexibilityServiceClient> {
    await this.initialize();

    if (!this.client) {
      throw new Error("FlexibilityServiceClient proxy not initialized");
    }

    return this.client;
  }

  /**
   * Create gRPC metadata with authorization and Dapr app-id
   */
  private createMetadata(token?: string): Metadata {
    return createDaprMetadata(this.appId, token);
  }

  /**
   * Query flexibilities with filtering, sorting, and pagination
   */
  async queryFlexibilities(
    request: QueryFlexibilitiesRequest,
    token?: string,
  ): Promise<QueryFlexibilitiesResponse> {
    const proxy = await this.getProxy();
    const metadata = this.createMetadata(token);

    return new Promise<QueryFlexibilitiesResponse>((resolve, reject) => {
      proxy.queryFlexibilities(request, metadata, (error, response) => {
        if (error) {
          console.error("queryFlexibilities error:", error);
          reject(error);
        } else {
          resolve(response);
        }
      });
    });
  }

  /**
   * Get a single flexibility by ID
   */
  async getFlexibility(
    request: GetFlexibilityRequest,
    token?: string,
  ): Promise<GetFlexibilityResponse> {
    const proxy = await this.getProxy();
    const metadata = this.createMetadata(token);

    return new Promise<GetFlexibilityResponse>((resolve, reject) => {
      proxy.getFlexibility(request, metadata, (error, response) => {
        if (error) {
          console.error("getFlexibility error:", error);
          reject(error);
        } else {
          resolve(response);
        }
      });
    });
  }

  /**
   * Upload CSV file for bulk flexibility import
   */
  async uploadCsv(
    request: UploadCsvRequest,
    token?: string,
  ): Promise<UploadCsvResponse> {
    const proxy = await this.getProxy();
    const metadata = this.createMetadata(token);

    return new Promise<UploadCsvResponse>((resolve, reject) => {
      proxy.uploadFlexibilities(request, metadata, (error, response) => {
        if (error) {
          console.error("uploadCsv error:", error);
          reject(error);
        } else {
          resolve(response);
        }
      });
    });
  }

  /**
   * Confirm the uploaded flexibilities
   */
  async confirmUpload(
    request: ConfirmUploadFlexibilitiesRequest,
    token?: string,
  ): Promise<ConfirmUploadFlexibilitiesResponse> {
    const proxy = await this.getProxy();
    const metadata = this.createMetadata(token);

    return new Promise<ConfirmUploadFlexibilitiesResponse>(
      (resolve, reject) => {
        proxy.confirmUploadFlexibilities(
          request,
          metadata,
          (error, response) => {
            if (error) {
              console.error("confirmUpload error:", error);
              reject(error);
            } else {
              resolve(response);
            }
          },
        );
      },
    );
  }

  /**
   * Close the client (resets initialization state)
   */
  close(): void {
    this.client = null;
    this.initPromise = null;
  }
}

// Singleton instance
let flexibilityClient: FlexibilityClient | null = null;

/**
 * Get or create the singleton FlexibilityClient instance
 */
export function getFlexibilityClient(): FlexibilityClient {
  if (!flexibilityClient) {
    flexibilityClient = new FlexibilityClient();
  }
  return flexibilityClient;
}

/**
 * Close and reset the singleton instance
 */
export function closeFlexibilityClient(): void {
  if (flexibilityClient) {
    flexibilityClient.close();
    flexibilityClient = null;
  }
}
