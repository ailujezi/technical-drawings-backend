package com.ritzjucy.technicaldrawingsbackend.entity.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.ritzjucy.technicaldrawingsbackend.model.ResultElementModel;
import com.ritzjucy.technicaldrawingsbackend.util.SerializationHelper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.List;

@Converter(autoApply = true)
public class ResultElementConverter implements AttributeConverter<List<ResultElementModel>, String>
{
    private static final CollectionType javaType
            = SerializationHelper.constructCollectionType(List.class, ResultElementModel.class);

    @Override
    public String convertToDatabaseColumn(List<ResultElementModel> messages)
    {
        try {
            return SerializationHelper.serializeToJSON(messages);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting ResultElementEntities to String", e);
        }
    }

    @Override
    public List<ResultElementModel> convertToEntityAttribute(String messagesAsString)
    {
        if(messagesAsString == null || messagesAsString.isBlank())
            messagesAsString = "[]";

        try {
            return SerializationHelper.deserializeFromJSON(messagesAsString, javaType);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting String to ResultElementEntities", e);
        }
    }

}
