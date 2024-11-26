# Spring and Spring boot
Spring and Spring Boot are both Java frameworks that are used to build applications. Think of them as toolkits that help developers build and structure their code in an efficient and scalable way.



## Spring
Spring is comprehensive framework that provides various modules for building different types of application, like Spring security to add a robust layer of security in your application and Spring MVC to build web application like APIs REST. The versatility comes at a cost. Setting up a Spring application requires a lot of configuration, and developers need to manually configure various components of the framework to get an application up and running.



## Spring Boot
Spring Boot is like a more opinionated version of Spring, that comes with many pre-configured settings and dependencies that are commonly used. Spring boot, in summary, is a powerful, comprehensive framework that gives you a lot of flexibility, but is more opinionated, streamlined version of Spring that comes with a lot of built-in features to help you get started quickly and easily.

## Spring IoC (Inversion of Control)

Spring Boot allows you to configure how and when dependencies are provided to your application at runtime. For example, you might want to use a different database for local development than for your live, public-facing application. Your application code shouldn't care about this distinction;

**note**:  Inversion of control is often called dependency injection (DI), though this is not strictly correct. Dependency injection and accompanying frameworks are one way of achieving inversion of control, and Spring developers will often state that dependencies are "injected" into their applications at runtime.

[Spring's IoC Container documentation](https://docs.spring.io/spring-framework/reference/core/beans.html)

## Controller-Repository Architecture

The **Separation of Concerns** principle states that well-designed software should be modular, with each module having distinct and separate concerns from any other module.

The common architectural framework divides layers typically by function or value, such as business, data and presentation layers, is called **Layered Architecture**.

- **Controller:** Is the nearest layer to the client, receives and respond the web request.
- **Repository:** Represents the layer near to the data store , that reads from and writes to the data store.
- **Service:** Is the layer that handles the business logic and algorithms.

<img src="https://raw.githubusercontent.com/vmware-tanzu-learning/spring-academy-assets/main/courses/course-spring-brasb-build-a-rest-api/layers.png" width="700" height="500"/>o

## Database

That are two type of databases in Spring, **embedded, in-memory** and **persistent**. **"embedded"** simply means that it's a Java library and can be added as a dependency in the project. **"in-memory"** means that it stores data in memory only, as opposed to persisting data in permanent, durable storage.

<img src="https://raw.githubusercontent.com/vmware-tanzu-learning/spring-academy-assets/main/courses/course-spring-brasb-build-a-rest-api/db-types.png" width="700" height="500"/>
