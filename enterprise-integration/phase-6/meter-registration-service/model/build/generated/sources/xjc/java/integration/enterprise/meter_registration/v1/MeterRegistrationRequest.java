package integration.enterprise.meter_registration.v1;

import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MeterRegistrationRequest complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType name="MeterRegistrationRequest">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="gsrn" type="{http://enterprise.integration/meter-registration/v1}GSRN"/>
 *         <element name="meterSerialNumber" type="{http://enterprise.integration/meter-registration/v1}MeterSerialNumber"/>
 *         <element name="customerId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="relayNumber" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="relayState" type="{http://enterprise.integration/meter-registration/v1}RelayState"/>
 *         <element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MeterRegistrationRequest", propOrder = {
    "gsrn",
    "meterSerialNumber",
    "customerId",
    "relayNumber",
    "relayState",
    "timestamp"
})
public class MeterRegistrationRequest {

    @XmlElement(required = true)
    protected String gsrn;
    @XmlElement(required = true)
    protected String meterSerialNumber;
    @XmlElement(required = true)
    protected String customerId;
    protected int relayNumber;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected RelayState relayState;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar timestamp;

    /**
     * Gets the value of the gsrn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGsrn() {
        return gsrn;
    }

    /**
     * Sets the value of the gsrn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGsrn(String value) {
        this.gsrn = value;
    }

    /**
     * Gets the value of the meterSerialNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMeterSerialNumber() {
        return meterSerialNumber;
    }

    /**
     * Sets the value of the meterSerialNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMeterSerialNumber(String value) {
        this.meterSerialNumber = value;
    }

    /**
     * Gets the value of the customerId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * Sets the value of the customerId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerId(String value) {
        this.customerId = value;
    }

    /**
     * Gets the value of the relayNumber property.
     * 
     */
    public int getRelayNumber() {
        return relayNumber;
    }

    /**
     * Sets the value of the relayNumber property.
     * 
     */
    public void setRelayNumber(int value) {
        this.relayNumber = value;
    }

    /**
     * Gets the value of the relayState property.
     * 
     * @return
     *     possible object is
     *     {@link RelayState }
     *     
     */
    public RelayState getRelayState() {
        return relayState;
    }

    /**
     * Sets the value of the relayState property.
     * 
     * @param value
     *     allowed object is
     *     {@link RelayState }
     *     
     */
    public void setRelayState(RelayState value) {
        this.relayState = value;
    }

    /**
     * Gets the value of the timestamp property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTimestamp(XMLGregorianCalendar value) {
        this.timestamp = value;
    }

}
