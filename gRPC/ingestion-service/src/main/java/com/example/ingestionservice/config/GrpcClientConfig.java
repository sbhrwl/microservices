package com.example.ingestionservice.config;

import com.example.ingestionservice.proto.RegistrationServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * GrpcClientConfig is a Spring configuration class that sets up and provides
 * the necessary gRPC client components (ManagedChannel and service stub)
 * for connecting to the Hub Service.
 */
@Configuration
public class GrpcClientConfig {

    @Value("${grpc.hub.host}")
    private String grpcHubHost;

    @Value("${grpc.hub.port}")
    private int grpcHubPort;

    /**
     * Configures and provides a ManagedChannel bean.
     * The ManagedChannel represents a connection to a gRPC server.
     * It's recommended to reuse a single channel for multiple gRPC calls.
     *
     * @return A configured ManagedChannel instance.
     */
    @Bean
    public ManagedChannel managedChannel() {
        // For production, consider using .useTransportSecurity() for SSL/TLS
        // For development, .usePlaintext() is often used for simplicity.
        return ManagedChannelBuilder.forAddress(grpcHubHost, grpcHubPort)
                .usePlaintext() // DO NOT use in production without proper security!
                .build();
    }

    /**
     * Configures and provides a blocking gRPC stub for the RegistrationService.
     * This stub allows making synchronous gRPC calls to the Hub Service.
     *
     * @param managedChannel The ManagedChannel bean to use for communication.
     * @return A blocking stub for the RegistrationService.
     */
    @Bean
    public RegistrationServiceGrpc.RegistrationServiceBlockingStub registrationServiceBlockingStub(ManagedChannel managedChannel) {
        return RegistrationServiceGrpc.newBlockingStub(managedChannel);
    }
}
