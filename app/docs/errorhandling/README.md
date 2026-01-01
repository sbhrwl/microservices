# Error handling and Request limits
- [Exception hierarchy](#exception-hierarchy)
- [Exception types](#exception-types)
- [Interceptor chain](#interceptor-chain)
  - [ExceptionHandlerInterceptor](#exceptionhandlerinterceptor)
  - [LatencyLimiterInterceptor](#latencylimiterinterceptor)
  - [LoggingInterceptor](#logginginterceptor)
- [Request limits](#request-limits)
  - [Maximum message size](#maximum-message-size)
  - [Latency limiter threshold](#latency-limiter-threshold)
  - [Timeouts](#timeouts)

## Exception hierarchy
- All exceptions extend `ServiceException` which extends `RuntimeException`
- Exception-to-gRPC status mapping via `getReturnStatus()` method in each exception class
- Exception translation happens in `ExceptionHandlerInterceptor` before response sent to client
- Exception hierarchy allows type-safe error handling with automatic gRPC status code mapping
- Base exception: `ServiceException` maps to `Status.INTERNAL` (default for unhandled exceptions)

## Exception types
- **ServiceException**: Base exception class, maps to `Status.INTERNAL`, supports formatted messages and cause chaining
- **InvalidRequestException**: Invalid request parameters or malformed data, maps to `Status.INVALID_ARGUMENT`, supports path field for field-level errors
- **AccessDeniedException**: Insufficient permissions for operation, maps to `Status.PERMISSION_DENIED`, thrown by authorization checks
- **AuthenticationException**: Missing or invalid authentication credentials, maps to `Status.UNAUTHENTICATED`, thrown by token validation
- **ResourceNotFoundException**: Requested resource does not exist, maps to `Status.NOT_FOUND`, used for missing devices, events, organizations
- **ResourceAlreadyExistsException**: Resource creation conflict, maps to `Status.ALREADY_EXISTS`, used for duplicate resource creation
- **TooManyRequestsException**: Rate limiting or quota exceeded, maps to `Status.RESOURCE_EXHAUSTED`, used for throttling
- **RequestTooLargeException**: Request payload exceeds size limits, maps to `Status.INVALID_ARGUMENT`, used for oversized requests (e.g., tag operations > 1000)
- **HighLatencyException**: Request rejected due to high average latency, maps to `Status.RESOURCE_EXHAUSTED`, thrown by latency limiter

## Interceptor chain
- Interceptors applied in order: `LatencyLimiterInterceptor` → `AuthorizationInterceptor` → `LoggingInterceptor` → `ExceptionHandlerInterceptor`
- Interceptor execution: outer interceptors wrap inner interceptors, exception handler is innermost (closest to service)
- Exception propagation: exceptions bubble up through interceptor chain, caught by `ExceptionHandlerInterceptor`
- Health check bypass: Dapr health checks only use `LatencyLimiterInterceptor`, other interceptors skipped
- Revision service bypass: `RevisionServiceImpl` has no interceptors (public information, no auth required)

### ExceptionHandlerInterceptor
- Catches all exceptions thrown during request processing (interceptors and service implementations)
- Exception translation: converts domain exceptions to gRPC `Status` codes via `asGrpcStatus()` method
- Exception handling: wraps `ServerCall.Listener` to catch exceptions in `onMessage()` and `onHalfClose()` callbacks
- Status code mapping: `ServiceException.getReturnStatus()` provides exception-specific gRPC status codes
- Special handling: `CompletionException` unwrapped to handle underlying cause, `StatusRuntimeException` preserved
- Default mapping: unknown exceptions mapped to `Status.INTERNAL` with generic error message
- Logging: exceptions logged at appropriate levels (ERROR for unexpected, WARN for client errors)

### LatencyLimiterInterceptor
- Protects service from overload by rejecting requests when average latency exceeds threshold
- Latency tracking: uses `MovingAverageLatencyTracker` with Exponential Moving Average (EMA) algorithm (alpha = 0.2)
- Request rejection: throws `HighLatencyException` if average latency > threshold (default: 10000 ms)
- Health check bypass: Dapr health checks excluded from latency limiting (prevents health check failures)
- Latency measurement: tracks request duration from interceptor start to `onComplete()` callback
- Warning threshold: logs WARN when average latency > 80% of threshold (early warning for degradation)
- Thread-safe: `MovingAverageLatencyTracker` uses `AtomicLong` for thread-safe average calculation

### LoggingInterceptor
- Logs all gRPC requests with method name and duration for observability
- Request logging: logs at DEBUG level with method name on request start
- Duration logging: logs request duration on completion with human-readable time units (millis, sec, min, hours)
- Time formatting: `format()` method converts milliseconds to appropriate time unit with 2 decimal precision
- Performance impact: minimal overhead, uses `System.currentTimeMillis()` for timing
- Log level: DEBUG level logging (enabled in development, disabled in production by default)

## Request limits
- Service enforces limits to prevent resource exhaustion and ensure stability
- Limits configurable via `ApplicationSetting.GrpcServer` configuration
- Limits apply per-request basis, no global rate limiting (handled by infrastructure)
- Limit violations result in immediate request rejection with appropriate error status

### Maximum message size
- Default limit: `8 MiB` (8388608 bytes) for inbound gRPC messages
- Configuration: `maxInboundMessageSize` in `grpc-server` config, overridden by `MAX_INBOUND_MESSAGE_SIZE` environment variable
- Enforcement: gRPC framework enforces limit at protocol level, oversized messages rejected before reaching service
- Error handling: gRPC returns `Status.RESOURCE_EXHAUSTED` for messages exceeding limit
- Use case: prevents memory exhaustion from malicious or malformed large requests

### Latency limiter threshold
- Default threshold: `10000 ms` (10 seconds) for average request latency
- Configuration: `latency-limiter-threshold` in `grpc-server` config, overridden by `LATENCY_LIMITER_THRESHOLD_MILLIS`
- Algorithm: Exponential Moving Average (EMA) with alpha = 0.2 for rolling average calculation
- Enforcement: requests rejected if current average latency > threshold (except health checks)
- Warning threshold: WARN logged when average latency > 80% of threshold (8000 ms at default)
- Purpose: circuit breaker pattern to prevent cascading failures during high load

### Timeouts
| Context             | Timeout Type                                   | Default                                                       |
| ------------------- | ---------------------------------------------- | ------------------------------------------------------------- |
| gRPC client         | Client-side request timeout                    | Not enforced by server; clients must set appropriate timeouts |
| MongoDB             | `connectTimeoutMS`                             | 30000 ms (connection timeout)                                 |
| MongoDB             | `heartbeatFrequencyMS`                         | For connection monitoring                                     |
| Dapr API            | `DAPR_API_TIMEOUT_MILLISECONDS` (env variable) | 30000 ms (client call timeout)                                |
| Graceful shutdown   | In-flight request wait                         | 5 seconds                                                     |
| Graceful shutdown   | Forced shutdown wait                           | 2 seconds                                                     |
| No request timeout  | Server-side per-request timeout                | Not enforced; relies on client timeouts and latency limiter   |
