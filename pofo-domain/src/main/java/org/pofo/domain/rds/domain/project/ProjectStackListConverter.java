package org.pofo.domain.rds.domain.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.pofo.common.exception.CustomException;
import org.pofo.common.exception.ErrorCode;

import java.io.IOException;
import java.util.List;

@Converter
public class ProjectStackListConverter implements AttributeConverter<List<ProjectStack>, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<ProjectStack> attribute) {
       try {
           return objectMapper.writeValueAsString(attribute);
       } catch (JsonProcessingException ex) {
           throw new CustomException(ErrorCode.PROJECT_STACK_NOT_FOUND);
       }
    }

    @Override
    public List<ProjectStack> convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, new TypeReference<>() {
            });
        } catch (IOException ex) {
            throw new CustomException(ErrorCode.PROJECT_STACK_NOT_FOUND);
        }
    }
}

