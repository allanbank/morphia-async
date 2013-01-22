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
public class FloatConverter extends TypeConverter<Float> implements
        SimpleValueConverter {

    /**
     * Creates a new FloatConverter.
     */
    public FloatConverter() {
        super(double.class, Float.class);
    }

    @Override
    public Float decode(Class targetClass, Element val,
            MappedField optionalExtraInfo) throws MappingException {
        if ((val == null) || (val.getType() == ElementType.NULL)) {
            return null;
        }
        else if (val instanceof NumericElement) {
            return Float.valueOf((float) ((NumericElement) val)
                    .getDoubleValue());
        }
        else if (val.getType() == ElementType.STRING) {
            String sVal = ((StringElement) val).getValue();
            return Float.valueOf(Float.parseFloat(sVal));
        }
        else if (val.getType() == ElementType.SYMBOL) {
            String sVal = ((SymbolElement) val).getSymbol();
            return Float.valueOf(Float.parseFloat(sVal));
        }

        throw new MappingException("Could not figure out how to map a "
                + val.getClass().getSimpleName() + " into a Float.");
    }

    @Override
    public void encode(DocumentBuilder builder, String name, Float value,
            MappedField optionalExtraInfo) {
        if (value == null) {
            builder.addNull(name);
        }
        else {
            builder.add(name, value.doubleValue());
        }
    }
}
