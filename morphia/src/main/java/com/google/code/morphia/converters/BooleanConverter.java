/**
 * 
 */
package com.google.code.morphia.converters;

import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.ElementType;
import com.allanbank.mongodb.bson.NumericElement;
import com.allanbank.mongodb.bson.element.BooleanElement;
import com.allanbank.mongodb.bson.element.StringElement;
import com.allanbank.mongodb.bson.element.SymbolElement;
import com.google.code.morphia.mapping.MappedField;
import com.google.code.morphia.mapping.MappingException;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @author scotthernandez
 */
@SuppressWarnings({ "rawtypes" })
public class BooleanConverter extends TypeConverter<Boolean> implements
        SimpleValueConverter {

    /**
     * Creates a new BooleanConverter.
     */
    public BooleanConverter() {
        super(boolean.class, Boolean.class);
    }

    @Override
    public Boolean decode(Class targetClass, Element val,
            MappedField optionalExtraInfo) throws MappingException {
        if ((val == null) || (val.getType() == ElementType.NULL)) {
            return null;
        }
        else if (val.getType() == ElementType.BOOLEAN) {
            return Boolean.valueOf(((BooleanElement) val).getValue());
        }
        else if (val instanceof NumericElement) {
            // handle the case for things like the ok field
            return Boolean.valueOf(((NumericElement) val).getIntValue() != 0);
        }
        else if (val.getType() == ElementType.STRING) {
            String sVal = ((StringElement) val).getValue();
            return Boolean.valueOf(Boolean.parseBoolean(sVal));
        }
        else if (val.getType() == ElementType.SYMBOL) {
            String sVal = ((SymbolElement) val).getSymbol();
            return Boolean.valueOf(Boolean.parseBoolean(sVal));
        }

        throw new MappingException("Could not figure out how to map a "
                + val.getClass().getSimpleName() + " into a boolean.");
    }
}
