/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arpit.soap.client.config;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpComponentsConnection;

/**
 *
 * @author olivier
 */
@Configurable
public class AppClientInterceptor implements ClientInterceptor {

    @Autowired
    @Qualifier("webServiceTemplate")
    WebServiceTemplate webServiceTemplate;

    @Override
    public boolean handleRequest(MessageContext messageContext) throws WebServiceClientException {
        // pull out the request from the current thread
        try {
            TransportContext context = TransportContextHolder.getTransportContext();
            HttpComponentsConnection connection = (HttpComponentsConnection) context.getConnection();
            connection.addRequestHeader("Authorization", String.format("Bearer %s", "token_value"));
        } catch (Exception e) {
        }

        return true;
    }

    @Override
    public void afterCompletion(MessageContext mc, Exception excptn) throws WebServiceClientException {
        try {
            System.out.println("Request :");
            mc.getRequest().writeTo(System.out);
            System.out.println("\nResponse : ");
            mc.getResponse().writeTo(System.out);
            System.out.println();
        } catch (IOException ignored) {
        }
//    
    }

    @Override
    public boolean handleFault(MessageContext mc) throws WebServiceClientException {
        return true;
    }

    @Override
    public boolean handleResponse(MessageContext mc) throws WebServiceClientException {
        return true;
    }

}
