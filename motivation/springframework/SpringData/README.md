# Spring Data
## JPA
- `Jakarta` Persistence API (formerly Java Persistence API)
- Jakarta Persistence API is a **collection of classes and methods** to persistently `store the vast amounts of data into a database` (relational and nosql)
- JPA helps you avoid writing boilerplate SQL like you would with plain JDBC.
- Spring Boot uses **JPA with Hibernate** under the hood. 
- **Concepts**
  - `@Entity`, `@Table`
  - `@Id`, `@GeneratedValue`
  - `@OneToMany`, `@ManyToOne` (for relationships)
  - `CrudRepository`, `JpaRepository`
  - `Paging` and `Sorting` repository
- [Example](JPA-EmployeeRelationshipManagement/README.md)
- [Mongo: Console](MongoDB-Console-CustomerRelationshipManagement/README.md)
- [Mongo: RestController](MongoDB-RestController-CustomerRelationshipManagement/README.md)
## Data Transfer Object pattern
- The DTO pattern introduces a layer of abstraction, improving the design of your application by avoiding exposing internal database structure in your APIs
- Transform between `Entity` (DB model) and `DTO` (API model)
- Improve security, clarity, and control
- You can use `MapStruct` or `ModelMapper` to automate mapping between Entity and DTOs.
- [Example](DTO-EmployeeRelationshipManagement/README.md)

## JDBC
- JDBC is important to understand behind the scenes, but you should only dive into it if:
  - You're doing low-level DB operations
  - You want to understand how JPA works internally
  - You’re building something very performance-critical
- [Example](JDBC-LibraryManagementSystem/README.md)
