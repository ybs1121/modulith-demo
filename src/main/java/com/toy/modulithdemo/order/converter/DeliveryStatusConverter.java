package com.toy.modulithdemo.order.converter;

import com.toy.modulithdemo.order.constant.DeliveryStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class DeliveryStatusConverter implements AttributeConverter<DeliveryStatus, String> {

    @Override
    public String convertToDatabaseColumn(DeliveryStatus attribute) {
        return attribute.getCode();
    }

    @Override
    public DeliveryStatus convertToEntityAttribute(String dbData) {
        return DeliveryStatus.ofCode(dbData);
    }
}
