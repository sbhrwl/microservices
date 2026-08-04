package integration.enterprise.config;

import integration.enterprise.service.MeterRegistrationServiceImpl;
import jakarta.xml.ws.Endpoint;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CxfConfig {

    @Bean
    public Endpoint meterRegistrationEndpoint(
            Bus bus,
            MeterRegistrationServiceImpl service) {

        EndpointImpl endpoint = new EndpointImpl(bus, service);
        endpoint.publish("/meter-registration");

        return endpoint;
    }
}