# Sending JSON data
- This project demonstrates how to send JSON data to a Spring Boot REST API.

## Features
- RESTful API endpoint to accept JSON data from the client.
- Demonstrates the use of Jackson API for JSON-to-POJO binding.
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
- **Postman**
  - URL: `http://localhost:9999/customer/register`
  - Set request body (JSON)
    ```json
    {
      "cid": 7,
      "cname": "dhoni",
      "billAmount": 4567.5,
      "caddr": "CSK"
    }
    ```

  - Response
    ```bash
    Customer [cid=7, cname=dhoni, billAmount=4567.5, caddr=CSK]
    ```
- **Powershell** script to send a POST request with JSON data
  - Set URL
    ```powershell
    $uri = "http://localhost:9999/customer/register"
    ```
  - Set request body
    ```powershell
    $body = @{ cid = 7; cname = "dhoni"; billAmount = 4567.5; caddr = "CSK" } | ConvertTo-Json -Depth 10

    $body = @{
        cid = 7
        cname = "dhoni"
        billAmount = 4567.5
        caddr = "CSK"
    } | ConvertTo-Json -Depth 10
    ```
  - Send command
    ```powershell
    $response = Invoke-RestMethod -Uri $uri -Method Post -Body $body -ContentType "application/json"
    ```
    - Response
    ```powershell
    $response
    ```
