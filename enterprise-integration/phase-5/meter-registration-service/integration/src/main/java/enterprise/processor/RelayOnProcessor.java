package integration.enterprise.processor;

import integration.enterprise.meter_registration.v1.MeterRegistrationRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class RelayOnProcessor implements Processor {

    @Override
    public void process(Exchange exchange) {

        MeterRegistrationRequest request =
                exchange.getIn().getBody(MeterRegistrationRequest.class);

        System.out.println("Relay ON requested for "
                + request.getMeterSerialNumber());
    }
}