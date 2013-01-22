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
public class ClassConverter extends TypeConverter<Class<?>> implements
        SimpleValueConverter {

    /**
     * Creates a new ClassConverter.
     */
    public ClassConverter() {
        super(Class.class);
    }

    @Override
    public Class<?> decode(Class targetClass, Element val,
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
            try {
                return Class.forName(name);
            }
            catch (ClassNotFoundException e) {
                throw new MappingException("Cannot create class from name '"
                        + name + "'", e);
            }
        }

        throw new MappingException("Could not figure out how to map a "
                + val.getClass().getSimpleName() + " into a Class.");
    }

    @Override
    public void encode(DocumentBuilder builder, String name, Class<?> value,
            MappedField optionalExtraInfo) {
        if (value == null) {
            builder.addNull(optionalExtraInfo.getNameToStore());
        }
        else {
            builder.add(name, value.getName());
        }
    }
}
