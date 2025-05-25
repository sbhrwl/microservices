# REST endpoints
- This project demonstrates advanced REST API features using Spring Boot.
## How to Run
- Build 
```bash
mvn clean install

mvn clean install -DskipTests
```
- Run
```bash
mvn spring-boot:run
```
- APIs

| Method  | URL (test from postman)                                     | PowerShell Command | Output                                 |
|:--------|:----------------------------------------|:-------------------|:--------------------|
| GET     | `http://localhost:9999/customer/report`   |  `Invoke-RestMethod -Uri "http://localhost:9999/customer/report" -Method Get` | FROM GET-ShowReport Method             |
| POST    | `http://localhost:9999/customer/register` | `Invoke-RestMethod -Uri "http://localhost:9999/customer/register" -Method Post` |FROM POST-RegisterCustomer Method      |
| PUT     | `http://localhost:9999/customer/modify`   | `Invoke-RestMethod -Uri "http://localhost:9999/customer/modify" -Method Put` |FROM PUT-UpdateCustomer Method         |
| DELETE  | `http://localhost:9999/customer/delete`   | `Invoke-RestMethod -Uri "http://localhost:9999/customer/delete" -Method Delete` |FROM DELETE-deleteCustomer Method      |
| GET  | `http://localhost:9999/customer/report/query?cid=10&cname=sachin`  |  |`Query params`      |
| GET  | `http://localhost:9999/customer/report/18/kohli`   |  |`Path variables`      |
