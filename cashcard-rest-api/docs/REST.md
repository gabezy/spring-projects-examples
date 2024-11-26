## REST (Representational State Transfer)

In a RESTful system, data objects are called **Resource Representations**. The purpose of a RESTful  API (Application Programming Interface) is to manage the state of these Resources. Basically, a REST is just a way to manage the values of objects through an API, and are often stored in an database.

<img src="https://raw.githubusercontent.com/vmware-tanzu-learning/spring-academy-assets/main/courses/course-spring-brasb-build-a-rest-api/rest-http-flow.png" width="700" height="350"/>

## CRUD

**CRUD** stands from "Create, Read, Update and Delete", and are the four basic operations that can be performed on objects in a data store.

## HTTP

*Hypertext Transfer Protocol* is the form that a REST API communicates between the client (front-end or Postman) to Server (Back-end or service). The client sends a request to a URI and a web server receives the request, routes it to a request handler. The handler performs the operation and create a response, which is then sent back to the client.

<img src="https://www.akamai.com/site/en/images/article/2023/how-a-web-api-works.png" width="700" height="350"/>

- Components of the Request
  - Method (also called Verb)
  - URI (also called Endpoint)
  - Body
- Components of the Response
  - Status Code
  - Body

- For CREATE: use HTTP method POST
- For READ: use HTTP method GET
- For UPDATE: use HTTP method PUT
- For DELETE: use HTTP method DELETE

To perform `READ`, `UPDATE` and `DELETE` operations, the application needs the unique identifier (ID) of the correct resource. For example `/cashcards/42`, the resource's ID in this case is `42`.

## REST in Spring Boot

### Spring Web Controllers

In Spring WQeb, Request are handled by Controllers, that are annotated with `@RestController`.

```java
@RestController
class CashCardController {}
```
The Controller gets injected into Spring Web, which routes API requests to the correct method.

<img src="https://raw.githubusercontent.com/vmware-tanzu-learning/spring-academy-assets/main/courses/course-spring-brasb-build-a-rest-api/webcontroller-implementingGET.jpg" width="700" height="450"/>

- `@GetMapping` for HTTP GET
- `@PostMapping` for HTTP POST
- `@PutMapping` for HTTP PUT
- `@DeleteMapping` for HTTP DELETE

To retrieve the ID's value from the URI, it is used `@PathVariable` annotation and has to match the name inside `{}` like `{requestedId}`, that allows the Spring to assign (inject) the correct value to the `requestedId` variable.
```java
@GetMapping("/cashcards/{requestedId}")
public CashCard findById(@PathVariable Long requestedId) {}
```

## Idempotence and HTTP

An **idempotent** operation is defined as one which, if performed more than once, results the same outcome. In REST API context, idempotent operation is one that even if it were performed several times, the resulting data would be the same. 

For each method, the HTTP standard specifies whether it is idempotent or not. `GET`, `PUT`, and `DELETE` are idempotent, whereas `POST` and `PATCH` are not.


<img src="https://raw.githubusercontent.com/vmware-tanzu-learning/spring-academy-assets/main/courses/course-spring-brasb-build-a-rest-api/idempotency.jpg" width="700" height="266"/>

## Pagination and Sorting

When the server has  a large of resources (cash cards), it is not interest to return all this information. Io order to ensure that an API response doesn’t include an astronomically large number of Cash Cards, let’s utilize Spring Data’s pagination functionality.

Pagination in Spring (and many other frameworks) is to specify the page length (e.g. 10 items), and the page index (starting with 0). For example, if a user has 25 Cash Cards, and you elect to request the second page where each page has 10 items, you would request a page of size 10, and page index of 1.

For default, Spring provides an "unordered" sorting strategy, when we do not select the specific fields to sort. This "unordered" strategy may seem random, but is predictable; in other words, never changes on subsequent request.

### Spring Data Pagination API
```java
Page<CashCard> page2 = cashCardRepository.findAll(
    PageRequest.of(
        1,  // page index for the second page - indexing starts at 0
        10, // page size (the last page might have fewer items)
        Sort.by(new Sort.Order(Sort.Direction.DESC, "amount"))));
```

### Request and Response

- **Pagination:** Spring can parse out the `page` (0 indexer) and `size` parameters if it is passed a `Pageable` object to a `PagingAndSortingRepository find...()`  method.
- **Sorting:** Spring can parse out the `sort` parameter, consisting of the field name and the direction **separated by a comma** – but be careful, no space before or after the comma is allowed! Again, this data is part of the `Pageable` object.
- **URI:**: /cashcards?**page=1&size=3&sort=amount,desc**

```java
@GetMapping
private ResponseEntity<List<CashCard>> findAll(Pageable pageable) {
   Page<CashCard> page = cashCardRepository.findAll(
           PageRequest.of(
                   pageable.getPageNumber(),
                   pageable.getPageSize(),
                   pageable.getSortOr(Sort.by(Sort.Direction.DESC, "amount"))));
   return ResponseEntity.ok(page.getContent()); // page.getContent() -> to just get the objects list and not the whole Pagination object
}
```
