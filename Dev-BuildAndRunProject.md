# Introduction
- This project is a basic Spring Boot REST API that generates a greeting message based on the time of day.
## Features
- RESTful API endpoint to generate a greeting message.
- Simple and lightweight implementation using Spring Boot.
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