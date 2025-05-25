# Spring Boot
- [Introduction](#introduction)
- [Features](#features)
- [Use cases](#use-cases)
- [Projects](#projects)
## Introduction
- Spring Boot is a framework built on top of the `Spring Framework` that simplifies the development of production-ready, stand-alone Java applications. 
- It `reduces boilerplate configuration` and allows developers to quickly create applications with minimal setup.

## Features
- Auto-configuration
  - Automatically configures your application based on the dependencies present.
- Standalone
  - No need for external application servers, it can run as a simple Java application using an embedded server like Tomcat or Jetty.
- Production-ready
  - Comes with built-in features like `health checks, metrics, and externalized configuration`.
- Opinionated defaults
  - Provides sensible defaults for application setup, speeding up development.

## Use cases
- Building REST APIs
- Microservices
- Web applications using `Thymeleaf` or `JSP`
- Cloud-native applications

## Projects
| Project Name                               | Description                                                        |
|--------------------------------------------|--------------------------------------------------------------------|
| [Greetings message](https://github.com/sbhrwl/microservices/blob/main/README.md)                      | A basic REST API that generates a greeting message based on the time of day. |
| [REST endpoints](RESTendpoints/README.md)                      | Demonstrates GET, POST, PUT and DELETE methods, along with sending `query parameters` and `Path variables` in the URL.                          |
| [Sending JSON data](SendingJSONData/README.md)      | Demonstrates sending JSON data to the server.                      |
| [Receiving JSON data](ReceivingJSONData/README.md)    | Demonstrates receiving JSON data from the client.                  |
| [Receiving JSON data and sending response as a `Collection object`](ReceivingJSONToCollectionObject/README.md) | Demonstrates receiving JSON data and sending response as a collection object. |
| [ActiveMQ query parameter](ActiveMQ-QueryParameter/README.md)    | Demonstrates sending request with Query parameters.                  |
| [ActiveMQ JSON payload](ActiveMQ-JSONpayload/README.md) | Demonstrates sending request with JSON payload. |
