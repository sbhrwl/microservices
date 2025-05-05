# Introduction
- `RESTful API endpoint` to generate a greeting message based on time of the day.
## How to run
- Build 
```bash
mvn clean install
```
- Run
```bash
mvn spring-boot:run
```
- API 

| Method  | URL (test from postman)                                     | PowerShell Command | Output                                 |
|:--------|:----------------------------------------|:-------------------|:--------------------|
| GET     | `http://localhost:9999/message/generate`   |  `Invoke-RestMethod -Uri "http://localhost:9999/message/generate" -Method Get` | FROM GET-generateMessage Method             |