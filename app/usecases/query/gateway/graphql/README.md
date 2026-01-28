# GraphQL

## GraphQL concepts
* **Type**: defines what data you can read.
* **Input**: defines what data you can send.
* **Field**: a single piece of data on a type (like `id` or `name`).
* **List**: written as `[Type]`, meaning multiple items.
* **Non-null (`!`)**: the value must always be present.
## Flow
* You can:
  * query flexibilities (optionally filtered and paginated),
  * create or update them using inputs,
  * upload many at once and confirm the upload,
  * receive a clear summary of successes and errors
## what this schema is about
  * It describes how a system stores and works with **flexibility resources** (for example boilers, lighting, or heat pumps).
  * GraphQL is used to define the *shape of the data* and how it can be sent or received.
## types 
* objects you can read
  * `Flexibility`
    * Represents one flexibility resource.
    * Fields:
      * `id`: a required unique ID.
      * `name`: a readable name.
      * `flexibilityType`: the category of the resource.
  * `Flexibilities`
    * Represents a **collection** of flexibility items.
    * Contains:
      * `items`: a list of `Flexibility` objects.
      * `meta`: extra information such as pagination details.
  * `ConfirmFlexibilityUploadResult`
    * Returned after confirming a bulk upload.
    * Shows the upload ID and a summary of the import.
  * `ImportSummary`
    * Explains what happened during an import:
      * how many rows were processed,
      * how many succeeded,
      * how many failed.
  * `ErrorDetails` and `RowError`
    * Describe errors in detail, including:
      * which row failed,
      * which column caused the problem,
      * why it failed.
## Input types 
* Objects you send
  * Inputs are used when **sending data to the API**, for example when creating, updating, or filtering data.
  * `FlexibilityInput`
    * Used to create or update a flexibility.
    * Requires an `id`.
  * `FlexibilitiesInput`
    * Used when requesting a list of flexibilities.
    * Allows filtering and pagination.
  * `FlexibilityQueryFilter`
    * Lets you filter flexibilities by type (for example only “Boiler”).
  * `ConfirmUploadFlexibilitiesInput`
    * Used to confirm a previously uploaded file using its `uploadId`.