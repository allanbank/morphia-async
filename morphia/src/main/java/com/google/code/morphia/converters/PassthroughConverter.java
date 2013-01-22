/**
 * 
 */
package com.google.code.morphia.converters;

import com.allanbank.mongodb.bson.Element;
import com.google.code.morphia.mapping.MappedField;
import com.google.code.morphia.mapping.MappingException;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @author scotthernandez
 */
@SuppressWarnings({ "rawtypes" })
public class PassthroughConverter extends TypeConverter<Object> {

    /**
     * Creates a new PassthroughConverter.
     */
    public PassthroughConverter() {
    }

    /**
     * Creates a new PassthroughConverter.
     * 
     * @param types
     *            The types to pass.
     */
    public PassthroughConverter(Class... types) {
        super(types);
    }

    @Override
    protected boolean isSupported(Class c, MappedField optionalExtraInfo) {
        return true;
    }

    @Override
    public Object decode(Class targetClass, Element fromDBObject,
            MappedField optionalExtraInfo) throws MappingException {
        return fromDBObject;
    }
}
