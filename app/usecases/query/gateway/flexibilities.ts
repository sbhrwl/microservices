import { GraphQLError } from "graphql";
import { QueryFlexibilitiesRequest } from "../../../__generated__/core/api/flexibility/v1/flexibility.js";
import type { QueryResolvers } from "../../../__generated__/resolvers-types.js";
import { FlexibilityClient } from "../../../clients/flexibility-client.js";

const flexibilities: QueryResolvers["flexibilities"] = async (
  _parent,
  args,
  context,
) => {
  console.log("Auth token:", context.token);

  try {
    const client = new FlexibilityClient();

    // Build the gRPC request using protobuf create method
    const request = QueryFlexibilitiesRequest.create({
      filter: args.input?.filter
        ? {
            flexibilityIdIn: [],
            flexibilityNameIn: [],
            flexibilityTypeIn: [],
          }
        : undefined,
      pagination: args.input?.pagination
        ? {
            pageSize: args.input.pagination.pageSize ?? undefined,
            pageNumber: args.input.pagination.pageNumber ?? undefined,
          }
        : undefined,
    });

    // Call the gRPC service
    const response = await client.queryFlexibilities(request, context.token);

    console.log("gRPC response:", response);

    // Map gRPC response to GraphQL Flexibilities type
    return {
      items:
        response.flexibilities?.items?.map((item) => ({
          id: item.id ?? "",
          name: item.name ?? null,
          flexibilityType: item.flexibilityType ?? null,
        })) ?? [],
      meta: {
        totalCount: response.flexibilities?.meta?.totalCount
          ? Number(response.flexibilities.meta.totalCount)
          : 0,
      },
    };
  } catch (error) {
    console.error("Error fetching flexibilities:", error);
    throw new GraphQLError("Failed to fetch flexibilities", {
      extensions: {
        code: "INTERNAL_SERVER_ERROR",
        originalError: error,
      },
    });
  }
};

export default flexibilities;
