/**
 * 
 */
package com.google.code.morphia.converters;

import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.ElementType;
import com.allanbank.mongodb.bson.NumericElement;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.allanbank.mongodb.bson.element.StringElement;
import com.allanbank.mongodb.bson.element.SymbolElement;
import com.google.code.morphia.mapping.MappedField;
import com.google.code.morphia.mapping.MappingException;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @author scotthernandez
 */
@SuppressWarnings({ "rawtypes" })
public class LongConverter extends TypeConverter<Long> implements
        SimpleValueConverter {
    /**
     * Creates a new LongConverter.
     */
    public LongConverter() {
        super(int.class, Long.class);
    }

    @Override
    public Long decode(Class targetClass, Element val,
            MappedField optionalExtraInfo) throws MappingException {
        if ((val == null) || (val.getType() == ElementType.NULL)) {
            return null;
        }
        else if (val instanceof NumericElement) {
            return Long.valueOf(((NumericElement) val).getLongValue());
        }
        else if (val.getType() == ElementType.STRING) {
            String sVal = ((StringElement) val).getValue();
            return Long.valueOf(Long.parseLong(sVal));
        }
        else if (val.getType() == ElementType.SYMBOL) {
            String sVal = ((SymbolElement) val).getSymbol();
            return Long.valueOf(Long.parseLong(sVal));
        }

        throw new MappingException("Could not figure out how to map a "
                + val.getClass().getSimpleName() + " into a Long.");
    }

    @Override
    public void encode(DocumentBuilder builder, String name, Long value,
            MappedField optionalExtraInfo) {
        if (value == null) {
            builder.addNull(name);
        }
        else {
            builder.addLong(name, value.longValue());
        }
    }
}
