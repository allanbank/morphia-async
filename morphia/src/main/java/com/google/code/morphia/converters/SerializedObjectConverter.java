/**
 * 
 */
package com.google.code.morphia.converters;

import java.io.IOException;
import java.io.Serializable;

import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.ElementType;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.allanbank.mongodb.bson.element.BinaryElement;
import com.allanbank.mongodb.bson.element.SymbolElement;
import com.google.code.morphia.annotations.Serialized;
import com.google.code.morphia.mapping.MappedField;
import com.google.code.morphia.mapping.MappingException;
import com.google.code.morphia.mapping.Serializer;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
@SuppressWarnings("rawtypes")
public class SerializedObjectConverter extends TypeConverter<Serializable> {
    @Override
    protected boolean isSupported(Class c, MappedField optionalExtraInfo) {
        return (optionalExtraInfo != null)
                && (optionalExtraInfo.hasAnnotation(Serialized.class));
    }

    @Override
    public Serializable decode(Class targetClass, Element val, MappedField f)
            throws MappingException {
        if ((val == null) || (val.getType() == ElementType.NULL)) {
            return null;
        }
        else if (val.getType() == ElementType.BINARY) {
            try {
                boolean useCompression = !f.getAnnotation(Serialized.class)
                        .disableCompression();
                return (Serializable) Serializer.deserialize(
                        ((BinaryElement) val).getValue(), useCompression);
            }
            catch (IOException e) {
                throw new MappingException("While deserializing to "
                        + f.getFullName(), e);
            }
            catch (ClassNotFoundException e) {
                throw new MappingException("While deserializing to "
                        + f.getFullName(), e);
            }
        }
        else if (val.getType() == ElementType.SYMBOL) {
            return ((SymbolElement) val).getSymbol();
        }

        throw new MappingException("Could not figure out how to map a "
                + val.getClass().getSimpleName()
                + " into a Serialized Object of type " + targetClass.getName());
    }

    @Override
    public void encode(DocumentBuilder builder, String name,
            Serializable value, MappedField f) {
        if (value == null) {
            builder.addNull(name);
        }
        else {
            try {
                boolean useCompression = !f.getAnnotation(Serialized.class)
                        .disableCompression();
                builder.add(name, Serializer.serialize(value, useCompression));
            }
            catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

}
