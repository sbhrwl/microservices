package integration.enterprise.service;

import integration.enterprise.meter_registration.v1.MeterRegistrationPortType;
import integration.enterprise.meter_registration.v1.MeterRegistrationRequest;
import integration.enterprise.meter_registration.v1.MeterRegistrationResponse;
import jakarta.jws.WebService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@WebService(
        serviceName = "MeterRegistrationService",
        portName = "MeterRegistrationPort",
        targetNamespace = "http://enterprise.integration/meter-registration/v1",
        endpointInterface = "integration.enterprise.meter_registration.v1.MeterRegistrationPortType"
)
public class MeterRegistrationServiceImpl implements MeterRegistrationPortType {

    private static final Logger log =
            LoggerFactory.getLogger(MeterRegistrationServiceImpl.class);

    private final MeterRegistrationProcessor processor;

    public MeterRegistrationServiceImpl(MeterRegistrationProcessor processor) {
        this.processor = processor;
    }

    @Override
    public MeterRegistrationResponse registerMeter(MeterRegistrationRequest request) {

        log.info("========== SOAP REQUEST RECEIVED ==========");
        log.info("GSRN              : {}", request.getGsrn());
        log.info("Meter Serial      : {}", request.getMeterSerialNumber());
        log.info("Customer ID       : {}", request.getCustomerId());
        log.info("Relay Number      : {}", request.getRelayNumber());
        log.info("Relay State       : {}", request.getRelayState());
        log.info("Timestamp         : {}", request.getTimestamp());

        MeterRegistrationResponse response = processor.register(request);

        log.info("Sending SOAP response");
        log.info("Status            : {}", response.getStatus());
        log.info("Message           : {}", response.getMessage());
        log.info("Registration ID   : {}", response.getRegistrationId());
        log.info("===========================================");

        return response;
    }
}