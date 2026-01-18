const { ApolloServer } = require("apollo-server");
const { GraphQLUpload } = require("graphql-upload");
const client = require("./grpcClient");
const typeDefs = require("./schema");

const resolvers = {
  Upload: GraphQLUpload,

  Query: {
    items: () => {
      return new Promise((resolve, reject) => {
        client.getItems({}, (err, response) => {
          if (err) {
            console.error("gRPC error fetching items:", err);
            return reject(err);
          }
          console.log("Items fetched successfully:", response.items.length);
          resolve(response.items);
        });
      });
    },
  },

  Mutation: {
    uploadCsv: async (_, { file }) => {
      try {
        const { createReadStream } = await file;
        const stream = createReadStream();

        const chunks = [];
        for await (const chunk of stream) {
          chunks.push(chunk);
        }
        const buffer = Buffer.concat(chunks);

        console.log(`CSV file received, size: ${buffer.length} bytes`);

        return new Promise((resolve, reject) => {
          client.uploadCsv({ file: buffer }, (err, response) => {
            if (err) {
              console.error("gRPC error uploading CSV:", err);
              return reject(err);
            }
            console.log("CSV uploaded successfully:", response.success);
            resolve(response.success);
          });
        });
      } catch (error) {
        console.error("Error processing CSV upload:", error);
        throw error;
      }
    },
  },
};

const server = new ApolloServer({
  typeDefs,
  resolvers,
  csrfPrevention: true,
  cache: "bounded",
  context: ({ req }) => ({ req }),
  formatError: (error) => {
    console.error("GraphQL Error:", error);
    return error;
  },
});

server.listen({ port: 4000 }).then(({ url }) => {
  console.log(`🚀 GraphQL Gateway running at ${url}`);
  console.log(`📡 Connected to gRPC service at localhost:9090`);
});