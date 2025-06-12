package com.example.hubservice.config;

import com.example.hubservice.service.RegistrationServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PreDestroy; // Use jakarta.annotation for Spring Boot 3+

/**
 * GrpcServerConfig is a Spring configuration class responsible for setting up and
 * managing the lifecycle of the gRPC server within the Hub Service.
 */
@Configuration
public class GrpcServerConfig {

    private static final Logger logger = LoggerFactory.getLogger(GrpcServerConfig.class);

    @Value("${grpc.server.port}")
    private int grpcServerPort;

    @Autowired
    private RegistrationServiceImpl registrationService; // Autowire the gRPC service implementation

    private Server grpcServer; // Hold a reference to the gRPC server

    /**
     * Configures and starts the gRPC server as a Spring Bean.
     * The server will bind to the port defined in application.properties and register
     * our RegistrationServiceImpl to handle incoming gRPC calls.
     *
     * @return The configured and started gRPC Server instance.
     * @throws Exception if the server fails to start.
     */
    @Bean
    public Server grpcServer() throws Exception {
        logger.info("Starting gRPC server on port {}", grpcServerPort);
        // Build the gRPC server, binding it to the specified port
        grpcServer = ServerBuilder.forPort(grpcServerPort)
                .addService(registrationService) // Register our service implementation
                .build()
                .start(); // Start the server

        // Add a shutdown hook to ensure the server is properly shut down when the JVM exits
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down gRPC server due to JVM shutdown hook.");
            if (grpcServer != null) {
                grpcServer.shutdown();
                try {
                    // Wait for the server to terminate gracefully
                    grpcServer.awaitTermination();
                } catch (InterruptedException e) {
                    logger.error("gRPC server shutdown interrupted.", e);
                    Thread.currentThread().interrupt();
                }
            }
            logger.info("gRPC server shut down.");
        }));

        logger.info("gRPC server started successfully.");
        return grpcServer;
    }

    /**
     * Ensures the gRPC server is shut down when the Spring application context closes.
     */
    @PreDestroy
    public void shutdownGrpcServer() {
        if (grpcServer != null) {
            logger.info("Shutting down gRPC server via @PreDestroy.");
            grpcServer.shutdown();
            try {
                grpcServer.awaitTermination();
            } catch (InterruptedException e) {
                logger.error("gRPC server shutdown interrupted.", e);
                Thread.currentThread().interrupt();
            }
        }
    }
}
