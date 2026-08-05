package integration.enterprise.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class MeterRegistrationEnrichmentProcessor implements Processor {

    @Override
    public void process(Exchange exchange) {

        exchange.setProperty("correlationId", UUID.randomUUID().toString());

        exchange.setProperty("receivedAt", Instant.now());

        exchange.getIn().setHeader("operation", "REGISTER_METER");
    }
}