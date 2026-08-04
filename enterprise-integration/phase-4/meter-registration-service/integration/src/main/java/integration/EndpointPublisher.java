package integration;

import integration.enterprise.meter_registration.v1.MeterRegistrationPortTypeImpl;
import jakarta.xml.ws.Endpoint;

public class EndpointPublisher {

    public static void main(String[] args) {

        Endpoint.publish(
                "http://localhost:8080/meter-registration",
                new MeterRegistrationPortTypeImpl()
        );

        System.out.println("SOAP endpoint published.");
        System.out.println("http://localhost:8080/meter-registration?wsdl");
    }
}