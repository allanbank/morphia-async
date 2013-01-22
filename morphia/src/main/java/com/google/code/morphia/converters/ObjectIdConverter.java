/**
 * 
 */
package com.google.code.morphia.converters;

import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.ElementType;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.allanbank.mongodb.bson.element.ObjectId;
import com.allanbank.mongodb.bson.element.ObjectIdElement;
import com.google.code.morphia.mapping.MappedField;
import com.google.code.morphia.mapping.MappingException;

/**
 * Convert to an ObjectId from string
 * 
 * @author scotthernandez
 */
@SuppressWarnings({ "rawtypes" })
public class ObjectIdConverter extends TypeConverter<ObjectId> implements
        SimpleValueConverter {

    /**
     * Creates a new ObjectIdConverter.
     */
    public ObjectIdConverter() {
        super(ObjectId.class);
    }

    @Override
    public void encode(DocumentBuilder builder, String name, ObjectId value,
            MappedField optionalExtraInfo) {
        if (value == null) {
            builder.addNull(name);
        }
        else {
            builder.add(name, value);
        }
    }

    @Override
    public ObjectId decode(Class targetClass, Element val,
            MappedField optionalExtraInfo) throws MappingException {
        if ((val == null) || (val.getType() == ElementType.NULL)) {
            return null;
        }
        else if (val.getType() == ElementType.OBJECT_ID) {
            return ((ObjectIdElement) val).getId();
        }

        // TODO Support an Object from a string.
        // else if (val.getType() == ElementType.STRING) {
        // String sVal = ((StringElement) val).getValue();
        // return new ObjectId(sVal);
        // }
        // else if (val.getType() == ElementType.SYMBOL) {
        // String sVal = ((SymbolElement) val).getSymbol();
        // return new ObjectId(sVal);
        // }

        throw new MappingException("Could not figure out how to map a "
                + val.getClass().getSimpleName() + " into a UUID.");
    }
}
