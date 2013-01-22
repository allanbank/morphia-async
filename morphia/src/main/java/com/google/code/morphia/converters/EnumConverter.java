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
@SuppressWarnings({ "unchecked", "rawtypes" })
public class EnumConverter extends TypeConverter<Enum> implements
        SimpleValueConverter {

    @Override
    protected boolean isSupported(Class c, MappedField optionalExtraInfo) {
        return c.isEnum();
    }

    @Override
    public Enum decode(Class targetClass, Element val,
            MappedField optionalExtraInfo) throws MappingException {
        if ((val == null) || (val.getType() == ElementType.NULL)) {
            return null;
        }

        String name = null;
        if (val.getType() == ElementType.STRING) {
            name = ((StringElement) val).getValue();
        }
        else if (val.getType() == ElementType.SYMBOL) {
            name = ((SymbolElement) val).getSymbol();
        }

        if (name != null) {
            return Enum.valueOf(targetClass, name);
        }

        throw new MappingException("Could not figure out how to map a "
                + val.getClass().getSimpleName() + " into an Enum for "
                + targetClass.getSimpleName());
    }

    @Override
    public void encode(DocumentBuilder builder, String name, Enum value,
            MappedField optionalExtraInfo) {
        if (value == null) {
            builder.addNull(name);
        }
        else {
            builder.addString(name, value.name());
        }
    }
}
