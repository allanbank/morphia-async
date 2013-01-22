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
public class ShortConverter extends TypeConverter<Short> implements
        SimpleValueConverter {
    /**
     * Creates a new ShortConverter.
     */
    public ShortConverter() {
        super(int.class, Short.class);
    }

    @Override
    public Short decode(Class targetClass, Element val,
            MappedField optionalExtraInfo) throws MappingException {
        if ((val == null) || (val.getType() == ElementType.NULL)) {
            return null;
        }
        else if (val instanceof NumericElement) {
            return Short.valueOf((short) ((NumericElement) val).getIntValue());
        }
        else if (val.getType() == ElementType.STRING) {
            String sVal = ((StringElement) val).getValue();
            return Short.valueOf(Short.parseShort(sVal));
        }
        else if (val.getType() == ElementType.SYMBOL) {
            String sVal = ((SymbolElement) val).getSymbol();
            return Short.valueOf(Short.parseShort(sVal));
        }

        throw new MappingException("Could not figure out how to map a "
                + val.getClass().getSimpleName() + " into a Short.");
    }

    @Override
    public void encode(DocumentBuilder builder, String name, Short value,
            MappedField optionalExtraInfo) {
        if (value == null) {
            builder.addNull(name);
        }
        else {
            builder.addInteger(name, value.intValue());
        }
    }
}
