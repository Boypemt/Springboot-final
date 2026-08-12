# Task Assignment — Plants vs Zombie Shop

Who builds what, and everything an AI assistant needs to build it correctly.
The architecture is in [README.md](README.md); this file is the work plan.

---

## 0. How to use this file

Each member works on **their own service only**. To get help from an AI
(ChatGPT / Gemini / Claude), open a fresh chat and paste, in this order:

1. **Section 1** — shared conventions (same for everyone)
2. **Section 2** — the integration contracts (so your JSON matches everyone else's)
3. **Your own section** from Section 4 — your service brief

Then ask for one file at a time. Do not paste the whole README — it is longer
than it needs to be and the AI will drift.

> **Warning that matters at the demo:** the professor asks each member to explain
> the code they wrote. Read what the AI gives you before you commit it. If you
> cannot explain a line, delete it or ask the AI to explain it until you can.

---

## 1. Shared conventions — paste this first

```
We are building a microservice backend for a university course (MFU, Backend
Development). Follow these conventions exactly. Do not modernise anything.

VERSIONS
- Spring Boot 2.x (2.3-2.5), Spring Cloud Hoxton/2020.x, Java 11, Maven.
- JPA imports are javax.persistence.*  — NEVER jakarta.persistence.*
- JUnit 5 (org.junit.jupiter.api.Test) with @SpringBootTest and MockMvc.

PROJECT SHAPE
- One Maven multi-module project. Each service is its own module with its own
  application.properties, its own in-memory H2 database and its own data.sql.
- Base package: th.mfu.pvz.<service>   e.g. th.mfu.pvz.catalog
- Inside a service: domain / repository / dto / dto.mapper / controllers at the
  service root.

CODE STYLE
- Controllers: @RestController on the class, @RequestMapping("/api"),
  @GetMapping/@PostMapping/@PutMapping/@PatchMapping/@DeleteMapping on methods.
- ALWAYS return new ResponseEntity<>(body, HttpStatus.XXX).
  Never ResponseEntity.ok() or .status(...).
- Injection: @Autowired private XRepository repository;   (field injection)
- Repositories extend CrudRepository<T, Long> and redeclare List<T> findAll();
  derived queries only (findByX, findByXAndY). Never write SQL, never use
  EntityManager.
- Entities: @Entity, @Table(name="..."), @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY), a public no-arg
  constructor, and getters + setters for EVERY field.
- Entities NEVER leave the controller. Every response and request body is a DTO.
- DTO fields are object types (Long, Integer, Double, Boolean) — never
  primitives, because PATCH works by skipping null fields.
- Mapping uses MapStruct: @Mapper(componentModel = "spring"), and for PATCH
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  on an update method with @MappingTarget.

STATUS CODES
  GET ok 200 · POST created 201 · PUT ok 200 · PATCH ok 200 · DELETE 204
  bad input or unknown referenced id 400 · not found 404
  another service is down 503

PUT vs PATCH
  PUT replaces the whole resource (missing fields are cleared).
  PATCH merges (missing fields are left alone). Both must exist.

MICROSERVICE WIRING
- Every service except the naming server has @EnableDiscoveryClient and:
    eureka.client.register-with-eureka=true
    eureka.client.fetch-registry=true
    eureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/
- Feign clients use @FeignClient(name = "<spring.application.name>") — a NAME,
  never a URL, because the name is what gets load balanced.
- Kafka: producer uses KafkaTemplate<String,String>; consumers use
  @KafkaListener(topics = "${app.kafka.topic:orders}", groupId = "<name>-group").

SERVICE NAMES AND PORTS
  naming-server        netflix-eureka-naming-server   8761
  catalog-service      catalog-service                8100 (second copy 8101)
  customer-service     customer-service               8200
  order-service        order-service                  8300
  inventory-service    inventory-service              8400
  notification-service notification-service           8500
  Kafka: localhost:9094 from the host, kafka:9092 inside Docker.

H2 (same in every service, change only the db name)
  spring.datasource.url=jdbc:h2:mem:<service>db;DB_CLOSE_DELAY=-1
  spring.datasource.driver-class-name=org.h2.Driver
  spring.datasource.username=sa
  spring.datasource.password=
  spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
  spring.jpa.hibernate.ddl-auto=create-drop
  spring.jpa.defer-datasource-initialization=true
  spring.datasource.initialization-mode=always
  spring.jpa.show-sql=true

HOW TO ANSWER ME
- Return complete files I can paste over the original.
- Do not add dependencies I did not ask for, and no Lombok.
- After the code, 3 bullets max explaining what it does, so I can explain it in
  an oral exam.
```

---

## 2. Integration contracts — paste this second

Four people are writing four services. These JSON shapes are the agreement
between them. **Nobody changes a field name here without telling the group.**

### `ProductDTO` — produced by catalog-service, consumed by order-service and inventory-service

```json
{
  "id": 1,
  "plantId": 1,
  "plantName": "Peashooter",
  "className": "Attack",
  "environmentName": "Day",
  "price": 100.00,
  "stock": 25,
  "servedBy": 8100
}
```

`servedBy` is the port of the catalog instance that answered — it is what makes
the load balancer visible. Every catalog response must include it.

### `CustomerDTO` — produced by customer-service, consumed by order-service

```json
{
  "id": 3,
  "username": "crazydave",
  "phone": "0812345678",
  "email": "dave@pvz.com",
  "defaultAddress": "123 Suburb Lane, Bangkok, Thailand 10110"
}
```

Never return `password` in any DTO.

### `OrderDTO` — produced by order-service

```json
{
  "id": 1,
  "customerId": 3,
  "customerName": "crazydave",
  "orderDate": "2026-08-12T14:03:00",
  "totalPrice": 300.00,
  "status": "pending",
  "servedBy": 8100,
  "items": [
    { "id": 1, "productId": 1, "productName": "Peashooter",
      "qty": 3, "unitPrice": 100.00, "lineTotal": 300.00 }
  ]
}
```

### `OrderPlaced` — Kafka event on topic `orders`

Published by order-service, consumed by inventory-service and
notification-service. **This is the most important contract in the project.**

```json
{
  "orderId": 1,
  "customerId": 3,
  "customerName": "crazydave",
  "totalPrice": 300.00,
  "orderDate": "2026-08-12T14:03:00",
  "items": [
    { "productId": 1, "productName": "Peashooter", "qty": 3, "unitPrice": 100.00 }
  ]
}
```

### Stock adjustment — called by inventory-service on catalog-service

```
PATCH /api/products/{id}/stock      body: {"delta": -3}      -> 200 with ProductDTO
```

A negative `delta` reduces the stock. Catalog answers **400** if the stock would
go below zero.

---

## 3. Milestones

| When | What | Who |
| --- | --- | --- |
| **M1** | Repo created, parent `pom.xml` + empty modules pushed, `docker-compose.yml` for Kafka | #1 and #4 |
| **M2** | naming-server runs, every empty service registers and appears on <http://localhost:8761> | #3, then everyone |
| **M3** | catalog-service and customer-service work standalone (all REST verbs, tests pass) | #2, #3 |
| **M4** | order-service creates an order using Feign, publishes to Kafka | #1 |
| **M5** | both consumers react; stock drops; notification appears | #4 |
| **M6** | Load-balancer demo (2 catalog instances), README finished, video recorded | everyone |

**M3 does not need M4.** #2 and #3 can start immediately and should not wait for
anybody.

### Git workflow

```bash
git checkout -b feature/<your-service>     # e.g. feature/catalog-service
# work, commit often
git push -u origin feature/<your-service>
# open a Pull Request on GitHub, one other member reviews, then merge to main
```

Never commit `target/`. Never push straight to `main` once M1 is merged.

---

## 4. The four briefs

---

### Member 1 — เมธาสิทธิ์ พิบูลย์ศิลป์ (682110189)

**You own: `order-service` (8300), plus the parent `pom.xml` and repo setup.**

This is the hardest service: it is the only one that both calls other services
(Feign) and publishes events (Kafka). Do the repo setup first so everyone else
is unblocked.

**Part A — repo setup (do this first, today)**

* Parent `pom.xml`, `packaging: pom`, `spring-boot-starter-parent` 2.x, Java 11,
  `spring-cloud-dependencies` in `dependencyManagement`, MapStruct in
  `pluginManagement` (`annotationProcessorPaths`).
* Six empty modules: `naming-server`, `catalog-service`, `customer-service`,
  `order-service`, `inventory-service`, `notification-service`.
* `.gitignore` with `target/`.
* Push to `main` and tell the group.

**Part B — your service**

Entities (`th.mfu.pvz.order.domain`), from the data dictionary in the README:

| Entity | Table | Fields |
| --- | --- | --- |
| `Order` | `orders` | `id`, `customerId` (Long — **not** a @ManyToOne), `orderDate` (LocalDateTime, default now), `totalPrice` (BigDecimal), `status` (String, default `"pending"`) |
| `OrderItem` | `order_items` | `id`, `productId` (Long), `qty` (Integer), `unitPrice` (BigDecimal), `@ManyToOne Order order` |

`Order` → `OrderItem` is `@OneToMany(mappedBy="order", cascade=ALL, orphanRemoval=true)`.
That cascade **is** the `ON DELETE CASCADE` of the weak entity in our schema —
be ready to say that out loud at the demo.

`customerId` and `productId` are plain `Long` because those rows live in another
service's database. That is the whole lesson of the microservice split.

Feign clients (`th.mfu.pvz.order.client`):

```java
@FeignClient(name = "customer-service")
public interface CustomerClient {
    @GetMapping("/api/customers/{id}")
    CustomerDTO getCustomer(@PathVariable("id") Long id);
}

@FeignClient(name = "catalog-service")
public interface CatalogClient {
    @GetMapping("/api/products/{id}")
    ProductDTO getProduct(@PathVariable("id") Long id);
}
```

Add `@EnableFeignClients` and `@EnableDiscoveryClient` to the main class.

Endpoints:

| Method | Path | Behaviour |
| --- | --- | --- |
| POST | `/api/orders` | Body `{"customerId":3,"items":[{"productId":1,"qty":3}]}`. Validate the customer via Feign (**400** if unknown), fetch each product via Feign (**400** if unknown, **400** if `stock < qty`), copy `price` into `unitPrice`, sum `totalPrice`, save, **then publish `OrderPlaced` to Kafka**, return **201** with `OrderDTO`. |
| GET | `/api/orders` · `/api/orders/{id}` | 200, or 404 |
| PUT | `/api/orders/{id}` | full replace, 200 or 404 |
| PATCH | `/api/orders/{id}` | partial, typically `{"status":"shipped"}`, 200 or 404 |
| DELETE | `/api/orders/{id}` | 204 or 404 (items cascade away) |

Failure handling — this earns Q&A marks:

```java
catch (FeignException.NotFound e) { return new ResponseEntity<>(HttpStatus.BAD_REQUEST); }
catch (Exception e)               { return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE); }
```

plus `feign.client.config.default.connectTimeout=2000` and `readTimeout=2000`.

**Done when:** `POST /api/orders` returns 201 with `customerName` and
`productName` filled in from the other two services, the event is visible in
`docker exec -it kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic orders --from-beginning`,
and an unknown customer gives 400 instead of 500.

---

### Member 2 — ปัณณวิชญ์ สิทธิตัน (682110181)

**You own: `catalog-service` (8100, and a second copy on 8101).**

The biggest service — 4 of the 8 tables and all five REST verbs. You can start
immediately; you depend on nobody.

Entities (`th.mfu.pvz.catalog.domain`), exactly as the data dictionary says:

| Entity | Table | Fields |
| --- | --- | --- |
| `PlantClass` | `classes` | `id`, `classname` (unique), `description` |
| `Environment` | `environments` | `id`, `envname` (unique), `description` |
| `Plant` | `plants` | `id`, `name`, `description`, `hp`, `dmg`, `sunCost`, `actionSpeed`, `@ManyToOne PlantClass plantClass`, `@ManyToOne Environment environment` |
| `Product` | `products` | `id`, `price` (BigDecimal), `stock` (Integer), `@OneToOne Plant plant` with `@JoinColumn(name="plant_id", unique=true)` |

> The Java class is `PlantClass`, not `Class` — `Class` is taken by `java.lang.Class`.
> Table stays `classes`.

Relationships to be able to explain:

* `Classes → Plants` and `Environments → Plants` are **One-to-Many** — the
  `@ManyToOne` side owns `class_id` / `environment_id`.
* `Plants ↔ Products` is **One-to-One** with a unique FK, which is how the schema
  separates game stats from sale data.

Endpoints — all five verbs on **both** `/api/plants` and `/api/products`
(this is where the "REST GET POST PUT DELETE PATCH" marks come from), plus:

| Method | Path | Behaviour |
| --- | --- | --- |
| PATCH | `/api/products/{id}/stock` | Body `{"delta": -3}`. Adds `delta` to the stock. **400** if the result would be negative. Returns 200 + `ProductDTO`. Used by inventory-service. |
| GET | `/api/classes` · `/api/environments` | 200 |
| GET | `/api/plants?classId=1` | optional filter, derived query |

**Every response must include `servedBy`:**

```java
@Autowired private Environment env;   // org.springframework.core.env.Environment
dto.setServedBy(Integer.parseInt(env.getProperty("server.port")));
```

Without it the load-balancer demo (5 marks) cannot be shown.

`data.sql`: at least 3 classes, 3 environments, 8 plants and 8 products
(Peashooter, Sunflower, Wall-nut, Cherry Bomb, Snow Pea, Chomper, Repeater,
Potato Mine — real PvZ names look much better in the demo).

Tests: `PlantControllerTest` and `ProductControllerTest` with MockMvc —
create / list / put / patch / delete, plus one proving PATCH does **not** wipe
the fields you did not send.

**Done when:** all five verbs work on both resources, `mvn test` is green, and
you can start a second copy with
`mvn -pl catalog-service spring-boot:run "-Dspring-boot.run.arguments=--server.port=8101"`
where both appear on the Eureka page.

---

### Member 3 — สิรวิชญ์ ยวงคำ (682110199)

**You own: `naming-server` (8761) and `customer-service` (8200).**

The naming server is small but everybody is blocked until it exists — build it
first, today, then take your time on customer-service.

**Part A — naming-server (about 30 minutes)**

```java
@SpringBootApplication
@EnableEurekaServer
public class NamingServerApplication { ... }
```

```properties
spring.application.name=netflix-eureka-naming-server
server.port=8761
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
# make the lab quick enough to demo
eureka.server.eviction-interval-timer-in-ms=5000
eureka.server.response-cache-update-interval-ms=5000
```

The two `false` lines are the exam question: the naming server **is** the list,
so it must not register with itself and has nothing to fetch.

**Part B — customer-service**

| Entity | Table | Fields |
| --- | --- | --- |
| `Customer` | `customers` | `id`, `username` (unique), `password`, `phone`, `email` (unique) |
| `Address` | `addresses` | `id`, `country`, `city`, `district`, `subDistrict`, `zipcode`, `isDefault` (Boolean, default false), `@ManyToOne Customer customer` |

`Customer → Address` is **One-to-Many** (`@OneToMany(mappedBy="customer", cascade=ALL, orphanRemoval=true)`).

Endpoints:

| Method | Path | Behaviour |
| --- | --- | --- |
| GET | `/api/customers` · `/api/customers/{id}` | 200, or 404. `CustomerDTO` includes `defaultAddress` as one formatted string, built from the address where `isDefault` is true |
| POST | `/api/customers` | 201. **409 Conflict** if the username or email already exists |
| PUT / PATCH | `/api/customers/{id}` | 200, or 404 |
| DELETE | `/api/customers/{id}` | 204, or 404 |
| GET / POST | `/api/customers/{id}/addresses` | 200 / 201 |
| PATCH | `/api/addresses/{id}` | 200 — e.g. `{"isDefault": true}` |

**Never put `password` in a DTO.** That is a deliberate DTO decision and a good
thing to be asked about: the entity has it, the wire format does not.

Tests: `CustomerControllerTest` — create / list / patch / delete, plus one
asserting a duplicate username gives 409 and one asserting the response JSON has
no `password` field.

**Done when:** the Eureka page at <http://localhost:8761> lists every running
service, and `GET /api/customers/1` returns a customer with `defaultAddress`
filled in and no password.

---

### Member 4 — สายกลาง จะวะนะ (682110198)

**You own: `inventory-service` (8400), `notification-service` (8500), and
`docker-compose.yml`.**

Both of your services are Kafka consumers. They are small, but they are worth
5 marks and they carry the whole "decoupling" story.

**Part A — docker-compose.yml (do this first, today)**

ZooKeeper + Kafka, copied from the `lab-web-pubsub` lab. Kafka must be reachable
at `localhost:9094` from the host and `kafka:9092` inside the network. Push it
so the others can `docker compose up -d`.

**Part B — inventory-service (8400)**

```java
@KafkaListener(topics = "${app.kafka.topic:orders}", groupId = "inventory-group")
public void onOrderPlaced(ConsumerRecord<String, String> record) { ... }
```

Read the `OrderPlaced` JSON (contract in Section 2) with Jackson `ObjectMapper`,
and for each item call catalog-service:

```java
@FeignClient(name = "catalog-service")
public interface CatalogClient {
    @PatchMapping("/api/products/{id}/stock")
    ProductDTO adjustStock(@PathVariable("id") Long id, @RequestBody StockDeltaDTO delta);
}
```

Save one `StockMovement` row per item (`id`, `orderId`, `productId`, `qty`,
`movedAt`, `result`) so there is something to show, and expose
`GET /api/stock-movements`.

**Part C — notification-service (8500)**

Same pattern, **different group id**:

```java
@KafkaListener(topics = "${app.kafka.topic:orders}", groupId = "notification-group")
```

Save a `Notification` (`id`, `orderId`, `customerId`, `message`, `createdAt`,
`status`) with a message like
`"Order #1 received from crazydave, total 300.00 THB"`, and expose
`GET /api/notifications`.

**The exam question you must be able to answer:** why two different group ids?
Because different groups each receive their own copy of every event. If both
services used one group, Kafka would treat them as two copies of the same
subscriber and split the stream — each order would reach only one of them, and
half the stock updates would silently go missing.

**Done when:** one `POST /api/orders` makes a row appear in **both**
`/api/stock-movements` and `/api/notifications`, and the product's stock has
dropped. Then, for the demo: stop notification-service, place two orders (they
still return 201), restart it — it processes both missed events and catches up.
That is the decoupling proof, and it is the best 60 seconds of the video.

---

## 5. Rubric coverage — who earns what

| Rubric line | Points | Earned by |
| --- | --- | --- |
| REST GET POST PUT DELETE PATCH | 5 | #2 (catalog: all five on plants and products), #1 (orders), #3 (customers) |
| JPA — 5 related entities | 10 | #2 (PlantClass, Environment, Plant, Product), #3 (Customer, Address), #1 (Order, OrderItem) = 8 entities |
| Microservice with Feign and Eureka | 5 | #3 (naming-server), #1 (Feign clients), #4 (Feign to catalog) |
| Load balancer | 5 | #2 (`servedBy` in every response, second instance on 8101) |
| Kafka pub/sub | 5 | #1 (producer), #4 (two consumers, two groups) |

Points are partly awarded through the demo Q&A — **each member explains their
own service.** The likely questions are written into each brief above.
