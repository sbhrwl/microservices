package integration.enterprise.route;

import integration.enterprise.meter_registration.v1.MeterRegistrationRequest;
import integration.enterprise.meter_registration.v1.MeterRegistrationResponse;
import integration.enterprise.meter_registration.v1.RelayState;
import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MeterRegistrationRouteTest {

    @Autowired
    private ProducerTemplate producerTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void shouldProcessMeterRegistrationRoute() throws Exception {

        // --------------------------------------------------------------------
        // Arrange
        // --------------------------------------------------------------------
        MeterRegistrationRequest request = new MeterRegistrationRequest();

        request.setGsrn("735999123456789011");
        request.setMeterSerialNumber("MS-123456");
        request.setCustomerId("CUST-1001");
        request.setRelayNumber(1);
        request.setRelayState(RelayState.ON);

        XMLGregorianCalendar timestamp =
                DatatypeFactory.newInstance()
                        .newXMLGregorianCalendar("2026-08-05T12:00:00Z");

        request.setTimestamp(timestamp);

        // --------------------------------------------------------------------
        // Act
        // Send the request into the Camel route.
        // --------------------------------------------------------------------
        MeterRegistrationResponse response =
                producerTemplate.requestBody(
                        "direct:registerMeter",
                        request,
                        MeterRegistrationResponse.class);

        // --------------------------------------------------------------------
        // Assert
        // --------------------------------------------------------------------
        assertNotNull(response);

        assertEquals(
                "SUCCESS",
                response.getStatus());

        assertEquals(
                "Meter registered successfully",
                response.getMessage());

        assertNotNull(response.getRegistrationId());

        assertFalse(response.getRegistrationId().isBlank());
    }
}