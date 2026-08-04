package integration.enterprise.service;

import integration.enterprise.meter_registration.v1.MeterRegistrationRequest;
import integration.enterprise.meter_registration.v1.MeterRegistrationResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MeterRegistrationProcessorImpl
        implements MeterRegistrationProcessor {

    @Override
    public MeterRegistrationResponse register(
            MeterRegistrationRequest request) {

        MeterRegistrationResponse response =
                new MeterRegistrationResponse();

        response.setStatus("SUCCESS");
        response.setMessage("Meter registered successfully");
        response.setRegistrationId(UUID.randomUUID().toString());

        return response;
    }
}