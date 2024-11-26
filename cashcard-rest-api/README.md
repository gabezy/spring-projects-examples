# Family Cash Card API

A RESTful API built with Spring Boot and Gradle, designed to help parents easily manage allowances for their kids via virtual "cash cards." With the Family Cash Card app, parents can create, edit, view, and delete cash cards, while also ensuring security to protect against unauthorized access.

## Background

Parents often struggle to manage allowances for their children efficiently. Instead of handling physical cash, the Family Cash Card app offers a cloud-based solution where parents can manage virtual "cash cards" for their children. Similar to a gift card, this cash card lets parents:
- **Add** funds to a child’s cash card,
- **Track** spending and balance,
- **Control** access securely.

The Family Cash Card API enables all of this functionality, providing endpoints for managing cash cards and securing data.

## Features

- **Create a Cash Card**: Set up a new cash card for a child, with an initial balance.
- **View Cash Cards**: Retrieve information on a specific cash card or list all cash cards.
- **Update a Cash Card**: Modify details of a cash card, including balance and limits.
- **Delete a Cash Card**: Remove a cash card when it’s no longer needed.
- **Security**: Ensure only authorized users have access to the API.

## Getting Started

### Prerequisites

- **Java**: Version 17+
- **Gradle**: Version 7+
- **Database**: PostgreSQL (or compatible)

### Installation

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/your-username/family-cash-card-api.git
   cd family-cash-card-api
    ```
2. **Build the project**
    ```bash
   ./gradlew build
    ``` 
3. **Run the application**
    ```bash
   ./gradlew booRun
   ```

### Configuration

* Environment variables
  * `DB_URL`: Database connection URL
  * `DB_USERNAME`: Database username 
  * `DB_PASSWORD`: Database password
  * `JWT_SECRET`: Secret key for securing JWT Tokens
* **database**: app is configured to use PostgreSQL, but it can be adapted to other databases by changing the configurations in `application.yml`.

### API Documentation

| Endpoint               | Method | Description                         |
|------------------------|--------|-------------------------------------|
| `/api/cashcards`       | GET    | List all cash cards                 |
| `/api/cashcards/{id}`  | GET    | Get details of a specific cash card |
| `/api/cashcards`       | POST   | Create a new cash card              |
| `/api/cashcards/{id}`  | PUT    | Update a specific cash card         |
| `/api/cashcards/{id}`  | DELETE | Delete a specific cash card         |

 
### Docs References

- [Spring x Spring boot and Controller-Repository Architecture](./docs/Spring.md)
- [Test Driven Development](./docs/TDD.md)
- [REST and REST in Spring Boot](./docs/REST.md)
