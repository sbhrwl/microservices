package integration.enterprise.route;

import integration.enterprise.processor.MeterRegistrationValidationProcessor;
import integration.enterprise.processor.MeterRegistrationEnrichmentProcessor;
import integration.enterprise.service.MeterRegistrationProcessor;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MeterRegistrationRoute extends RouteBuilder {

    private final MeterRegistrationProcessor processor;
    private final MeterRegistrationValidationProcessor validationProcessor;
    private final MeterRegistrationEnrichmentProcessor enrichmentProcessor;

    public MeterRegistrationRoute(
            MeterRegistrationProcessor processor,
            MeterRegistrationValidationProcessor validationProcessor,
            MeterRegistrationEnrichmentProcessor enrichmentProcessor) {

        this.processor = processor;
        this.validationProcessor = validationProcessor;
        this.enrichmentProcessor = enrichmentProcessor;
    }

    @Override
    public void configure() {

        onException(IllegalArgumentException.class)
                .handled(false)
                .log(LoggingLevel.ERROR,
                        "Validation failed: ${exception.message}");

        from("direct:registerMeter")
                .routeId("meter-registration-route")
                .log("Camel received request for GSRN=${body.gsrn}")
                .process(validationProcessor)
                .process(enrichmentProcessor)
                .bean(processor, "register")
                .log("Camel completed registration. Response=${body.status}");
    }
}