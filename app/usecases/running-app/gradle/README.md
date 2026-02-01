# Local setup and execution guide
- [GFC core](#gfc-core)
- [DAPR setup](#dapr-setup)
- [API gateway](#api-gateway)
- [References](#references)
## GFC core
- Go to `gfc-core`
- Build
  - `mvn install` or `mvn clean install`
- Run using IntelliJ IDEA play button
  - Build and run settings
```bash
-Dconfig.file=gfc-core/src/main/dist/etc/application.conf -Dlogback.configurationFile=gfc-core/src/main/dist/etc/logback.xml -Dlog.appender=STDOUT --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/jdk.internal.misc=ALL-UNNAMED
```
  - Go to `Modify options` and Add this as "Add VM options"
  <img src="images/google-java-formatter.png">

* Working directory: `C:/Git/gfc-app`
* Environment variables: `C:/Git/gfc-app/gfc-core/envfile.env`
* Verify gRPC using Kreya
  * Refresh protos from menu
  * Check `GetRevisionInfo`
  * Query `QueryFlexibilities`
  * Upload `UploadFlexibilities`
  * Confirm `ConfirmUploadFlexibilities`
## DAPR setup
* Dapr is required since `api-gateway` makes gRPC calls to `gfc-core`
* Initialize Dapr
  * `dapr init`
* Run Dapr sidecar for `gfc-core`
```bash
dapr run --enable-profiling --app-id gfc-core --app-port 9090 --app-protocol grpc --dapr-grpc-port 50012 --scheduler-host-address ""
```

* Run Dapr client
  * File: `gfc-core/src/test/java/com/landisgyr/gfc/api/v1/SampleDaprGrpcClient.java`
  * Verify Dapr gRPC port: `50012`
  * Run from file editor (right click)
* Verify running apps
  * `dapr list`
```bash
APP ID       HTTP PORT  GRPC PORT  APP PORT  COMMAND               AGE
api-gateway  3500       50001      0         tsx watch src/...     1h
gfc-core     53028      50012      9090                            1h
```

## API gateway
* Go to `api-gateway`
* Build
  * `npm install`
* Compile protos
  * `npm run proto:compile`
* Generate TypeScript
  * `npm run codegen`
* Run
  * `npm run start:dev`
* Open GraphQL playground
  * `http://127.0.0.1:4000/api/graphql`
* Sample query
```graphql
query ExampleQuery($input: FlexibilitiesInput) {
  flexibilities(input: $input) {
    items {
      flexibilityType
      name
      id
    }
  }
}
```
* Sample mutation
```graphql
mutation Mutation($input: ConfirmUploadFlexibilitiesInput!) {
  confirmUploadFlexibilities(input: $input) {
    importSummary {
      totalRows
      importedRows
      failedRows
      errorDetails {
        errors {
          rowNumber
          errorMessage
          columnName
        }
      }
    }
  }
}
```
## References
* Routes
  * `api-gateway/src/routes/flexibilities-import.route.ts`
* Test CSV upload
  * `curl -X POST http://localhost:4000/api/flexibilities/import -F file=@Flexibilities-L540.csv`
  * Proto: `gfc-apis/proto/core/api/flexibility/v1/flexibility.proto`
  * GraphQL schema: `api-gateway/graphql/core/api/v1/flexibility.graphql`
  * Operations: `api-gateway/graphql/operations.graphql`
