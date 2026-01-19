package com.example.sensor.grpc;

import com.example.sensor.application.NoSensorsFoundException;
import com.example.sensor.application.SensorApplicationService;
import com.example.sensor.application.SensorNotFoundException;
import com.example.sensor.domain.Sensor;
import com.example.sensor.grpc.proto.*;
import com.example.sensor.repository.SensorAlreadyExistsException;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.stream.Collectors;

/**
 * gRPC service adapter - maps between gRPC and application layer.
 * Zero business logic - pure translation.
 */
@GrpcService
public class SensorGrpcService extends SensorServiceGrpc.SensorServiceImplBase {
    
    private static final Logger log = LoggerFactory.getLogger(SensorGrpcService.class);
    
    private final SensorApplicationService applicationService;

    public SensorGrpcService(SensorApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @Override
    public void registerSensor(RegisterSensorRequest request, 
                               StreamObserver<SensorResponse> responseObserver) {
        try {
            Sensor sensor = applicationService.registerSensor(
                request.getSensorId(),
                request.getUserEmail(),
                request.getPostcode()
            );
            
            responseObserver.onNext(toProto(sensor));
            responseObserver.onCompleted();
            
        } catch (SensorAlreadyExistsException e) {
            log.warn("Sensor already exists: {}", e.getMessage());
            responseObserver.onError(Status.ALREADY_EXISTS
                .withDescription(e.getMessage())
                .asRuntimeException());
                
        } catch (IllegalArgumentException e) {
            log.warn("Invalid input: {}", e.getMessage());
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription(e.getMessage())
                .asRuntimeException());
                
        } catch (Exception e) {
            log.error("Internal error during sensor registration", e);
            responseObserver.onError(Status.INTERNAL
                .withDescription("Internal server error")
                .asRuntimeException());
        }
    }

    @Override
    public void getSensor(GetSensorRequest request, 
                         StreamObserver<SensorResponse> responseObserver) {
        try {
            Sensor sensor = applicationService.getSensor(request.getSensorId());
            
            responseObserver.onNext(toProto(sensor));
            responseObserver.onCompleted();
            
        } catch (SensorNotFoundException e) {
            log.warn("Sensor not found: {}", e.getMessage());
            responseObserver.onError(Status.NOT_FOUND
                .withDescription(e.getMessage())
                .asRuntimeException());
                
        } catch (Exception e) {
            log.error("Internal error during sensor retrieval", e);
            responseObserver.onError(Status.INTERNAL
                .withDescription("Internal server error")
                .asRuntimeException());
        }
    }

    @Override
    public void listSensorsByUser(ListSensorsByUserRequest request, 
                                  StreamObserver<ListSensorsResponse> responseObserver) {
        try {
            List<Sensor> sensors = applicationService.listSensorsByUser(request.getUserEmail());
            
            ListSensorsResponse response = ListSensorsResponse.newBuilder()
                .addAllSensors(sensors.stream()
                    .map(this::toProto)
                    .collect(Collectors.toList()))
                .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (NoSensorsFoundException e) {
            log.warn("No sensors found: {}", e.getMessage());
            responseObserver.onError(Status.FAILED_PRECONDITION
                .withDescription(e.getMessage())
                .asRuntimeException());
                
        } catch (Exception e) {
            log.error("Internal error during sensor listing", e);
            responseObserver.onError(Status.INTERNAL
                .withDescription("Internal server error")
                .asRuntimeException());
        }
    }

    @Override
    public void updateSensorPostcode(UpdatePostcodeRequest request, 
                                     StreamObserver<SensorResponse> responseObserver) {
        try {
            Sensor sensor = applicationService.updateSensorPostcode(
                request.getSensorId(),
                request.getNewPostcode()
            );
            
            responseObserver.onNext(toProto(sensor));
            responseObserver.onCompleted();
            
        } catch (SensorNotFoundException e) {
            log.warn("Sensor not found: {}", e.getMessage());
            responseObserver.onError(Status.NOT_FOUND
                .withDescription(e.getMessage())
                .asRuntimeException());
                
        } catch (IllegalArgumentException e) {
            log.warn("Invalid postcode update: {}", e.getMessage());
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription(e.getMessage())
                .asRuntimeException());
                
        } catch (Exception e) {
            log.error("Internal error during postcode update", e);
            responseObserver.onError(Status.INTERNAL
                .withDescription("Internal server error")
                .asRuntimeException());
        }
    }

    /**
     * Convert domain Sensor to proto SensorResponse.
     */
    private SensorResponse toProto(Sensor sensor) {
        return SensorResponse.newBuilder()
            .setSensorId(sensor.getSensorId())
            .setUserEmail(sensor.getUserEmail())
            .setPostcode(sensor.getPostcode())
            .setStatus(sensor.getStatus().name())
            .setRegisteredAt(sensor.getRegisteredAt().toEpochMilli())
            .setLastUpdatedAt(sensor.getLastUpdatedAt().toEpochMilli())
            .build();
    }
}