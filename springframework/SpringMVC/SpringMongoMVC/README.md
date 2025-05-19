# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.2.0/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.2.0/maven-plugin/reference/html/#build-image)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.2.0/reference/htmlsingle/index.html#web)
* [Spring Data MongoDB](https://docs.spring.io/spring-boot/docs/3.2.0/reference/htmlsingle/index.html#data.nosql.mongodb)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Accessing Data with MongoDB](https://spring.io/guides/gs/accessing-data-mongodb/)

## API
- https://www.youtube.com/watch?v=qVNOw9TWwxo
- POST http://localhost:8080/tasks
```
{
    "description": "MongoDb crud",
    "severity": 1,
    "assignee": "sabharwal",
    "storyPoint": 2
}

# Setup
## Java setup
- Download jdk
- set JAVA_HOME and PATH (path to the bin)
## Maven setup
- Download maven
- set MAVEN_HOME and PATH (path to the bin)
## VS code extensions
- Java extension pack
- Spring boot extension pack
## Add dependencies
- spring-boot-devtools
- spring-boot-starter-data-cassandra
## Git
- Set a Git username
  - `git config --global user.name "rahul"`
- Setting your commit email address
  - `git config --global user.email "sabharwalonnet@gmail.com"`
  - email shall be same as email for Github account
- `git init`
- `git add .`
- `git commit -m "commit message"`
- First push to Github repository
  - `git push --set-upstream https://github.com/sbhrwl/betterreads.git master`
  - subsequent push `git push`  
```
