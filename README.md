# PRO3 Assignment 1 – Part 2: Slaughterhouse Traceability gRPC Service

Spring Boot gRPC server built on the course template
([viajook/spring-boot-grpc-server-example](https://github.com/viajook/spring-boot-grpc-server-example)).
It reads from the **Central Traceability Store** (PostgreSQL) and answers two questions:

| RPC | Input | Returns |
|---|---|---|
| `GetAnimalsInProduct` | `product_id` | registration numbers (+ weight, arrival time) of all animals with parts in the product |
| `GetProductsForAnimal` | `registration_number` | all products that contain a part of the animal (used for recall) |

Errors: `INVALID_ARGUMENT` for an empty id, `NOT_FOUND` for an unknown animal/product.
An animal that exists but has not been packed yet returns an empty list.

## Project structure
```
src/main/proto/traceability.proto          gRPC contract
src/main/java/dk/via/pro3/slaughterhouse/
    SlaughterhouseGrpcApplication.java     Spring Boot main class
    service/TraceabilityServiceImpl.java   gRPC endpoint (extends generated ImplBase)
    repository/TraceabilityRepository.java interface for data access
    repository/TraceabilityRepositoryJdbc.java  SQL queries (Spring JdbcClient)
    entity/AnimalEntity, ProductEntity     database rows
src/main/resources/schema.sql, data.sql    tables + demo data (reloaded on every start)
src/test/...                               JUnit tests (H2 in-memory database)
```
Generated gRPC code goes to `src/main/java/dk/via/pro3/generated` (in `.gitignore`, like in the template).

## Run
1. Open the project in IntelliJ and "Load Maven Changes".
2. Run `mvn compile` (or Maven → Lifecycle → compile) to generate the gRPC code.
3. Create the database in PostgreSQL:
   ```sql
   CREATE DATABASE slaughterhouse;
   ```
   Change username/password in `src/main/resources/application.properties` if yours differ.
4. Run `SlaughterhouseGrpcApplication`. The server listens on **localhost:9090**.

## Test
### JUnit
`mvn test` – uses an in-memory H2 database, so PostgreSQL does not need to run.
- `TraceabilityServiceImplTest` – calls the service methods directly (10 cases: normal, duplicates, half animal, empty result, NOT_FOUND, INVALID_ARGUMENT).
- `TraceabilityGrpcClientTest` – end-to-end: a real gRPC client (blocking stub) calls the server on port 9091.

### Postman
New → gRPC → server URL `localhost:9090` → select method (server reflection, or import `traceability.proto`).

| Method | Message | Expected |
|---|---|---|
| `GetAnimalsInProduct` | `{"product_id": "P-5003"}` | A-1001, A-1002, A-1003 (half animal from 3 animals) |
| `GetAnimalsInProduct` | `{"product_id": "P-5001"}` | A-1001 (only once, though 2 parts) |
| `GetProductsForAnimal` | `{"registration_number": "A-1002"}` | P-5002, P-5003, P-5004 |
| `GetProductsForAnimal` | `{"registration_number": "A-1004"}` | empty list (not cut yet) |
| `GetProductsForAnimal` | `{"registration_number": "A-9999"}` | status NOT_FOUND |

## Demo data
| Product | Type | Parts → animals |
|---|---|---|
| P-5001 | PART_PACKAGE | 2 legs from A-1001 |
| P-5002 | PART_PACKAGE | 1 leg from A-1002 |
| P-5003 | HALF_ANIMAL | leg A-1003, rib A-1001, loin A-1002, shoulder A-1003 |
| P-5004 | PART_PACKAGE | 1 rib from A-1002 |
