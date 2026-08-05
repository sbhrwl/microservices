package integration.enterprise.service;

import integration.enterprise.meter_registration.v1.MeterRegistrationRequest;
import integration.enterprise.meter_registration.v1.MeterRegistrationResponse;
import integration.enterprise.meter_registration.v1.RelayState;
import org.junit.jupiter.api.Test;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import static org.junit.jupiter.api.Assertions.*;

class MeterRegistrationProcessorImplTest {

    @Test
    void shouldRegisterMeterSuccessfully() throws Exception {

        // --------------------------------------------------------------------
        // Arrange
        // Create the processor under test.
        // This is a pure unit test, so no Spring context is started.
        // --------------------------------------------------------------------
        MeterRegistrationProcessorImpl processor =
                new MeterRegistrationProcessorImpl();

        // Create a sample request
        MeterRegistrationRequest request =
                new MeterRegistrationRequest();

        request.setGsrn("735999123456789011");
        request.setMeterSerialNumber("MS-123456");
        request.setCustomerId("CUST-1001");
        request.setRelayNumber(1);
        request.setRelayState(RelayState.ON);

        // JAXB maps xs:dateTime to XMLGregorianCalendar
        XMLGregorianCalendar timestamp =
                DatatypeFactory.newInstance()
                        .newXMLGregorianCalendar("2026-08-05T12:00:00Z");

        request.setTimestamp(timestamp);

        // --------------------------------------------------------------------
        // Act
        // Invoke the business logic.
        // --------------------------------------------------------------------
        MeterRegistrationResponse response =
                processor.register(request);

        // --------------------------------------------------------------------
        // Assert
        // Verify that the processor returns the expected response.
        // --------------------------------------------------------------------
        assertNotNull(response);

        assertEquals(
                "SUCCESS",
                response.getStatus());

        assertEquals(
                "Meter registered successfully",
                response.getMessage());

        assertNotNull(
                response.getRegistrationId());

        assertFalse(
                response.getRegistrationId().isBlank());
    }
}