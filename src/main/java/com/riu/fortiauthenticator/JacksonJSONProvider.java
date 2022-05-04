package com.riu.fortiauthenticator;


import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.List;

@Provider
@Consumes({"application/json", "application/*+json", "application/javascript"})
@Produces({"application/json", "application/*+json", "application/javascript"})
public class JacksonJSONProvider extends com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider {
//Fuerzo el contentType a JSON, ver http://stackoverflow.com/questions/38904640/how-to-use-jackson-as-json-provider-for-jax-rs-client-instead-of-johnzon-in-tome

    public JacksonJSONProvider() {
        super();
        setMapper(JSONParser.objectMapper());
    }

    public static List<JacksonJSONProvider> jsonWebClientProviders() {
        List providers = new ArrayList();
        providers.add(new JacksonJSONProvider());
        return providers;
    }

    @Override
    public String toString() {
        return "JacksonJSONProvider{}";
    }
}