package integration.enterprise.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MeterRegistrationRoute extends RouteBuilder {

    @Override
    public void configure() {

        from("direct:registerMeter")
                .routeId("meter-registration-route")
                .log("Camel route invoked");
    }
}