# Test Driven Development (TDD)

The TDD is the practice to write tests **before** implement the application code. This technic is widely used because show what to expect before implementing the desired functionality. In this way, the system is designed based what we want it to do, rather than what the system already does.

## Testing Pyramid

![testing pyramid](https://raw.githubusercontent.com/vmware-tanzu-learning/spring-academy-assets/main/courses/course-spring-brasb-build-a-rest-api/test-pyramid.jpg)

- **Unit Test:** A Unit Test is the smallest "unit" of the system that's isolated from the rest of the system. They should be **simple** and **speed*, such as methods or individual classes.
- **Integration Test:** Integration Tests validates the interaction between different components in the system, such as APIs and services. They are more complicated to write and maintain, and run slower than unit tests.
- **End-to-End Test (e2e Tests):** An End-to-End Test simulates the system using the sema interface that a user would, such as a web browser. While extremely thorough, End-to-End Tests can be very slow and fragile because they use simulated user interactions in potentially complicated UIs. Implement the smallest number of these tests.

## Red-Green-Refactor

1. Red (Write a failing test)
   - Write a test based on the expected requirement.
   - Initially, the test will fail because the functional code does not yet exist.
2. Green (Write the minimal code to pass the test)
   - Implement just enough code to make the test pass.
3. Refactor (Improve the code)
   - Refactor the code while ensuring the test still passes.
