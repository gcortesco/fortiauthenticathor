package com.riu.fortiauthenticator;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.*;

import java.io.*;
import java.time.Instant;



import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

import java.io.IOException;


public class JSONParser {



    private static volatile ObjectMapper mapper = null;
    private static ObjectReader reader = null;
    private static ObjectWriter writer = null;

    static {
        if (mapper == null) {
            mapper = new ObjectMapper();
            AnnotationIntrospector primary = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance(), false);
            AnnotationIntrospector secondary = new JacksonAnnotationIntrospector();
            AnnotationIntrospector introspector = new AnnotationIntrospectorPair(primary, secondary);
            mapper.setAnnotationIntrospector(introspector);
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

            mapper.findAndRegisterModules();
            reader = mapper.reader();
            writer = mapper.writer();
        }
    }



    private JSONParser() {
        //Constructor privado
    }

    public static ObjectMapper objectMapper() {
        return mapper;
    }

    public static ObjectReader reader() {
        return reader;
    }

    public static ObjectWriter writer() {
        return writer;
    }



    private static <T> T parse( Reader json, Class<T> type) {
        Instant now = Instant.now();
        try {
            if (JsonNode.class.equals(type)) {
                return (T) reader().readTree(json);
            } else {
                return reader().readValue(new JsonFactory().createParser(json), type);
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static String toJson(Object object) throws JsonProcessingException {
        Instant now = Instant.now();
        String json = writer().writeValueAsString(object);
        return json;
    }



    public static <T> T toObject(InputStream json, Class<T> type) {
        return parse(new InputStreamReader(json), type);
    }


    public static <T> T to0bject(String json, Class<T> type)  {
        try {
            Instant now = Instant.now();
            T object = objectMapper().readValue(json, type);
            return object;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}

