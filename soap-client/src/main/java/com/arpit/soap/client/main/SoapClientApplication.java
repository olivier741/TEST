package com.arpit.soap.client.main;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.bind.JAXBElement;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.xml.transform.StringSource;

import java.io.StringReader;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.transform.stream.StreamSource;
import org.springframework.ws.client.WebServiceTransportException;
import org.springframework.ws.soap.SoapFaultException;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

@SpringBootApplication
@ComponentScan("com.arpit.soap.client.config")
public class SoapClientApplication implements CommandLineRunner {

    @Autowired
    @Qualifier("webServiceTemplate")
    private WebServiceTemplate webServiceTemplate;

    @Value("#{'${service.soap.action}'}")
    private String serviceSoapAction;
    
    @Value("#{'${service.endpoint}'}")
    private String serviceEndpoint;

    @Value("#{'${service.user.id}'}")
    private String serviceUserId;

    @Value("#{'${service.user.password}'}")
    private String serviceUserPassword;

    private String uri = "http://localhost:9999/service/hello-world";

    private String MESSAGE = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n"
            + " xmlns:ser=\"http://service.server.soap.arpit.com/\">\n"
            + "   <soapenv:Header>\n"
            + "      <ser:arg1>\n"
            + "         <userId>?</userId>\n"
            + "         <password>?</password>\n"
            + "      </ser:arg1>\n"
            + "   </soapenv:Header>\n"
            + "   <soapenv:Body>\n"
            + "      <ser:helloWorld xmlns:ser=\"http://service.server.soap.arpit.com/\">\n"
            + "         <!--Optional:-->\n"
            + "         <arg0>?</arg0>\n"
            + "      </ser:helloWorld>\n"
            + "   </soapenv:Body>\n"
            + "</soapenv:Envelope>";

    private String MESSAGE1 = "<ser:helloWorld xmlns:ser=\"http://service.server.soap.arpit.com/\">\n"
            + "         <!--Optional:-->\n"
            + "         <arg0>olivier</arg0>\n"
            + "      </ser:helloWorld>\n";

    private String Header = "<ser:arg1  xmlns:ser=\"http://service.server.soap.arpit.com/\"> \n"
            + "         <userId>?</userId>\n"
            + "         <password>?</password>\n"
            + "      </ser:arg1>";

    public static void main(String[] args) {
        SpringApplication.run(SoapClientApplication.class, args);
        System.exit(0);
    }

    public void run(String... args) throws Exception {
        webServiceTemplate.setDefaultUri(serviceEndpoint);
        testLogin();
    }

 
    private void testLogin() throws Exception {

        StreamSource source = new StreamSource(new StringReader(MESSAGE1));
        StreamResult result = new StreamResult(System.out);

        boolean resultReturned = false;
        try {
            resultReturned = webServiceTemplate.sendSourceAndReceiveToResult(source,
                    new SoapActionCallback("") {
                @Override
                public void doWithMessage(WebServiceMessage message) throws IOException {
                    super.doWithMessage(message); //To change body of generated methods, choose Tools | Templates.

                    SoapMessage soapMessage = (SoapMessage) message;
                    SoapHeader header = soapMessage.getSoapHeader();
                    StringSource headerSource = new StringSource(Header);
                    try {
                        Transformer transformer = TransformerFactory.newInstance().newTransformer();
                        transformer.transform(headerSource, header.getResult());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            },
                    result);
        } catch (Exception sfe) {

            System.out.println("SoapFaultClientException resultReturned: " + resultReturned);
            sfe.printStackTrace();

        }
    }
}
