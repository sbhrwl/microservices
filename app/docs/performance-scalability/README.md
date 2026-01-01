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
- Thread pool configuration: configurable via `ApplicationSetting.GrpcServer` or environment variables
- Thread naming: threads named with descriptive prefixes for debugging and monitoring
- Thread pool lifecycle: executors created at application startup, shutdown gracefully on application stop

## Netty boss executor
- Purpose: handles incoming connection acceptance and channel registration for gRPC server
- Default size: `Runtime.getRuntime().availableProcessors()` threads (one per CPU core)
- Configuration: `boss-thread-pool-count` in `grpc-server` config, overridden by environment variable
- Thread naming: `netty-boss-{threadNumber}` for easy identification in thread dumps
- Thread pool type: `FixedThreadPool` with daemon threads for graceful shutdown
- Performance: boss threads handle lightweight connection setup, minimal CPU usage

## Netty worker executor
- Purpose: handles actual request processing, gRPC call execution, and I/O operations
- Default size: `Runtime.getRuntime().availableProcessors() * 5` threads (5x CPU cores)
- Configuration: `worker-thread-pool-count` in `grpc-server` config, overridden by environment variable
- Thread naming: `netty-worker-{threadNumber}` for easy identification in thread dumps
- Thread pool type: `FixedThreadPool` with daemon threads for graceful shutdown
- Performance: worker threads handle request processing, database queries, and business logic execution
- Scaling: higher multiplier (5x) accounts for I/O wait time during database and Dapr calls

## Dapr client executor
- Purpose: handles asynchronous Dapr API calls (state store, pub/sub, service invocation)
- Default size: `5 * Runtime.getRuntime().availableProcessors()` threads (5x CPU cores)
- Configuration: hardcoded in `AppModule`, not configurable via application settings
- Thread naming: `dapr-client-executor-{threadNumber}` for easy identification in thread dumps
- Thread pool type: `FixedThreadPool` with daemon threads for graceful shutdown
- Performance: separate pool prevents Dapr I/O operations from blocking gRPC request processing

## MongoDB search strategies
- Two search strategies: `PRIMARY` (standard MongoDB queries) and `ATLAS` (MongoDB Atlas Search)
- Strategy selection: configured via `MONGODB_SEARCH_STRATEGY` environment variable or `search-strategy` config
- Auto-switching: `PRIMARY` strategy automatically switches to `ATLAS` when free text or autocomplete filters present
- Performance trade-offs: `PRIMARY` faster for indexed queries, `ATLAS` required for full-text search capabilities
- Count queries: always use `PRIMARY` strategy regardless of configuration (optimization for count-only queries)

## PRIMARY strategy
- Implementation: uses MongoDB `$match` aggregation stage with standard BSON filters
- Use cases: equality filters, range filters, date intervals, exists filters, compound filters (AND/OR)
- Performance: leverages MongoDB indexes for fast query execution
- Limitations: does not support free text search or autocomplete functionality
- Query building: `AggregationPipelineBuilder` converts `SearchFilter` to `Filters` (BSON queries)
- Index requirements: requires appropriate indexes on queried fields for optimal performance

## ATLAS strategy
- Implementation: uses MongoDB Atlas Search `$search` aggregation stage with `SearchOperator`
- Use cases: free text search, autocomplete, wildcard search, text-based equality matching
- Performance: requires MongoDB Atlas Search indexes configured in Atlas cluster
- Features: supports compound search operators, wildcard patterns, number ranges, date ranges
- Query building: `AggregationPipelineBuilder` converts `SearchFilter` to `SearchOperator` (Atlas Search queries)
- Infrastructure: requires MongoDB Atlas cluster with Search indexes configured

## Strategy selection
- Configuration: `MONGODB_SEARCH_STRATEGY` environment variable (`PRIMARY` or `ATLAS`)
- Default: `PRIMARY` strategy if not configured
- Auto-switching logic: `resolveSearchStrategy()` method in `AggregationPipelineBuilder`
- Switch conditions: `PRIMARY` → `ATLAS` when `FreeTextFilter` or `AutocompleteFilter` present in query
- Count query optimization: count-only queries (empty field list) always use `PRIMARY` regardless of configuration
- Strategy resolution: determined per-query based on filter types and requested fields

## Caching and performance features
- Service implements caching and latency tracking for performance optimization
- Caching: in-memory cache for frequently accessed organization data
- Latency tracking: rolling average latency calculation for circuit breaker pattern
- Performance monitoring: request duration logging for observability

## Organization cache
- Implementation: Caffeine cache in `OrganizationService` for organization document caching
- Cache configuration: maximum 50 entries, 10 minute TTL (time-to-live) after write
- Cache key: organization code (`orgCode`) used as cache key
- Cache population: lazy loading via `cache.get(key, loader)` on cache miss
- Cache invalidation: cache entry invalidated on organization settings update
- Use case: reduces database queries for frequently accessed organization data (device identifier lookup)
- Cache scope: per-service instance (not shared across instances)

## Moving average latency tracker
- Implementation: Exponential Moving Average (EMA) algorithm with alpha = 0.2
- Purpose: tracks rolling average request latency for latency-based circuit breaker
- Thread safety: uses `AtomicLong` for thread-safe average calculation
- Latency measurement: tracks request duration from interceptor start to completion
- Algorithm: `newAverage = 0.2 * currentLatency + 0.8 * previousAverage` for smooth averaging
- Usage: `LatencyLimiterInterceptor` uses average latency to reject requests when threshold exceeded
- Performance: O(1) time complexity, minimal memory overhead (single `AtomicLong`)

## Deployment topologies
- Service designed for horizontal scaling with stateless architecture
- Stateless design: no in-memory state shared between requests, enables horizontal scaling
- External dependencies: MongoDB (persistence), Keycloak (authentication), Dapr (service mesh)
- Scaling considerations: thread pool sizing, connection pooling, cache invalidation

## Horizontal scaling
- Stateless service: no shared state between service instances, enables unlimited horizontal scaling
- Load balancing: Kubernetes Service or Dapr sidecar handles request distribution across instances
- Session affinity: not required (stateless design, no sticky sessions)
- Cache considerations: organization cache is per-instance, cache warming may be needed after scaling
- Database connections: each instance maintains its own MongoDB connection pool
- Scaling triggers: scale based on CPU utilization, request latency, or custom metrics (e.g., average latency)

## MongoDB connection pooling
- Connection pool: MongoDB Java driver connection pool with configurable size
- Default pool size: `maxPoolSize=250` connections per service instance
- Pool configuration: configured via `MONGODB_OPTIONS` connection string parameter
- Connection lifecycle: connections created on-demand, reused across requests, closed on pool shutdown
- Scaling impact: each service instance maintains separate connection pool (250 connections × N instances)
- Database considerations: MongoDB server must handle total connections from all service instances
- Connection string: `mongodb://{host}/{db}?maxPoolSize=250&connectTimeoutMS=30000&heartbeatFrequencyMS=30000`

## Resource sizing
- CPU: service is CPU-bound for request processing, thread pools sized based on CPU cores
- Memory: heap size depends on request volume, cache size (50 org entries), and connection pool overhead
- Recommended: 2-4 CPU cores, 2-4 GB heap per instance for moderate load
- Thread pool sizing: boss = CPU cores, worker = CPU cores × 5, Dapr = CPU cores × 5
- MongoDB: ensure MongoDB cluster can handle connection pool size × number of instances
- Network: ensure sufficient bandwidth for gRPC requests and MongoDB queries
- Monitoring: monitor thread pool utilization, connection pool usage, and average latency for capacity planning


