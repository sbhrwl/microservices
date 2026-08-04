package integration.enterprise.service;

import integration.enterprise.meter_registration.v1.MeterRegistrationRequest;
import integration.enterprise.meter_registration.v1.MeterRegistrationResponse;

public interface MeterRegistrationProcessor {

    MeterRegistrationResponse register(MeterRegistrationRequest request);

}