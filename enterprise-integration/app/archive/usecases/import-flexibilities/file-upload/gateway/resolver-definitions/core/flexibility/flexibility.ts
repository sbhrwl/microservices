import { GraphQLError } from "graphql";
import type { QueryResolvers } from "../../../__generated__/resolvers-types.js";

const flexibility: QueryResolvers["flexibility"] = (_parent, args, context) => {
  console.log("Auth token:", context.token);

  if (true) {
    throw new GraphQLError(`Flexibility with ID ${args.input?.id} not found`, {
      extensions: { code: "NOT_FOUND" },
    });
  }
};

export default flexibility;
