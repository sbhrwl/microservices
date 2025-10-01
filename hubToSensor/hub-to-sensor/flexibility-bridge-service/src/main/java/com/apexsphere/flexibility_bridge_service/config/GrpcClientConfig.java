package com.apexsphere.flexibility_bridge_service.config;

import com.apexsphere.storage_service.service.RecordServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {

    // Inject properties from application.yml
    @Value("${grpc.client.storage-service.host:localhost}")
    private String grpcHost;

    @Value("${grpc.client.storage-service.port:9090}")
    private int grpcPort;


    @Bean(destroyMethod = "shutdown") 
    public ManagedChannel storageServiceChannel() {
        return ManagedChannelBuilder.forAddress(grpcHost, grpcPort)
                .usePlaintext() // Equivalent to negotiationType: plaintext
                .build();
    }

    @Bean
    public RecordServiceGrpc.RecordServiceBlockingStub recordServiceBlockingStub(ManagedChannel storageServiceChannel) {
        return RecordServiceGrpc.newBlockingStub(storageServiceChannel);
    }
}