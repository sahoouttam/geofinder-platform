# geofinder-platform

Geo and full-text place discovery API built with Spring Boot and MongoDB (`MongoTemplate`).

Find places near a point, check which places deliver to a location, and search products by text and distance.
The project is a hands-on way to learn advanced `MongoTemplate` usage: geospatial queries, aggregation, full-text search, atomic updates and custom repositories.

## Tech stack

- Java 21, Spring Boot 3.3.5, Spring Web, Spring Validation
- Spring Data MongoDB (`MongoTemplate`, custom repository fragments)
- MongoDB (local)
- Lombok, Maven

## Features so far

**Places**
- Create a place with type, tags, address, location, operating hours and attributes
- Nearby search with `geoNear`: radius in km, optional type filter, optional open-at-time filter, limit, distance returned in km
- Add and remove tags in one request (`addToSet` and `pullAll`, tags normalized)
- Atomic running-average rating update using an aggregation-pipeline update
- Open-now search at any given time, including overnight shifts that run past midnight and 24x7 places (`$expr` with `$filter` and `$dateToString`)
- Full-text place search over name, tags and description with relevance ranking (weights 5, 3, 1)

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
- List the products of a place with optional category, veg and availability filters (dynamic `Criteria`, sorted by category and name)
- Mark a product available or unavailable (atomic `findAndModify` that returns the updated document)

**Cross-cutting**
- Request validation and a global exception handler with a consistent JSON error body
- `LocalTime` stored as `"HH:mm"` strings through custom converters
- Auditing fields (`createdAt`, `updatedAt`) and optimistic locking (`@Version`)

## API

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/places` | Create a place |
| GET | `/api/places/nearby?latitude=&longitude=&radiusKm=&placeType=&openAt=&limit=` | Nearby places, nearest first; `openAt` keeps only places open at that local date-time |
| GET | `/api/places/open-now?at=` | Places open at the given local date-time (defaults to now, IST) |
| GET | `/api/places/search?text=&limit=` | Full-text place search |
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
| GET | `/api/products/places/{placeId}?category=&veg=&available=&limit=` | Products of a place, filters optional |
| PATCH | `/api/products/{id}/availability?available=` | Mark a product available or unavailable |

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

The API starts on `http://localhost:8080`. Swagger UI: `http://localhost:8080/swagger-ui.html`.

If a geo or text query fails with `IndexNotFound`, the index was not created. Check `auto-index-creation`, or create it manually:

```js
db.places.createIndex({ location: "2dsphere" })
db.delivery_zones.createIndex({ area: "2dsphere" })
db.products.createIndex(
  { name: "text", description: "text", tags: "text" },
  { weights: { name: 5, tags: 3, description: 1 }, name: "TextIndex" })
```

---

*This README is updated as features are added.*