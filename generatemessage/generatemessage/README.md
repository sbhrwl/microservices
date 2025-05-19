# Introduction
- `RESTful API endpoint` to generate a greeting message based on time of the day.
## Properties
- [application.properties](src/main/resources/application.properties)
## How to run
- Build 
  ```bash
  mvn clean install
  ```
- Run
  ```bash
  mvn spring-boot:run
  ```
## Test
- API 

| Method  | URL (test from postman)                                     | PowerShell Command | Output                                 |
|:--------|:----------------------------------------|:-------------------|:--------------------|
| GET     | `http://localhost:9080/message/generate`   |  `Invoke-RestMethod -Uri "http://localhost:9080/message/generate" -Method Get` | FROM GET-generateMessage Method             |
