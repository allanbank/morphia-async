/**
 * 
 */
package com.google.code.morphia.converters;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.ElementType;
import com.allanbank.mongodb.bson.builder.ArrayBuilder;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.allanbank.mongodb.bson.element.ArrayElement;
import com.allanbank.mongodb.bson.element.StringElement;
import com.allanbank.mongodb.bson.element.SymbolElement;
import com.google.code.morphia.mapping.MappedField;
import com.google.code.morphia.mapping.MappingException;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @author scotthernandez
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class EnumSetConverter extends TypeConverter<EnumSet<? extends Enum>>
        implements SimpleValueConverter {

    /**
     * Creates a new EnumSetConverter.
     */
    public EnumSetConverter() {
        super(EnumSet.class);
    }

    @Override
    public EnumSet<? extends Enum> decode(Class targetClass, Element val,
            MappedField optionalExtraInfo) throws MappingException {

        if ((val == null) || (val.getType() == ElementType.NULL)) {
            return null;
        }

        Class enumType = optionalExtraInfo.getSubClass();
        if (val.getType() == ElementType.ARRAY) {
            ArrayElement ae = (ArrayElement) val;
            List<Enum> values = new ArrayList<Enum>();
            for (Element e : ae.getEntries()) {
                String name = null;
                if (e.getType() == ElementType.STRING) {
                    name = ((StringElement) e).getValue();
                }
                else if (e.getType() == ElementType.SYMBOL) {
                    name = ((SymbolElement) e).getSymbol();
                }

                if (name != null) {
                    values.add(Enum.valueOf(enumType, name));
                }
            }

            return EnumSet.copyOf(values);
        }
        throw new MappingException("Could not figure out how to map a "
                + val.getClass().getSimpleName() + " into an EnumSet for "
                + enumType.getSimpleName());
    }

    @Override
    public void encode(DocumentBuilder builder, String name,
            EnumSet<? extends Enum> value, MappedField optionalExtraInfo) {
        if (value == null) {
            builder.addNull(name);
        }
        else {
            ArrayBuilder ab = builder.pushArray(name);
            for (Enum e : value) {
                ab.add(e.name());
            }
        }
    }
}
