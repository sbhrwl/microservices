package integration.enterprise.service;

import integration.enterprise.meter_registration.v1.MeterRegistrationPortType;
import integration.enterprise.meter_registration.v1.MeterRegistrationRequest;
import integration.enterprise.meter_registration.v1.MeterRegistrationResponse;
import org.springframework.stereotype.Service;

@Service
public class MeterRegistrationServiceImpl implements MeterRegistrationPortType {

    @Override
    public MeterRegistrationResponse registerMeter(MeterRegistrationRequest request) {

        MeterRegistrationResponse response = new MeterRegistrationResponse();
        response.setStatus("SUCCESS");
        response.setMessage("Meter registered successfully");
        response.setRegistrationId("REG-10001");

        return response;
    }
}