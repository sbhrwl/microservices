# Test with SoapUI
- [List of services](#list-of-services)
- [WSDL](#wsdl)
- [Request](#request)
- [Response](#response)
## List of services
* `http://localhost:8080/services`
```
Available SOAP services:
MeterRegistrationPortType
registerMeter
Endpoint address: http://localhost:8080/services/meter-registration
WSDL : {http://service.enterprise.integration/}MeterRegistrationServiceImplService
Target namespace: http://service.enterprise.integration/
```
## WSDL
*`http://localhost:8080/services/meter-registration?wsdl`
```xml
<wsdl:definitions
        xmlns:xsd="http://www.w3.org/2001/XMLSchema"
        xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
        xmlns:tns="http://service.enterprise.integration/"
        xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"
        xmlns:ns2="http://schemas.xmlsoap.org/soap/http"
        xmlns:ns1="http://enterprise.integration/meter-registration/v1"
        name="MeterRegistrationServiceImplService"
        targetNamespace="http://service.enterprise.integration/">

  <wsdl:import
          namespace="http://enterprise.integration/meter-registration/v1"
          location="http://localhost:8080/services/meter-registration?wsdl=MeterRegistrationPortType.wsdl"/>

  <wsdl:binding
          name="MeterRegistrationServiceImplServiceSoapBinding"
          type="ns1:MeterRegistrationPortType">

    <soap:binding
            style="document"
            transport="http://schemas.xmlsoap.org/soap/http"/>

    <wsdl:operation name="registerMeter">

      <soap:operation
              soapAction="http://enterprise.integration/meter-registration/v1/registerMeter"
              style="document"/>

      <wsdl:input name="registerMeter">
        <soap:body use="literal"/>
      </wsdl:input>

      <wsdl:output name="registerMeterResponse">
        <soap:body use="literal"/>
      </wsdl:output>

    </wsdl:operation>

  </wsdl:binding>

  <wsdl:service name="MeterRegistrationServiceImplService">

    <wsdl:port
            name="MeterRegistrationServiceImplPort"
            binding="tns:MeterRegistrationServiceImplServiceSoapBinding">

      <soap:address
              location="http://localhost:8080/services/meter-registration"/>

    </wsdl:port>

  </wsdl:service>

</wsdl:definitions>
```
## Request
```xml
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:v1="http://enterprise.integration/meter-registration/v1">

   <soapenv:Header/>

   <soapenv:Body>
      <v1:MeterRegistrationRequest>
         <v1:gsrn>735999123456789012</v1:gsrn>
         <v1:meterSerialNumber>MS-123456</v1:meterSerialNumber>
         <v1:customerId>CUST-1001</v1:customerId>
         <v1:relayNumber>1</v1:relayNumber>
         <v1:relayState>ON</v1:relayState>
         <v1:timestamp>2026-08-03T18:30:00Z</v1:timestamp>
      </v1:MeterRegistrationRequest>
   </soapenv:Body>

</soapenv:Envelope>
```
## Response
```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
   <soap:Body>
      <MeterRegistrationResponse xmlns="http://enterprise.integration/meter-registration/v1">
         <status>SUCCESS</status>
         <message>Meter registered successfully</message>
         <registrationId>REG-10001</registrationId>
      </MeterRegistrationResponse>
   </soap:Body>
</soap:Envelope>
```