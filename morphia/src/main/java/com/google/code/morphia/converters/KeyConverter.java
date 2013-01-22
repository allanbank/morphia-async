/**
 * 
 */
package com.google.code.morphia.converters;

import com.allanbank.mongodb.bson.DocumentReference;
import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.ElementType;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.allanbank.mongodb.bson.element.DocumentElement;
import com.google.code.morphia.Key;
import com.google.code.morphia.mapping.MappedField;
import com.google.code.morphia.mapping.MappingException;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @author scotthernandez
 */
@SuppressWarnings({ "rawtypes" })
public class KeyConverter extends TypeConverter<Key> {

    /**
     * Creates a new KeyConverter.
     */
    public KeyConverter() {
        super(Key.class);
    }

    @Override
    public Key decode(Class targetClass, Element o,
            MappedField optionalExtraInfo) throws MappingException {

        DocumentReference ref = null;
        if (o == null) {
            return null;
        }
        else if (o.getType() == ElementType.DOCUMENT) {
            ref = ((DocumentElement) o).asDocumentReference();
        }

        if (ref == null) {
            throw new ConverterException(String.format(
                    "cannot convert %s to Key because it isn't a DBRef",
                    o.toString()));
        }
        return mapr.refToKey((DocumentReference) o);
    }

    @Override
    public void encode(DocumentBuilder builder, String name, Key key,
            MappedField optionalExtraInfo) {
        if (key == null) {
            builder.addNull(name);
        }
        else {
            builder.add(name, mapr.keyToRef(key));
        }
    }

}
