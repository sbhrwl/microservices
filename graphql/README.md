- [Gateway](gateway/README.md)
- [Backend](backend/README.md)

## Test
- Convert CSV to base64
```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("sample.csv"))
```
- Base64 result: `bmFtZSxhZ2UsY2l0eQ0KQWxpY2UsMjUsSGVsc2lua2kNCkJvYiwzMCxBbXN0ZXJkYW0=`
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