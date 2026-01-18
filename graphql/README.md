- [Gateway](gateway/README.md)
- [Backend](backend/README.md)

## Test
- Convert CSV to base64
```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("sample.csv"))
```
- Base64 result: `[Convert]::ToBase64String([IO.File]::ReadAllBytes("sample.csv"))`
  Perfect — MongoDB is now connecting correctly. ✅
  And the `find using query: {}` log shows Spring Boot **is querying MongoDB**.

So the only reason you still see an empty list is:

### **There is no data in MongoDB yet.**

That means:

* **Query works**
* **But upload didn’t happen yet**

---

# ✅ Now let’s upload CSV (the actual data step)

---

## 1) Create sample CSV (PowerShell)

Run:

```powershell
@"
name,age,city
Alice,25,Helsinki
Bob,30,Amsterdam
"@ > sample.csv
```

---

## 2) Convert CSV to base64 (PowerShell)

Run:

```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("sample.csv"))
```
- Upload CSV via GraphQL (Apollo Studio)
  - In the left editor:
    ```graphql
    mutation upload($csvBase64: String!) {
      uploadCsv(csvBase64: $csvBase64)
    }
    ```
  - In the variables panel:
    ```json
    {
      "csvBase64": "PASTE_BASE64_HERE"
    }
    ```
  - Click **Run ▶️**
- Query items again
```graphql
query {
  items {
    id
    name
    age
    city
  }
}
```
- if upload succeeds, you should see:
```json
{
  "data": {
    "items": [
      { "id": "...", "name": "Alice", "age": 25, "city": "Helsinki" },
      { "id": "...", "name": "Bob", "age": 30, "city": "Amsterdam" }
    ]
  }
}
```