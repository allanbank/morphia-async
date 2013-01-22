/**
 * 
 */
package com.google.code.morphia.converters;

import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.google.code.morphia.mapping.MappedField;
import com.google.code.morphia.mapping.Mapper;
import com.google.code.morphia.mapping.MappingException;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
@SuppressWarnings("rawtypes")
public abstract class TypeConverter<T> {
    protected Mapper mapr;
    protected Class[] supportTypes = null;

    protected TypeConverter() {
    }

    protected TypeConverter(Class... types) {
        supportTypes = types;
    }

    /** returns list of supported convertable types */
    final Class[] getSupportedTypes() {
        return supportTypes;
    }

    /** checks if the class is supported for this converter. */
    final boolean canHandle(Class c) {
        return isSupported(c, null);
    }

    /** checks if the class is supported for this converter. */
    protected boolean isSupported(Class<?> c, MappedField optionalExtraInfo) {
        return false;
    }

    /** checks if the MappedField is supported for this converter. */
    final boolean canHandle(MappedField mf) {
        return isSupported(mf.getType(), mf);
    }

    /**
     * decode the {@link DBObject} and provide the corresponding java
     * (type-safe) object<br>
     * <b>NOTE: optionalExtraInfo might be null</b>
     **/
    public abstract T decode(Class targetClass, Element fromDBObject,
            MappedField optionalExtraInfo) throws MappingException;

    /** checks if Class f is in classes **/
    protected boolean oneOf(Class f, Class... classes) {
        return oneOfClases(f, classes);
    }

    /** checks if Class f is in classes **/
    protected boolean oneOfClases(Class f, Class[] classes) {
        for (Class c : classes) {
            if (c.equals(f))
                return true;
        }
        return false;
    }

    /**
     * encode the (type-safe) java object into the corresponding {@link Element}
     **/
    public void encode(DocumentBuilder builder, String name, T value,
            MappedField optionalExtraInfo) {
        // Use the builder factory as the default implementation.
        builder.add(name, value);
    }

    public void setMapper(Mapper mapr) {
        this.mapr = mapr;
    }
}
