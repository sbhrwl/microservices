# Receiving JSON data
- This project demonstrates how to receive JSON data via a Spring Boot REST API.

## Features
- RESTful API endpoint to generate a customer report.
- Demonstrates the use of Java collections (List, Set, Map) and nested objects in JSON responses.
- Lightweight and easy-to-understand implementation.
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
| GET     | `http://localhost:9999/customer/showReport`   |  `Invoke-RestMethod -Uri "http://localhost:9999/customer/showReport" -Method GET` | FROM GET-showReport Method             |

- Response
```json
{
    "cid": 10,
    "cname": "sachin",
    "billAmount": 2345.5,
    "favColours": [
        "blue",
        "black",
        "purple"
    ],
    "studies": [
        "10",
        "10+2",
        "B.E"
    ],
    "phoneNumber": [
        9998887776,
        5454545,
        23456728
    ],
    "idDetails": {
        "adhar": 234567,
        "pan": 23456
    },
    "address": {
        "country": "INDIA",
        "state": "Maharashtra",
        "city": "Bombay"
    }
}
```
