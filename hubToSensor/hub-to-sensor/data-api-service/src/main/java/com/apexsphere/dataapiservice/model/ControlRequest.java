package com.apexsphere.dataapiservice.model;

import jakarta.persistence.*;
import java.util.List;

// This import is necessary to resolve the 'cannot find symbol' error for List<RequestChangeLog>
import com.apexsphere.dataapiservice.model.RequestChangeLog; 

@Entity
@Table(name = "control_requests")
public class ControlRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Maps to 'id'

    private Integer duration; // Maps to 'duration'
    private String operation; // Maps to 'operation'

    @Column(name = "relay_number")
    private Integer relayNumber; // Maps to 'relay_number'

    @Column(name = "sensor_id")
    private String sensorId; // Maps to 'sensor_id'

    private String status; // Maps to 'status'

    // API 3 (Request Tracker): One ControlRequest has Many RequestChangeLogs
    @OneToMany(mappedBy = "controlRequest", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RequestChangeLog> changeLogs; 

    // --- Constructors ---

    public ControlRequest() {
    }

    public ControlRequest(Long id, Integer duration, String operation, Integer relayNumber, String sensorId, String status, List<RequestChangeLog> changeLogs) {
        this.id = id;
        this.duration = duration;
        this.operation = operation;
        this.relayNumber = relayNumber;
        this.sensorId = sensorId;
        this.status = status;
        this.changeLogs = changeLogs;
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Integer getRelayNumber() {
        return relayNumber;
    }

    public void setRelayNumber(Integer relayNumber) {
        this.relayNumber = relayNumber;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<RequestChangeLog> getChangeLogs() {
        return changeLogs;
    }

    public void setChangeLogs(List<RequestChangeLog> changeLogs) {
        this.changeLogs = changeLogs;
    }
}
