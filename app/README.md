# Application
- [Platform engineering](platform-engineering/README.md)
- [bff](api-gateway/README.md)
- [gfc-apis](gfc-apis/README.md)
- [gfc-core](gfc-service/README.md)
- [command-orchestrator](command-orchestrator/README.md)
- [protocol-adapter](protocol-adapter/README.md)
- [Tooling](tooling/README.md)
- Use cases
  - [Switching flexibilities](usecases/switching/README.md)
  - [Organization API](usecases/organization/README.md)
  - [File upload](usecases/fileupload/README.md)
    - Go to `gfc-core`
      - Build: `mvn install` or `mvn clean install`
      - Run with Intellijidea `play` button
        - Build and run settings:
        ```bash
        -Dconfig.file=gfc-core/src/main/dist/etc/application.conf -Dlogback.configurationFile=gfc-core/src/main/dist/etc/logback.xml -Dlog.appender=STDOUT --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/jdk.internal.misc=ALL-UNNAMED `
        ```
        - Working directory: `C:\Git\gfc-app`
        - Environment variables (For intellijidea): `C:/Git/gfc-app/gfc-core/envfile.env`
      - Run Dapr side car for `gfc-core` on port `50012`
        ```
        dapr run --enable-profiling --app-id gfc-service --app-port 9090 --app-protocol grpc --dapr-grpc-port 50012 -- mvn exec:java -D"config.file"=src/main/dist/etc/application.conf -D"logback.configurationFile"=src/main/dist/etc/logback.xml -D"log.appender"=STDOUT
        ```
      - Run Dapr client: `C:\Git\gfc-app\gfc-core\src\test\java\com\landisgyr\gfc\api\v1\SampleDaprGrpcClient.java`
        - Run it from file editor (right click)
      - Verify gRPC from `Kreya`
        - Refresh protos from the menu button
          - Check `Revision info`: `GetRevisionInfo`
          - Query Flexibilities: `QueryFlexibilities`
          - Upload Flexibilities: `UploadFlexibilities`
          - Confirm Upload Flexibilities: `ConfirmUploadFlexibilities`
    - Go to `api-gateway`
      - Build: `npm install`
      - Run: `npm run start:dev`
      - Compiling protos: `npm run proto:compile`
      - Generate typescript from protos: `npm run codegen`
      - Open `playground`: `http://127.0.0.1:4000/api/graphql`
        - Query
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
        - Mutation
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
