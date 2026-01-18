const { gql } = require("apollo-server");

module.exports = gql`
  scalar Upload

  type Item {
    id: ID!
    name: String!
    age: Int!
    city: String!
  }

  type Query {
    items: [Item!]!
  }

  type Mutation {
    uploadCsv(file: Upload!): Boolean!
  }
`;