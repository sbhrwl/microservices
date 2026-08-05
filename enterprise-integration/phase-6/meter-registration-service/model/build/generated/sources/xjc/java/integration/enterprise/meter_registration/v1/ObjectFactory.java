package integration.enterprise.meter_registration.v1;

import javax.xml.namespace.QName;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the integration.enterprise.meter_registration.v1 package. 
 * <p>An ObjectFactory allows you to programmatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private static final QName _MeterRegistrationRequest_QNAME = new QName("http://enterprise.integration/meter-registration/v1", "MeterRegistrationRequest");
    private static final QName _MeterRegistrationResponse_QNAME = new QName("http://enterprise.integration/meter-registration/v1", "MeterRegistrationResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: integration.enterprise.meter_registration.v1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MeterRegistrationRequest }
     * 
     * @return
     *     the new instance of {@link MeterRegistrationRequest }
     */
    public MeterRegistrationRequest createMeterRegistrationRequest() {
        return new MeterRegistrationRequest();
    }

    /**
     * Create an instance of {@link MeterRegistrationResponse }
     * 
     * @return
     *     the new instance of {@link MeterRegistrationResponse }
     */
    public MeterRegistrationResponse createMeterRegistrationResponse() {
        return new MeterRegistrationResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MeterRegistrationRequest }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link MeterRegistrationRequest }{@code >}
     */
    @XmlElementDecl(namespace = "http://enterprise.integration/meter-registration/v1", name = "MeterRegistrationRequest")
    public JAXBElement<MeterRegistrationRequest> createMeterRegistrationRequest(MeterRegistrationRequest value) {
        return new JAXBElement<>(_MeterRegistrationRequest_QNAME, MeterRegistrationRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MeterRegistrationResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link MeterRegistrationResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://enterprise.integration/meter-registration/v1", name = "MeterRegistrationResponse")
    public JAXBElement<MeterRegistrationResponse> createMeterRegistrationResponse(MeterRegistrationResponse value) {
        return new JAXBElement<>(_MeterRegistrationResponse_QNAME, MeterRegistrationResponse.class, null, value);
    }

}
