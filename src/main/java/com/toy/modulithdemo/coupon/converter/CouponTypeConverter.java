package com.toy.modulithdemo.coupon.converter;

import com.toy.modulithdemo.coupon.constant.CouponType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CouponTypeConverter implements AttributeConverter<CouponType, String> {

    @Override
    public String convertToDatabaseColumn(CouponType attribute) {
        return attribute.getCode();
    }

    @Override
    public CouponType convertToEntityAttribute(String dbData) {
        return CouponType.ofCode(dbData);
    }
}
