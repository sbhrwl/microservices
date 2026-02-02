import { GraphQLError } from "graphql";
import { ConfirmUploadFlexibilitiesRequest } from "../../../__generated__/core/api/flexibility/v1/flexibility.js";
import type {
  ConfirmFlexibilityUploadResult,
  MutationResolvers,
} from "../../../__generated__/resolvers-types.js";
import { FlexibilityClient } from "../../../clients/flexibility-client.js";

const confirmUploadFlexibilities: MutationResolvers["confirmUploadFlexibilities"] =
  async (_parent, args, context) => {
    console.log("Auth token:", context.token);
    console.log("Upload ID:", args.input.uploadId);

    try {
      const client = new FlexibilityClient();

      const request = ConfirmUploadFlexibilitiesRequest.create(args.input);

      // Call the gRPC service
      const response = await client.confirmUpload(request, context.token);

      return response as ConfirmFlexibilityUploadResult;
    } catch (error) {
      console.error("Error confirming upload flexibilities:", error);
      throw new GraphQLError("Failed to confirm upload flexibilities", {
        extensions: {
          code: "INTERNAL_SERVER_ERROR",
          originalError: error,
        },
      });
    }
  };

export default confirmUploadFlexibilities;
