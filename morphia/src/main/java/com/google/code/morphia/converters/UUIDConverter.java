/**
 * 
 */
package com.google.code.morphia.converters;

import java.util.UUID;

import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.ElementType;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.allanbank.mongodb.bson.element.StringElement;
import com.allanbank.mongodb.bson.element.SymbolElement;
import com.allanbank.mongodb.bson.element.UuidElement;
import com.google.code.morphia.mapping.MappedField;
import com.google.code.morphia.mapping.MappingException;

/**
 * provided by http://code.google.com/p/morphia/issues/detail?id=320
 * 
 * @author stummb
 * @author scotthernandez
 */
@SuppressWarnings({ "rawtypes" })
public class UUIDConverter extends TypeConverter<UUID> implements
        SimpleValueConverter {

    /**
     * Creates a new UUIDConverter.
     */
    public UUIDConverter() {
        super(UUID.class);
    }

    @Override
    public void encode(DocumentBuilder builder, String name, UUID value,
            MappedField optionalExtraInfo) {
        if (value == null) {
            builder.addNull(name);
        }
        else {
            builder.add(name, value);
        }
    }

    @Override
    public UUID decode(Class targetClass, Element val,
            MappedField optionalExtraInfo) throws MappingException {
        if ((val == null) || (val.getType() == ElementType.NULL)) {
            return null;
        }
        else if (val instanceof UuidElement) {
            return ((UuidElement) val).getUuid();
        }
        else if (val.getType() == ElementType.STRING) {
            String sVal = ((StringElement) val).getValue();
            return UUID.fromString(sVal);
        }
        else if (val.getType() == ElementType.SYMBOL) {
            String sVal = ((SymbolElement) val).getSymbol();
            return UUID.fromString(sVal);
        }

        throw new MappingException("Could not figure out how to map a "
                + val.getClass().getSimpleName() + " into a UUID.");
    }
}