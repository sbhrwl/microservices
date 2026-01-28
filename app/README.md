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
      - Verify gRPC from `Kreya`
        - Refresh protos from the menu button
          - Check `Revision info`: `GetRevisionInfo`
          - Query Flexibilities: `QueryFlexibilities`
          - Upload Flexibilities: `UploadFlexibilities`
          - Confirm Upload Flexibilities: `ConfirmUploadFlexibilities`
    - Dapr startup:
      - As `api-gateway` will make gRPC calls to `gfc-core`, so we need to start dapr
      - Start Dapr: `dapr init`
      - Run Dapr side car for `gfc-core` on port `50012`
        ```
        dapr run --enable-profiling --app-id gfc-core --app-port 9090 --app-protocol grpc --dapr-grpc-port 50012 --scheduler-host-address  `"`"
        ```
      - Run Dapr client: `C:\Git\gfc-app\gfc-core\src\test\java\com\landisgyr\gfc\api\v1\SampleDaprGrpcClient.java`
        - Verify Dapr port: `50012`
        - Run it from file editor (right click)
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
- `dapr list`
```bash
C:\Users\SabharwalR>dapr list
APP ID       HTTP PORT  GRPC PORT  APP PORT  COMMAND               AGE  CREATED              DAPRD PID  CLI PID  APP PID
api-gateway  3500       50001      0         tsx watch src/ind...  1h   2026-01-28 09:55.38  11396      9296     21772
gfc-core     53028      50012      9090                            1h   2026-01-28 10:27.43  17284      30728    0
```
- routes: `C:\Git\gfc-app\api-gateway\src\routes\flexibilities-import.route.ts`
  - Test on windows prompt: `curl -X POST http://localhost:4000/api/flexibilities/import -F file=@Flexibilities-L540.csv`
    ```bash
    C:\Git\gfc-app\api-gateway\test>curl -X POST http://localhost:4000/api/flexibilities/import -F file=@Flexibilities-L540.csv
    {"uploadId":"a71f84ae-77be-42fa-b98d-bc632e1515c2","csvSummary":{"fileMetadata":{"filename":"Flexibilities-L540.csv","fileSizeBytes":"13157","uploadedAt":"2026-01-27T12:01:50.244Z"},"totalRows":100,"invalidRows":12,"flexibilityTypeCounts":[{"flexibilityType":"Boiler","count":45},{"flexibilityType":"Heat pump","count":32},{"flexibilityType":"Lighting","count":28},{"flexibilityType":"PV","count":18}],"errorDetails":{"errors":[{"rowNumber":5,"columnName":"FlexibilityId","errorMessage":"Duplicate FlexibilityId found: FLEX-001"},{"rowNumber":12,"columnName":"Tenant","errorMessage":"Invalid tenant format: expected alphanumeric"},{"rowNumber":18,"columnName":"FlexibilityId","errorMessage":"Duplicate FlexibilityId found: FLEX-042"},{"rowNumber":23,"columnName":"FlexibilityType","errorMessage":"Unknown flexibility type: InvalidType"},{"rowNumber":31,"columnName":"Name","errorMessage":"Missing required field: Name"},{"rowNumber":45,"columnName":"FlexibilityId","errorMessage":"Duplicate FlexibilityId found: FLEX-078"},{"rowNumber":67,"columnName":"Capacity","errorMessage":"Invalid format: expected numeric value"},{"rowNumber":89,"columnName":"Location","errorMessage":"Missing required field: Location"}]}}}
    C:\Git\gfc-app\api-gateway\test>
     ```
- flexibility.proto: `C:\Git\gfc-app\gfc-apis\proto\core\api\flexibility\v1\flexibility.proto`
- flexibility.graphql: `C:\Git\gfc-app\api-gateway\graphql\core\api\v1\flexibility.graphql`
- operations.graphql: `C:\Git\gfc-app\api-gateway\graphql\operations.graphql`
