# geofinder-platform

Geo and full-text place discovery API built with Spring Boot and MongoDB (`MongoTemplate`).

Find places near a point, check which places deliver to a location, and search products by text and distance.
The project is a hands-on way to learn advanced `MongoTemplate` usage: geospatial queries, aggregation, full-text search, atomic updates and custom repositories.

## Tech stack

- Java 17+, Spring Boot 3.3, Spring Web, Spring Validation
- Spring Data MongoDB (`MongoTemplate`, custom repository fragments)
- MongoDB (local)
- Lombok, Maven

## Features so far

**Places**
- Create a place with type, tags, address, location, operating hours and attributes
- Nearby search with `geoNear`: radius in km, optional type filter, limit, distance returned in km
- Add and remove tags in one request (`addToSet` and `pullAll`, tags normalized)
- Atomic running-average rating update using an aggregation-pipeline update

**Delivery zones**
- One place can have many zones (`GeoJsonPolygon`), each with a delivery fee and minimum order
- Create a zone for a place
- Find the zones of a place that contain a given point (`$geoIntersects`)

**Users**
- Create a user (unique email, home and work locations, preferred place types)
- Get a user by id
- Partial update of home and work locations with `MongoTemplate`

**Products**
- Create a product for a place (`BigDecimal` price, unique name per place)
- Full-text search over name, tags and description with relevance ranking (weights 5, 3, 1)
- Text search restricted to places near a point: finds nearby places first, then their matching products, sorted by distance

**Cross-cutting**
- Request validation and a global exception handler with a consistent JSON error body
- `LocalTime` stored as `"HH:mm"` strings through custom converters
- Auditing fields (`createdAt`, `updatedAt`) and optimistic locking (`@Version`)

## API

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/places` | Create a place |
| GET | `/api/places/nearby?latitude=&longitude=&radiusKm=&placeType=&limit=` | Nearby places, nearest first |
| PATCH | `/api/places/{id}/tags` | Body: `{"add": [...], "remove": [...]}` |
| PATCH | `/api/places/{id}/ratings?rating=` | Add a rating (1 to 5) |
| POST | `/api/zones` | Create a delivery zone (`placeId`, `rings`, `minOrderValue`, `deliveryFee`) |
| GET | `/api/zones/deliverable?placeId=&latitude=&longitude=` | Zones of a place that cover the point |
| POST | `/api/users` | Create a user |
| GET | `/api/users/{id}` | Get a user |
| PATCH | `/api/users/{id}/locations` | Update home and/or work location |
| POST | `/api/products` | Create a product |
| GET | `/api/products/search?text=&limit=` | Full-text product search |
| GET | `/api/products/search-nearby?text=&latitude=&longitude=&radiusKm=&limit=` | Product search near a point |

Coordinates are always sent as `latitude` and `longitude`. In MongoDB they are stored GeoJSON-style as `[longitude, latitude]`.

## Data model

| Collection | Notes |
|---|---|
| `places` | `GeoJsonPoint location` with a `2dsphere` index, weekly `operatingHours`, type-specific `attributes` list |
| `delivery_zones` | `placeId` (one place to many zones), `GeoJsonPolygon area` with a `2dsphere` index |
| `users` | Unique `email`, `homeLocation`, `workLocation` |
| `products` | `placeId`, text index on name, tags and description, unique `(placeId, name)` |

## Design decisions

- **Repositories only query their own collection.** Cross-entity logic lives in services, and service dependencies point one way, so there are no circular dependencies.
- **`MongoRepository` for plain CRUD, `MongoTemplate` for everything dynamic**: partial updates, geo queries, aggregations and text queries.
- **Delivery zones are a separate collection** because the relation is one-to-many and each zone has its own polygon and pricing.
- **Text and nearby search are two steps.** MongoDB cannot combine `$text` and `$geoNear` in one query, so nearby places are resolved first and their ids filter the product text query.
- **DTOs for requests and responses.** Entities are never returned from controllers.
- **Update operations bypass auditing**, so `updatedAt` is set explicitly with `currentDate`.

## Running locally

1. Start MongoDB locally on the default port.
2. Configure `src/main/resources/application.properties`:

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/geofinder
spring.data.mongodb.auto-index-creation=true
```

3. Run the app:

```bash
./mvnw spring-boot:run
```

The API starts on `http://localhost:8080`.

If a geo or text query fails with `IndexNotFound`, the index was not created. Check `auto-index-creation`, or create it manually:

```js
db.places.createIndex({ location: "2dsphere" })
db.delivery_zones.createIndex({ area: "2dsphere" })
db.products.createIndex(
  { name: "text", description: "text", tags: "text" },
  { weights: { name: 5, tags: 3, description: 1 }, name: "TextIndex" })
```

## Roadmap

| Status | Feature |
|---|---|
| ⬜ | Product list by place and availability update |
| ⬜ | Place text search |
| ⬜ | Open-now filter on nearby search (overnight hours and 24x7 places) |
| ⬜ | Collections (favorites) with `$lookup` joins |
| ⬜ | Search history with TTL index and trending aggregation |
| ⬜ | Integration tests with Testcontainers |
| ⬜ | Authentication |

## Known limitations

- Overlapping delivery zones of one place all match; there is no priority rule yet
- Nearby and deliverable searches are separate endpoints
- Distance is straight-line, not road distance
- Ratings are a running average with no per-user review records
- Pagination is limit-based only

---

*This README is updated as features are added.*