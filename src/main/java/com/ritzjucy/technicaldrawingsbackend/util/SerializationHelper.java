package com.ritzjucy.technicaldrawingsbackend.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.Collection;

public class SerializationHelper
{
    private static final JsonMapper jsonMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true)
            .configure(JsonReadFeature.ALLOW_TRAILING_COMMA.mappedFeature(), true)
            .configure(JsonReadFeature.ALLOW_JAVA_COMMENTS.mappedFeature(), true)
            .build();

    public static <T> String serializeToJSON(T toSerialize) throws JsonProcessingException
    {
        return jsonMapper.writeValueAsString(toSerialize);
    }

    public static <T> T deserializeFromJSON(String serialized, JavaType javaType) throws JsonProcessingException
    {
        if(serialized == null)
            return null;

        return jsonMapper.readValue(serialized, javaType);
    }

    public static CollectionType constructCollectionType(Class<? extends Collection> collectionClass, Class<?> elementClass)
    {
        return jsonMapper.getTypeFactory().constructCollectionType(collectionClass, elementClass);
    }

    public static JavaType constructJavaType(Class<?> elementClass)
    {
        return jsonMapper.getTypeFactory().constructType(elementClass);
    }

}
