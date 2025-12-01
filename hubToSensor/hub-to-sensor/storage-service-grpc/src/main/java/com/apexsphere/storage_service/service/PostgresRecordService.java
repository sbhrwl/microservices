package com.apexsphere.storage_service.service.postgres;

import com.apexsphere.storage_service.postgres.model.ControlRequestEntity;
import com.apexsphere.storage_service.postgres.model.RequestChangeLogEntity;
import com.apexsphere.storage_service.postgres.repository.ControlRequestRepository;
import com.apexsphere.storage_service.postgres.repository.RequestChangeLogJPARepository;
import com.apexsphere.storage_service.service.RecordRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostgresRecordService {

    private static final Logger log = LoggerFactory.getLogger(PostgresRecordService.class);

    private final ControlRequestRepository controlRequestRepository;
    private final RequestChangeLogJPARepository changeLogJPARepository;

    public PostgresRecordService(ControlRequestRepository controlRequestRepository,
                                 RequestChangeLogJPARepository changeLogJPARepository) {
        this.controlRequestRepository = controlRequestRepository;
        this.changeLogJPARepository = changeLogJPARepository;
    }

    @Transactional
    public RecordRequest save(RecordRequest request) {
        log.info("[PostgresRecordService] Saving new record");

        ControlRequestEntity entity = new ControlRequestEntity(
                request.getSensorId(),
                request.getOperation(),
                request.getRelayNumber(),
                request.getDuration(),
                request.getStatus()
        );

        ControlRequestEntity saved = controlRequestRepository.save(entity);
        log.info("[PostgresRecordService] Saved ControlRequest with ID {}", saved.getId());

        RequestChangeLogEntity logEntity = new RequestChangeLogEntity("Control Requested");
        logEntity.setControlRequest(saved);
        changeLogJPARepository.save(logEntity);

        return RecordRequest.newBuilder()
                .setRecordId(String.valueOf(saved.getId()))
                .setSensorId(saved.getSensorId())
                .setOperation(saved.getOperation())
                .setRelayNumber(saved.getRelayNumber())
                .setDuration(saved.getDuration())
                .setStatus(saved.getStatus())
                .build();
    }

    @Transactional
    public RecordRequest update(RecordRequest request) {
        log.info("[PostgresRecordService] Updating record {}", request.getRecordId());

        Long id = Long.parseLong(request.getRecordId());
        ControlRequestEntity entity = controlRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found for ID " + id));

        entity.setStatus(request.getStatus());
        ControlRequestEntity saved = controlRequestRepository.save(entity);

        RequestChangeLogEntity logEntity = new RequestChangeLogEntity(request.getStatus());
        logEntity.setControlRequest(saved);
        changeLogJPARepository.save(logEntity);

        return RecordRequest.newBuilder()
                .setRecordId(String.valueOf(saved.getId()))
                .setSensorId(saved.getSensorId())  // important!
                .setOperation(saved.getOperation())
                .setRelayNumber(saved.getRelayNumber())
                .setDuration(saved.getDuration())
                .setStatus(saved.getStatus())
                .build();
    }
}