/**
 * 
 */
package com.google.code.morphia.converters;

import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.ElementType;
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
public class StringConverter extends TypeConverter<String> implements
        SimpleValueConverter {
    /**
     * Creates a new StringConverter.
     */
    public StringConverter() {
        super(String.class);
    }

    @Override
    public void encode(DocumentBuilder builder, String name, String value,
            MappedField optionalExtraInfo) {
        if (value == null) {
            builder.addNull(name);
        }
        else {
            builder.add(name, value);
        }
    }

    @Override
    public String decode(Class targetClass, Element val,
            MappedField optionalExtraInfo) throws MappingException {
        if ((val == null) || (val.getType() == ElementType.NULL)) {
            return null;
        }
        else if (val.getType() == ElementType.STRING) {
            return ((StringElement) val).getValue();
        }
        else if (val.getType() == ElementType.SYMBOL) {
            return ((SymbolElement) val).getSymbol();
        }

        throw new MappingException("Could not figure out how to map a "
                + val.getClass().getSimpleName() + " into a URI.");
    }
}
