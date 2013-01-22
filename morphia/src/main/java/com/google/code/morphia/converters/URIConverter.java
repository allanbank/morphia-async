/**
 * 
 */
package com.google.code.morphia.converters;

import java.net.URI;

import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.ElementType;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.allanbank.mongodb.bson.element.StringElement;
import com.allanbank.mongodb.bson.element.SymbolElement;
import com.google.code.morphia.mapping.MappedField;
import com.google.code.morphia.mapping.MappingException;

/**
 * @author scotthernandez
 */
@SuppressWarnings({ "rawtypes" })
public class URIConverter extends TypeConverter<URI> implements
        SimpleValueConverter {

    /**
     * Creates a new URIConverter.
     */
    public URIConverter() {
        this(URI.class);
    }

    /**
     * Creates a new URIConverter.
     * 
     * @param clazz
     *            The sub type of URI being converted.
     */
    protected URIConverter(Class clazz) {
        super(clazz);
    }

    @Override
    public void encode(DocumentBuilder builder, String name, URI uri,
            MappedField optionalExtraInfo) {
        if (uri == null) {
            builder.addNull(name);
        }
        else {
            builder.add(name, uri.toString().replace(".", "%46"));
        }
    }

    @Override
    public URI decode(Class targetClass, Element val,
            MappedField optionalExtraInfo) throws MappingException {
        if ((val == null) || (val.getType() == ElementType.NULL)) {
            return null;
        }
        else if (val.getType() == ElementType.STRING) {
            String sVal = ((StringElement) val).getValue();
            return URI.create(sVal.replace("%46", "."));
        }
        else if (val.getType() == ElementType.SYMBOL) {
            String sVal = ((SymbolElement) val).getSymbol();
            return URI.create(sVal.replace("%46", "."));
        }

        throw new MappingException("Could not figure out how to map a "
                + val.getClass().getSimpleName() + " into a URI.");
    }
}
