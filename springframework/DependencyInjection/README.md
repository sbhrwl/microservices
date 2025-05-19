# Dependency injection
- [Introduction](#introduction)
- [Inversion of control](#inversion-of-control)
- [Dependency injection](#dependency-injection)
- [Example](#example)
- [Spring IoC container](#spring-ioc-container)
- [Benefits of DI and IoC](#benefits-of-di-and-ioc)
- [Spring annotations](#spring-annotations)
## Introduction
- In modern application development, managing dependencies between objects manually can quickly become complex, especially as applications grow. 
- **Spring Framework** addresses this problem elegantly through **Inversion of Control (IoC)** and **Dependency Injection (DI)**
  - DI and IoC are core principles that simplify how objects are created and connected.

## Inversion of control
- **Inversion of Control** is a design principle where the control of object creation and lifecycle is shifted from the application code to the **Spring container**. 
- Instead of creating objects manually with `new`, we let Spring create and manage them for us. 
- This approach improves **loose coupling** and enhances **testability**.
  - Your classes no longer control their dependencies—they rely on the framework to provide them.

## Dependency injection
- **Dependency Injection** is a specific type of IoC where dependencies (objects that a class depends on) are **injected** into a class by the Spring container, rather than the class instantiating them directly.
- There are three main types of DI in Spring:
  - **Constructor Injection** – dependencies are provided through the class constructor.
  - **Setter Injection** – dependencies are set via public setter methods.
  - **Field Injection** – dependencies are injected directly into the class fields using annotations.
## Example
- Here, the `Car` class depends on an `Engine`. 
- Instead of creating `Engine` inside `Car`, we let Spring inject it when creating the `Car` bean.
```java
@Component
public class Car {
    private final Engine engine;

    @Autowired
    public Car(Engine engine) { // Constructor Injection
        this.engine = engine;
    }

    public void start() {
        engine.run();
    }
}

@Component
public class Engine {
    public void run() {
        System.out.println("Engine running...");
    }
}
```

## Spring IoC container
- The **IoC container** in Spring is responsible for:
  - Instantiating beans
  - Resolving their dependencies
  - Managing their life cycle
- It uses configuration metadata (via annotations, XML, or Java config) to understand how to wire everything.

## Benefits of DI and IoC
- **Loose coupling** between classes
- **Improved testability** (easy to mock dependencies)
- **Simplified configuration and scaling**
- **Centralized dependency management**

## Spring annotations
- Spring annotations are special markers (starting with `@`) that tell the Spring framework `how to configure and manage components` like `classes, methods, and variables`. 
- These annotations help reduce XML configuration and make the code easier to read and maintain.
### @Component  
- Marks a Java class as a Spring-managed component (bean).
- Spring automatically detects it during classpath scanning.

```java
@Component
public class MyService { }
```

### @Service, @Repository, @Controller  
- Specialized versions of `@Component` used for different layers in an application:  
- `@Service`: for business logic.  
- `@Repository`: for data access (adds exception translation).  
- `@Controller`: for web controllers in Spring MVC.

### @Autowired  
- Automatically injects (wires) a bean where it is needed.

```java
@Autowired
private MyService myService;
```

### @Configuration  
- Marks a class that contains Spring bean definitions using `@Bean` methods.

```java
@Configuration
public class AppConfig { }
```

### @Bean  
- Declares a bean explicitly inside a `@Configuration` class.

```java
@Bean
public MyService myService() {
    return new MyService();
}
```

### @RequestMapping  
- Maps HTTP requests to handler methods in `@Controller` classes.

```java
@RequestMapping("/home")
public String homePage() {
    return "home";
}
```

### @GetMapping, @PostMapping, etc.  
- Shorthand annotations for `@RequestMapping` with specific HTTP methods.

```java
@GetMapping("/items")
public List<Item> getItems() { }
```

### @RestController  
- Combines `@Controller` and `@ResponseBody` — used to create REST APIs.

```java
@RestController
public class ApiController { }
```

### @Value  
- Injects values from property files into Spring beans.

```java
@Value("${app.name}")
private String appName;
```

### @Qualifier  
- Used with `@Autowired` when multiple beans of the same type exist.

```java
@Autowired
@Qualifier("specialService")
private MyService service;
```