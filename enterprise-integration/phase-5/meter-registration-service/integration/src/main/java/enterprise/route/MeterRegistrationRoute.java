package integration.enterprise.route;

import integration.enterprise.service.MeterRegistrationProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MeterRegistrationRoute extends RouteBuilder {

    private final MeterRegistrationProcessor processor;

    public MeterRegistrationRoute(MeterRegistrationProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void configure() {

        from("direct:registerMeter")
                .routeId("meter-registration-route")
                .log("Camel route invoked")
                .bean(processor, "register")
                .log("Camel route completed");
    }
}