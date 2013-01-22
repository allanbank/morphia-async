/**
 * 
 */
package com.google.code.morphia.converters;

import java.util.Date;

import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.ElementType;
import com.allanbank.mongodb.bson.NumericElement;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.allanbank.mongodb.bson.element.StringElement;
import com.allanbank.mongodb.bson.element.SymbolElement;
import com.allanbank.mongodb.bson.element.TimestampElement;
import com.google.code.morphia.mapping.MappedField;
import com.google.code.morphia.mapping.MappingException;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @author scotthernandez
 */
@SuppressWarnings({ "rawtypes" })
public class DateConverter extends TypeConverter<Date> implements
        SimpleValueConverter {

    /**
     * Creates a new DateConverter.
     */
    public DateConverter() {
        this(Date.class);
    }

    /**
     * Creates a new DateConverter.
     * 
     * @param clazz
     *            The sub type of Date.
     */
    protected DateConverter(Class<? extends Date> clazz) {
        super(clazz);
    }

    @SuppressWarnings("deprecation")
    @Override
    public Date decode(Class targetClass, Element val,
            MappedField optionalExtraInfo) throws MappingException {
        if ((val == null) || (val.getType() == ElementType.NULL)) {
            return null;
        }
        else if (val.getType() == ElementType.UTC_TIMESTAMP) {
            long ts = ((TimestampElement) val).getTime();
            return new Date(ts);
        }
        else if (val instanceof NumericElement) {
            long ts = ((NumericElement) val).getLongValue();
            return new Date(ts);
        }
        else if (val.getType() == ElementType.STRING) {
            String sVal = ((StringElement) val).getValue();
            return new Date(Date.parse(sVal));// good luck
        }
        else if (val.getType() == ElementType.SYMBOL) {
            String sVal = ((SymbolElement) val).getSymbol();
            return new Date(Date.parse(sVal));// good luck
        }

        throw new MappingException("Could not figure out how to map a "
                + val.getClass().getSimpleName() + " into a Date.");
    }

    @Override
    public void encode(DocumentBuilder builder, String name, Date value,
            MappedField optionalExtraInfo) {

        if (value == null) {
            builder.addNull(name);
        }
        else {
            builder.addTimestamp(name, value.getTime());
        }
    }

}
