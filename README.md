# Hyperativa - Card Management API

Hyperativa is a Spring Boot application for managing clients and their cards.

The API allows:

- Registering one card for a client
- Registering one or multiple cards using JSON
- Consulting cards by client name
- Consulting a card by card number
- Starting a card import process

## Technologies

- Java 21
- Spring Boot 3.2.5
- Spring MVC
- Spring Data JPA
- Spring Security
- JWT
- MySQL
- Lombok
- MapStruct
- OpenAPI Generator
- Swagger/OpenAPI specification


## Requirements

Before running the application, make sure you have installed:

- Java 21
- Maven
- MySQL 8+
- An HTTP client such as Postman, Insomnia, curl, or IntelliJ HTTP Client

## Database Setup

The database schema is available at:

src/main/resources/database.sql

## Project Structure

src/main/java/com.hyperativa <br>
├── card <br>
│ ├── config <br>
│ ├── controller <br>
│ ├── exception <br>
│ ├── mapper <br>
│ ├── model <br>
│ ├── repository <br>
│ └── service <br>
├── security <br>
│ ├── config <br>
│ ├── controller <br>
│ ├── dto <br>
│ └── service <br>
└── Main.java<br>
src/main/resources <br>
├── api/api.yaml <br>
├── application.properties <br>
├── database.sql <br>
└── log4j2-spring.xml <br>


## Database Schema Overview

The `database.sql` file defines three main tables:

- `client`
- `import`
- `card`

### `client`

Stores client information.

| Column | Type | Description |
|---|---|---|
| `id` | `INT` | Primary key, auto-generated |
| `name` | `VARCHAR(45)` | Client name |
| `date` | `DATETIME` | Client creation/registration date |

### `import`

Stores information about card import executions.

| Column | Type | Description |
|---|---|---|
| `id` | `INT` | Primary key, auto-generated |
| `file_name` | `VARCHAR(200)` | Imported file name |
| `chunk` | `VARCHAR(8)` | File chunk/control information |
| `date` | `DATETIME` | Import execution date |
| `file_date` | `DATE` | Date related to the imported file |
| `client_id` | `INT` | Foreign key referencing `client.id` |

Relationship:

The `client` table has a one-to-many relationship with the `import` table, where each client can have multiple import records. The `import` table also has a foreign key relationship with the `client` table, linking each import record to a specific client.

### `card`

Stores card information associated with clients.

| Column | Type | Description |
|---|---|---|
| `id` | `BIGINT` | Primary key, auto-generated |
| `card_number` | `VARCHAR(255)` | Card number |
| `client_id` | `INT` | Foreign key referencing `client.id` |
| `date` | `DATETIME` | Card creation/registration date |
| `import_id` | `INT` | Optional foreign key referencing `import.id` |

Relationships:

card.client_id -> client.id card.import_id -> import.id


### Entity Relationship Summary

| Table | Relationships |
|---|---|
| `client` | - One-to-many with `import` |
| `import` | - Foreign key with `client` |
| `card` | - Foreign key with `client` |
| `card` | - Optional foreign key with `import` |

## Application Configuration

The main configuration file is:

src/main/resources/application.properties

Example configuration:

properties spring.threads.virtual.enabled=true
spring.datasource.url=jdbc:mysql://localhost:3306/hyperativa
spring.datasource.username=<DATABASE_USERNAME>
spring.datasource.password=<DATABASE_PASSWORD>
logging.level.org.springframework.security=DEBUG


## OpenAPI and Swagger

The OpenAPI specification is located at:

src/main/resources/api/api.yaml

http://localhost:8080/swagger-ui/index.html


## Authentication

The project contains a security module with JWT support.

Depending on the security configuration, protected endpoints may require an `Authorization` header:

http Authorization: Bearer <JWT_TOKEN>



## Endpoint Summary

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/cards/register/{clientName}/{cardNumber}` | Register one card for a client |
| `POST` | `/api/v1/cards/register` | Register one or multiple cards using JSON |
| `GET` | `/api/v1/cards/client/{clientName}` | Get cards by client name |
| `GET` | `/api/v1/cards/card/{cardNumber}` | Get card by card number |
| `POST` | `/api/v1/cards/import` | Start card import process |

## Notes

- The API contract is defined in `src/main/resources/api/api.yaml`.
- The database schema is defined in `src/main/resources/database.sql`.
- The application uses MySQL as the persistence database.
- Card records are associated with clients.
- Imported cards can be associated with an import execution record.
- JWT authentication may be required depending on the security configuration.
- Avoid exposing real credentials in `application.properties`.

