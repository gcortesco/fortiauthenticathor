package com.riu.fortiauthenticator;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;

import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;


public class FortiOAuthClient {

    public static void main(String[] args) throws Exception{
        List providers = new ArrayList();
        providers.add(new JacksonJSONProvider());

        WebClient client = WebClient.create("https://doblefactor.riu.net/api/v1/oauth/token/", providers)
				.type(MediaType.APPLICATION_JSON_TYPE).
						accept(MediaType.APPLICATION_JSON_TYPE);
        setClientTimeOut(client, 3000L);
        setTrustStore(client);

        OauthRequest request = new OauthRequest("itar", "itartest01.",
                "6bMDbv5yVvpn1Y3AbNl4GFrm10ER7U3qX8oFNNLw", "cJ21o1MhDt7VZsJQ4qq6z4klA2or0eQilhALGcW8vtvc0eEiXr8os2I5COxDqB9bnWro5TniQ5OZWLpZOroZyxSkqseoiP4IDiA3rwbTx3PyJUBlOW4hkEadxZjjhyze",
                "password");

        InputStream jsonResponseStream = client.post(request, InputStream.class);
        String accessToken = JSONParser.toObject(jsonResponseStream, JsonNode.class).get("access_token").asText();
        verifyToken(accessToken);
    }

    private static void verifyToken(String token) throws Exception{
        List providers = new ArrayList();
        providers.add(new JacksonJSONProvider());
        WebClient client = WebClient.create("https://doblefactor.riu.net/api/v1/oauth/verify_token/?client_id=6bMDbv5yVvpn1Y3AbNl4GFrm10ER7U3qX8oFNNLw", providers)
                         .type(MediaType.APPLICATION_JSON_TYPE).
                        accept(MediaType.APPLICATION_JSON_TYPE);
        client.header("Authorization", "Bearer " + token);
        setClientTimeOut(client, 3000L);
        setTrustStore(client);

        Response response = client.get();
        System.out.println("Status Verified:"+response.getStatus());

    }

    private static void setTrustStore(WebClient client ) throws Exception{
        ClientConfiguration config = WebClient.getConfig(client);
        HTTPConduit http = config.getHttpConduit();
        TLSClientParameters tlsParams = new TLSClientParameters();
        http.setTlsClientParameters(tlsParams);
        TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        String trustpass = "password";
       // -- provide your truststore
        File truststore2 = new File("C:/Temp/fortiautenticator.p12");
        keyStore.load(new FileInputStream(truststore2), trustpass.toCharArray());
        trustFactory.init(keyStore);
        TrustManager[] tm = trustFactory.getTrustManagers();
        tlsParams.setTrustManagers(tm);
    }

    public static WebClient setClientTimeOut(WebClient client, long timeout) {
        ClientConfiguration config = WebClient.getConfig(client);
        config.setSynchronousTimeout(timeout);
        config.getInInterceptors().add(new LoggingInInterceptor());
        config.getOutInterceptors().add(new LoggingOutInterceptor());
        return client;
    }



}
