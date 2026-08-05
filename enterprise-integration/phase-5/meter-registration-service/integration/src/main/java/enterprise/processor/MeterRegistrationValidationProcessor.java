package integration.enterprise.processor;

import integration.enterprise.meter_registration.v1.MeterRegistrationRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class MeterRegistrationValidationProcessor implements Processor {

    @Override
    public void process(Exchange exchange) {

        MeterRegistrationRequest request =
                exchange.getIn().getBody(MeterRegistrationRequest.class);

        if (request.getGsrn() == null || request.getGsrn().isBlank()) {
            throw new IllegalArgumentException("GSRN is mandatory");
        }

        if (request.getMeterSerialNumber() == null ||
                request.getMeterSerialNumber().isBlank()) {

            throw new IllegalArgumentException(
                    "Meter Serial Number is mandatory");
        }

        if (request.getCustomerId() == null ||
                request.getCustomerId().isBlank()) {

            throw new IllegalArgumentException(
                    "Customer ID is mandatory");
        }
    }
}