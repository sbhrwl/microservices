# UI service for registring sensors
- [Properties](#properties)
- [How to run](#how-to-run)
- [Test](#test)
## Properties
- [application.properties](src/main/resources/application.properties)
## How to run
- Create spring boot app with [spring initialiser](https://start.spring.io/)
- Build 
```bash
mvn clean install

mvn clean install -DskipTests
```
- Run
```bash
mvn spring-boot:run
```

## Test
- `localhost:9081/`
  - username: `endpointaccessuser`
  - password: `password123`