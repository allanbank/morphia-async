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
public class CharArrayConverter extends TypeConverter<char[]> implements
        SimpleValueConverter {

    /**
     * Creates a new CharArrayConverter.
     */
    public CharArrayConverter() {
        super(char[].class);
    }

    @Override
    public char[] decode(Class targetClass, Element val,
            MappedField optionalExtraInfo) throws MappingException {
        if ((val == null) || (val.getType() == ElementType.NULL)) {
            return null;
        }
        else if (val.getType() == ElementType.STRING) {
            String sVal = ((StringElement) val).getValue();
            return sVal.toCharArray();
        }
        else if (val.getType() == ElementType.SYMBOL) {
            String sVal = ((SymbolElement) val).getSymbol();
            return sVal.toCharArray();
        }

        throw new MappingException("Could not figure out how to map a "
                + val.getClass().getSimpleName() + " into a char[].");
    }

    @Override
    public void encode(DocumentBuilder builder, String name, char[] value,
            MappedField optionalExtraInfo) {

        builder.add(name, new String(value));
    }
}
