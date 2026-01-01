# Performance and Scalability
- [Threading model](#threading-model)
  - [Netty boss executor](#netty-boss-executor)
  - [Netty worker executor](#netty-worker-executor)
  - [Dapr client executor](#dapr-client-executor)
- [MongoDB search strategies](#mongodb-search-strategies)
  - [PRIMARY strategy](#primary-strategy)
  - [ATLAS strategy](#atlas-strategy)
  - [Strategy selection](#strategy-selection)
- [Caching and performance features](#caching-and-performance-features)
  - [Organization cache](#organization-cache)
  - [Moving average latency tracker](#moving-average-latency-tracker)
- [Deployment topologies](#deployment-topologies)
  - [Horizontal scaling](#horizontal-scaling)
  - [MongoDB connection pooling](#mongodb-connection-pooling)
  - [Resource sizing](#resource-sizing)

## Threading model
- Service uses separate thread pools for different components to optimize resource utilization
- Thread pools sized based on CPU cores for optimal performance on multi-core systems

| Aspect                    | Details                                                                           |
| ------------------------- | --------------------------------------------------------------------------------- |
| Thread pool configuration | Configurable via `ApplicationSetting.GrpcServer` or environment variables         |
| Thread naming             | Threads named with descriptive prefixes for debugging and monitoring              |
| Thread pool lifecycle     | Executors created at application startup, shutdown gracefully on application stop |

### Netty boss executor

| Aspect           | Details                                                                              |
| ---------------- | ------------------------------------------------------------------------------------ |
| Purpose          | Handles `incoming connection acceptance` and `channel registration` for gRPC server      |
| Default size     | `Runtime.getRuntime().availableProcessors()` threads (one per CPU core)              |
| Configuration    | `boss-thread-pool-count` in `grpc-server` config, overridden by environment variable |
| Thread naming    | `netty-boss-{threadNumber}` for easy identification in thread dumps                  |
| Thread pool type | `FixedThreadPool` with daemon threads for graceful shutdown                          |
| Performance      | Boss threads handle lightweight connection setup, minimal CPU usage                  |

### Netty worker executor

| Aspect           | Details                                                                                  |
| ---------------- | ---------------------------------------------------------------------------------------- |
| Purpose          | Handles `actual request processing`, `gRPC call execution`, and `I/O operations`         |
| Default size     | `Runtime.getRuntime().availableProcessors() * 5` threads (5x CPU cores)                  |
| Configuration    | `worker-thread-pool-count` in `grpc-server` config, overridden by environment variable   |
| Thread naming    | `netty-worker-{threadNumber}` for easy identification in thread dumps                    |
| Thread pool type | `FixedThreadPool` with daemon threads for graceful shutdown                              |
| Performance      | Worker threads handle request processing, database queries, and business logic execution |
| Scaling          | Higher multiplier (5x) accounts for I/O wait time during database and Dapr calls         |

### Dapr client executor

| Aspect           | Details                                                                          |
| ---------------- | -------------------------------------------------------------------------------- |
| Purpose          | Handles asynchronous Dapr API calls (state store, pub/sub, service invocation)   |
| Default size     | `5 * Runtime.getRuntime().availableProcessors()` threads (5x CPU cores)          |
| Configuration    | Hardcoded in `AppModule`, not configurable via application settings              |
| Thread naming    | `dapr-client-executor-{threadNumber}` for easy identification in thread dumps    |
| Thread pool type | `FixedThreadPool` with daemon threads for graceful shutdown                      |
| Performance      | Separate pool prevents Dapr I/O operations from blocking gRPC request processing |

## MongoDB search strategies

| Aspect                 | Details                                                                                          |
| ---------------------- | ------------------------------------------------------------------------------------------------ |
| Search strategies      | `PRIMARY`: standard MongoDB queries<br>`ATLAS`: MongoDB Atlas Search (full text search)          |
| Strategy selection     | Configured via `MONGODB_SEARCH_STRATEGY` environment variable or `search-strategy` config        |
| Auto-switching         | `PRIMARY` automatically switches to `ATLAS` when free text or autocomplete filters are present   |
| Performance trade-offs | `PRIMARY` is faster for indexed queries<br>`ATLAS` is required for full-text search capabilities |
| Count queries          | Always use `PRIMARY` strategy regardless of configuration (optimized for count-only queries)     |

### PRIMARY strategy

| Aspect             | Details                                                                                    |
| ------------------ | ------------------------------------------------------------------------------------------ |
| Implementation     | Uses MongoDB `$match` aggregation stage with standard BSON filters                         |
| Use cases          | Equality filters, range filters, date intervals, exists filters, compound filters (AND/OR) |
| Performance        | Leverages MongoDB indexes for fast query execution                                         |
| Limitations        | Does not support free text search or autocomplete functionality                            |
| Query building     | `AggregationPipelineBuilder` converts `SearchFilter` to `Filters` (BSON queries), BSON is the binary version of JSON that MongoDB uses internally, and `$match` filters operate on it.       |
| Index requirements | Requires appropriate indexes on queried fields for optimal performance                     |

### ATLAS strategy

| Aspect         | Details                                                                                         |
| -------------- | ----------------------------------------------------------------------------------------------- |
| Implementation | Uses MongoDB Atlas Search `$search` aggregation stage with `SearchOperator`                     |
| Use cases      | Free text search, autocomplete, wildcard search, text-based equality matching                   |
| Performance    | Requires MongoDB Atlas Search indexes configured in the Atlas cluster                           |
| Features       | Supports compound search operators, wildcard patterns, number ranges, date ranges               |
| Query building | `AggregationPipelineBuilder` converts `SearchFilter` to `SearchOperator` (Atlas Search queries) |
| Infrastructure | Requires MongoDB Atlas cluster with Search indexes configured                                   |

### Strategy selection

| Aspect                   | Details                                                                                |
| ------------------------ | -------------------------------------------------------------------------------------- |
| Configuration            | `MONGODB_SEARCH_STRATEGY` environment variable (`PRIMARY` or `ATLAS`)                  |
| Default                  | `PRIMARY` strategy if not configured                                                   |
| Auto-switching logic     | `resolveSearchStrategy()` method in `AggregationPipelineBuilder`                       |
| Switch conditions        | `PRIMARY` → `ATLAS` when `FreeTextFilter` or `AutocompleteFilter` is present           |
| Count query optimization | Count-only queries (empty field list) always use `PRIMARY` regardless of configuration |
| Strategy resolution      | Determined per-query based on filter types and requested fields                        |

## Caching and performance features
- Service implements caching and latency tracking for performance optimization

| Aspect                 | Details                                                         |
| ---------------------- | --------------------------------------------------------------- |
| Caching                | In-memory cache for frequently accessed organization data       |
| Latency tracking       | Rolling average latency calculation for circuit breaker pattern |
| Performance monitoring | Request duration logging for observability                      |

### Organization cache

| Aspect              | Details                                                                                       |
| ------------------- | --------------------------------------------------------------------------------------------- |
| Implementation      | Caffeine cache in `OrganizationService` for organization document caching                     |
| Cache configuration | Maximum 50 entries, 10 minute TTL (time-to-live) after write                                  |
| Cache key           | Organization code (`orgCode`)                                                                 |
| Cache population    | Lazy loading via `cache.get(key, loader)` on cache miss                                       |
| Cache invalidation  | Cache entry invalidated on organization settings update                                       |
| Use case            | Reduces database queries for frequently accessed organization data (device identifier lookup) |
| Cache scope         | Per-service instance (not shared across instances)                                            |

### Moving average latency tracker

| Aspect              | Details                                                                                        |
| ------------------- | ---------------------------------------------------------------------------------------------- |
| Implementation      | Exponential Moving Average (EMA) algorithm with alpha = 0.2                                    |
| Purpose             | Tracks rolling average request latency for latency-based circuit breaker                       |
| Thread safety       | Uses `AtomicLong` for thread-safe average calculation                                          |
| Latency measurement | Tracks request duration from interceptor start to completion                                   |
| Algorithm           | `newAverage = 0.2 * currentLatency + 0.8 * previousAverage` for smooth averaging               |
| Usage               | `LatencyLimiterInterceptor` uses average latency to reject requests when threshold is exceeded |
| Performance         | O(1) time complexity, minimal memory overhead (single `AtomicLong`)                            |

## Deployment topologies
- Service designed for horizontal scaling with `stateless architecture`

| Aspect                 | Details                                                                |
| ---------------------- | ---------------------------------------------------------------------- |
| Stateless design       | No in-memory state shared between requests, enables horizontal scaling |
| External dependencies  | MongoDB (persistence), Keycloak (authentication), Dapr (service mesh)  |
| Scaling considerations | `Thread pool sizing`, `connection pooling`, `cache invalidation`       |

## Horizontal scaling

| Aspect               | Details                                                                                    |
| -------------------- | ------------------------------------------------------------------------------------------ |
| Stateless service    | No shared state between service instances, enables unlimited horizontal scaling            |
| Load balancing       | Kubernetes Service or Dapr sidecar handles request distribution across instances           |
| Session affinity     | Not required (stateless design, no sticky sessions)                                        |
| Cache considerations | Organization cache is per-instance; cache warming may be needed after scaling              |
| Database connections | Each instance maintains its own MongoDB connection pool                                    |
| Scaling triggers     | Scale based on CPU utilization, request latency, or custom metrics (e.g., average latency) |

## MongoDB connection pooling

| Aspect                  | Details                                                                                    |
| ----------------------- | ------------------------------------------------------------------------------------------ |
| Connection pool         | MongoDB Java driver connection pool with configurable size                                 |
| Default pool size       | `maxPoolSize=250` connections per service instance                                         |
| Pool configuration      | Configured via `MONGODB_OPTIONS` connection string parameter                               |
| Connection lifecycle    | Connections created on-demand, reused across requests, closed on pool shutdown             |
| Scaling impact          | Each service instance maintains a separate connection pool (250 connections × N instances) |
| Database considerations | MongoDB server must handle total connections from all service instances                    |
| Connection string       | `mongodb://{host}/{db}?maxPoolSize=250&connectTimeoutMS=30000&heartbeatFrequencyMS=30000`  |

## Resource sizing

| Aspect             | Details                                                                                           |
| ------------------ | ------------------------------------------------------------------------------------------------- |
| CPU                | Service is CPU-bound for request processing; thread pools sized based on CPU cores                |
| Memory             | Heap size depends on request volume, cache size (50 org entries), and connection pool overhead    |
| Recommended        | 2-4 CPU cores, 2-4 GB heap per instance for moderate load                                         |
| Thread pool sizing | Boss = CPU cores, Worker = CPU cores × 5, Dapr = CPU cores × 5                                    |
| MongoDB            | Ensure MongoDB cluster can handle connection pool size × number of instances                      |
| Network            | Ensure sufficient bandwidth for gRPC requests and MongoDB queries                                 |
| Monitoring         | Monitor thread pool utilization, connection pool usage, and average latency for capacity planning |
