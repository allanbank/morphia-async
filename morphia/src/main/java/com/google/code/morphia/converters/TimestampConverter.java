/**
 * 
 */
package com.google.code.morphia.converters;

import java.sql.Timestamp;
import java.util.Date;

import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.google.code.morphia.mapping.MappedField;
import com.google.code.morphia.mapping.MappingException;

/**
 * @author scotthernandez
 */
@SuppressWarnings({ "rawtypes" })
public class TimestampConverter extends DateConverter {

    /**
     * Creates a new TimestampConverter.
     */
    public TimestampConverter() {
        super(Timestamp.class);
    }

    @Override
    public Timestamp decode(Class targetClass, Element val,
            MappedField optionalExtraInfo) throws MappingException {
        Date d = super.decode(targetClass, val, optionalExtraInfo);
        return new Timestamp(d.getTime());
    }

    @Override
    public void encode(DocumentBuilder builder, String name, Date val,
            MappedField optionalExtraInfo) {
        super.encode(builder, name, val, optionalExtraInfo);
    }
}
