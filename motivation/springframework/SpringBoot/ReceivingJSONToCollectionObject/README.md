# Receiving JSON data and sending response as a Collection object
- This project demonstrates how to receive JSON data via a Spring Boot REST API and map it to `Java collection objects`.

## Features
- RESTful API endpoint to accept JSON data from the client.
- Demonstrates the use of Java collections (List, Set, Map) and nested objects in JSON requests.
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
  - URL: `http://localhost:9999/JsonToJavaObject/customer/register`
  - Set request body (JSON):
    ```json
    {
      "cid": 7,
      "cname": "dhoni",
      "compDetails": [
        {"name": "iNeuron", "location": "BGLR", "size": 250},
        {"name": "pwskills", "location": "Noida", "size": 350},
        {"name": "IGATE", "location": "HYD", "size": 300}
      ],
      "dob": "1991-01-03",
      "purchaseDate": "2022-06-05 19:01:23",
      "familyDetails": [
        {"adharNo": 123456, "pan": 123456},
        {"adharNo": 234234, "pan": 4443335}
      ]
    }

  - response as `customer collection object`
    ```json
    Customer [
      cid=7, cname=dhoni,
      compDetails=[
        Company [name=iNeuron, location=BGLR, size=250],
        Company [name=pwskills, location=Noida, size=350],
        Company [name=IGATE, location=HYD, size=300]
      ],
      dob=1991-01-03,
      purchaseDate=2022-06-05T19:01:23,
      familyDetails=[
        {adharNo=123456, pan=123456},
        {adharNo=234234, pan=4443335}
      ]
    ]
    ```
- **Powershell** request
```powershell
Invoke-RestMethod -Uri "http://localhost:9999/JsonToJavaObject/customer/register" -Method POST -Body (@{cid=7;cname="dhoni";compDetails=@(@{name="iNeuron";location="BGLR";size=250},@{name="pwskills";location="Noida";size=350},@{name="IGATE";location="HYD";size=300});dob="1991-01-03";purchaseDate="2022-06-05 19:01:23";familyDetails=@(@{adharNo=123456;pan=123456},@{adharNo=234234;pan=4443335})} | ConvertTo-Json -Depth 10) -ContentType "application/json"
```