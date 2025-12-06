package org.svids.tbankcooldownapi.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Converter(autoApply = true)
public class PurchaseCategorySetConverter implements AttributeConverter<Set<PurchaseCategory>, String[]> {
    
    @Override
    public String[] convertToDatabaseColumn(Set<PurchaseCategory> categories) {
        if (categories == null || categories.isEmpty()) {
            return new String[0];
        }
        
        return categories.stream()
            .map(PurchaseCategory::name)
            .toArray(String[]::new);
    }
    
    @Override
    public Set<PurchaseCategory> convertToEntityAttribute(String[] dbData) {
        if (dbData == null || dbData.length == 0) {
            return Collections.emptySet();
        }
        
        return Arrays.stream(dbData)
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .map(PurchaseCategory::valueOf)
            .collect(Collectors.toSet());
    }
}