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
- `Exception-to-gRPC status mapping` via `getReturnStatus()` method in each exception class
- `Exception translation` happens in **`ExceptionHandlerInterceptor`** before response sent to client
- `Exception hierarchy` allows type-safe error handling with automatic gRPC status code mapping
- Base exception: **`ServiceException`** maps to `Status.INTERNAL` (default for unhandled exceptions)

## Exception types

| Exception Class                | gRPC Status Code            | Description / Use Case                                                    |
| ------------------------------ | --------------------------- | ------------------------------------------------------------------------- |
| ServiceException               | `Status.INTERNAL`           | Base exception class, supports formatted messages and cause chaining      |
| InvalidRequestException        | `Status.INVALID_ARGUMENT`   | Invalid request parameters or malformed data; supports path field errors  |
| AccessDeniedException          | `Status.PERMISSION_DENIED`  | Insufficient permissions for operation; thrown by authorization checks    |
| AuthenticationException        | `Status.UNAUTHENTICATED`    | Missing or invalid authentication credentials; thrown by token validation |
| ResourceNotFoundException      | `Status.NOT_FOUND`          | Requested resource does not exist; used for missing devices, events, orgs |
| ResourceAlreadyExistsException | `Status.ALREADY_EXISTS`     | Resource creation conflict; used for duplicate resource creation          |
| TooManyRequestsException       | `Status.RESOURCE_EXHAUSTED` | Rate limiting or quota exceeded; used for throttling                      |
| RequestTooLargeException       | `Status.INVALID_ARGUMENT`   | Request payload exceeds size limits; e.g., tag operations > 1000          |
| HighLatencyException           | `Status.RESOURCE_EXHAUSTED` | Request rejected due to high average latency; thrown by latency limiter   |

## Interceptor chain

| Aspect                  | Details                                                                                                         |
| ----------------------- | --------------------------------------------------------------------------------------------------------------- |
| Interceptor order       | `LatencyLimiterInterceptor` → `AuthorizationInterceptor` → `LoggingInterceptor` → `ExceptionHandlerInterceptor` |
| Interceptor execution   | Outer interceptors wrap inner interceptors; exception handler is innermost (closest to service)                 |
| Exception propagation   | Exceptions bubble up through interceptor chain, caught by `ExceptionHandlerInterceptor`                         |
| Health check bypass     | Dapr health checks only use `LatencyLimiterInterceptor`; other interceptors skipped                             |
| Revision service bypass | `RevisionServiceImpl` has no interceptors (public information, no auth required)                                |

### ExceptionHandlerInterceptor
- Catches all exceptions thrown during request processing (interceptors and service implementations)

| Aspect                | Details                                                                                        |
| --------------------- | ---------------------------------------------------------------------------------------------- |
| Exception translation | Converts domain exceptions to gRPC `Status` codes via `asGrpcStatus()` method                  |
| Exception handling    | Wraps `ServerCall.Listener` to catch exceptions in `onMessage()` and `onHalfClose()` callbacks |
| Status code mapping   | `ServiceException.getReturnStatus()` provides exception-specific gRPC status codes             |
| Special handling      | `CompletionException` unwrapped to handle underlying cause; `StatusRuntimeException` preserved |
| Default mapping       | Unknown exceptions mapped to `Status.INTERNAL` with generic error message                      |
| Logging               | Exceptions logged at appropriate levels (ERROR for unexpected, WARN for client errors)         |

### LatencyLimiterInterceptor
- Protects service from overload by rejecting requests when average latency exceeds threshold

| Aspect              | Details                                                                                          |
| ------------------- | ------------------------------------------------------------------------------------------------ |
| Latency tracking    | Uses `MovingAverageLatencyTracker` with Exponential Moving Average (EMA) algorithm (alpha = 0.2) |
| Request rejection   | Throws `HighLatencyException` if average latency > threshold (default: 10000 ms)                 |
| Health check bypass | Dapr health checks excluded from latency limiting (prevents health check failures)               |
| Latency measurement | Tracks request duration from interceptor start to `onComplete()` callback                        |
| Warning threshold   | Logs WARN when average latency > 80% of threshold (early warning for degradation)                |
| Thread-safe         | `MovingAverageLatencyTracker` uses `AtomicLong` for thread-safe average calculation              |

### LoggingInterceptor
- Logs all gRPC requests with method name and duration for observability

| Aspect             | Details                                                                                   |
| ------------------ | ----------------------------------------------------------------------------------------- |
| Observability      | Logs all gRPC requests with method name and duration                                      |
| Request logging    | Logs at DEBUG level with method name on request start                                     |
| Duration logging   | Logs request duration on completion with human-readable time units (ms, s, min, h)        |
| Time formatting    | `format()` method converts milliseconds to appropriate time unit with 2 decimal precision |
| Performance impact | Minimal overhead; uses `System.currentTimeMillis()` for timing                            |
| Log level          | DEBUG level logging (enabled in development, disabled in production by default)           |

## Request limits
- Service enforces limits to prevent resource exhaustion and ensure stability
- Limits configurable via `ApplicationSetting.GrpcServer` configuration
- Limits apply `per-request basis`, no global rate limiting (handled by infrastructure)
- Limit violations result in immediate request rejection with appropriate error status

### Maximum message size

| Aspect         | Details                                                                                                |
| -------------- | ------------------------------------------------------------------------------------------------------ |
| Default limit  | 8 MiB (8388608 bytes) for inbound gRPC messages                                                        |
| Configuration  | `maxInboundMessageSize` in `grpc-server` config, overridden by `MAX_INBOUND_MESSAGE_SIZE` env variable |
| Enforcement    | gRPC enforces limit at protocol level; oversized messages rejected before reaching service             |
| Error handling | gRPC returns `Status.RESOURCE_EXHAUSTED` for messages exceeding limit                                  |
| Use case       | Prevents memory exhaustion from malicious or malformed large requests                                  |

### Latency limiter threshold

| Aspect            | Details                                                                                               |
| ----------------- | ----------------------------------------------------------------------------------------------------- |
| Default threshold | 10000 ms (10 seconds) for average request latency                                                     |
| Configuration     | `latency-limiter-threshold` in `grpc-server` config, overridden by `LATENCY_LIMITER_THRESHOLD_MILLIS` |
| Algorithm         | Exponential Moving Average (EMA) with alpha = 0.2 for rolling average calculation                     |
| Enforcement       | Requests rejected if current average latency > threshold (except health checks)                       |
| Warning threshold | WARN logged when average latency > 80% of threshold (8000 ms at default)                              |
| Purpose           | Circuit breaker pattern to prevent cascading failures during high load                                |

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
