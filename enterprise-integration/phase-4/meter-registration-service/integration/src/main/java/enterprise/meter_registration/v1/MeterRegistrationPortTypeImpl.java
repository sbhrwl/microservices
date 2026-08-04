package integration.enterprise.meter_registration.v1;

public class MeterRegistrationPortTypeImpl implements MeterRegistrationPortType {

    @Override
    public MeterRegistrationResponse registerMeter(MeterRegistrationRequest request) {

        MeterRegistrationResponse response = new MeterRegistrationResponse();

        response.setStatus("SUCCESS");
        response.setMessage("Meter registered successfully");
        response.setRegistrationId("REG-10001");

        return response;
    }
}