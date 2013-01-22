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
public class CharacterConverter extends TypeConverter<Character> implements
        SimpleValueConverter {
    /**
     * Creates a new CharacterConverter.
     */
    public CharacterConverter() {
        super(Character.class, char.class);
    }

    @Override
    public Character decode(Class targetClass, Element val,
            MappedField optionalExtraInfo) throws MappingException {

        // TODO: Check length. Maybe "" should be null?

        if ((val == null) || (val.getType() == ElementType.NULL)) {
            return null;
        }
        else if (val.getType() == ElementType.STRING) {
            String sVal = ((StringElement) val).getValue();
            return Character.valueOf(sVal.charAt(0));
        }
        else if (val.getType() == ElementType.SYMBOL) {
            String sVal = ((SymbolElement) val).getSymbol();
            return Character.valueOf(sVal.charAt(0));
        }

        throw new MappingException("Could not figure out how to map a "
                + val.getClass().getSimpleName() + " into a char.");
    }

    @Override
    public void encode(DocumentBuilder builder, String name, Character value,
            MappedField optionalExtraInfo) {
        builder.add(name, String.valueOf(value));
    }
}
