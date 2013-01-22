/**
 * 
 */
package com.google.code.morphia.converters;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.ElementType;
import com.allanbank.mongodb.bson.builder.ArrayBuilder;
import com.allanbank.mongodb.bson.builder.BuilderFactory;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.allanbank.mongodb.bson.element.ArrayElement;
import com.google.code.morphia.ObjectFactory;
import com.google.code.morphia.mapping.MappedField;
import com.google.code.morphia.mapping.MappingException;
import com.google.code.morphia.utils.ReflectionUtils;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @author scotthernandez
 */

@SuppressWarnings({ "unchecked", "rawtypes" })
public class IterableConverter extends TypeConverter<Object> {
    /** The converters for sub-fields. */
    private final DefaultConverters chain;

    /**
     * Creates a new IterableConverter.
     * 
     * @param chain
     *            The converters for sub-fields.
     */
    public IterableConverter(DefaultConverters chain) {
        this.chain = chain;
    }

    @Override
    protected boolean isSupported(Class c, MappedField mf) {
        if (mf != null) {
            return mf.isMultipleValues() && !mf.isMap(); // &&
                                                         // !mf.isTypeMongoCompatible();
        }
        return c.isArray()
                || ReflectionUtils.implementsInterface(c, Iterable.class);
    }

    @Override
    public Object decode(Class targetClass, Element element, MappedField mf)
            throws MappingException {
        if ((element == null) || (element.getType() == ElementType.NULL)) {
            return null;
        }

        Class subtypeDest = mf.getSubClass();
        Collection vals = createNewCollection(mf);
        if (element.getType() == ElementType.ARRAY) {
            for (Element o : ((ArrayElement) element).getEntries()) {
                vals.add(chain.decode(
                        (subtypeDest != null) ? subtypeDest : o.getClass(), o));
            }
        }
        else {
            // Single value case
            vals.add(chain.decode(
                    (subtypeDest != null) ? subtypeDest : element.getClass(),
                    element));
        }

        // convert to and array if that is the destination type (not a
        // list/set)
        if (mf.getType().isArray()) {
            return ReflectionUtils.convertToArray(subtypeDest, (List) vals);
        }
        return vals;
    }

    /**
     * Create the appropriate type of collections to be used.
     * 
     * @param mf
     *            The field information.
     * @return The created collection.
     */
    private Collection<?> createNewCollection(final MappedField mf) {
        ObjectFactory of = mapr.getOptions().objectFactory;
        return mf.isSet() ? of.createSet(mf) : of.createList(mf);
    }

    @Override
    public void encode(DocumentBuilder builder, String name, Object value,
            MappedField mf) {

        if (value == null) {
            builder.addNull(name);
        }
        else if (value instanceof byte[]) {
            builder.add(name, (byte[]) value);
        }
        else {
            Iterable<?> iterableValues = null;
            if (value.getClass().isArray()) {
                if (value.getClass().getComponentType().isPrimitive()) {
                    final int length = Array.getLength(value);
                    Object[] upCast = new Object[Array.getLength(value)];
                    for (int i = 0; i < length; ++i) {
                        upCast[i] = Array.get(value, i);
                    }
                    iterableValues = Arrays.asList(upCast);
                }
                else {
                    iterableValues = Arrays.asList((Object[]) value);
                }
            }
            else {
                if (!(value instanceof Iterable))
                    throw new ConverterException("Cannot cast "
                            + value.getClass()
                            + " to Iterable for MappedField: " + mf);

                // cast value to a common interface
                iterableValues = (Iterable<?>) value;
            }

            // Create a temp doc.
            DocumentBuilder doc = BuilderFactory.start();
            if (mf != null && mf.getSubClass() != null) {
                int i = 0;
                for (Object o : iterableValues) {
                    chain.encode(doc, mf.getSubClass(), o, String.valueOf(i++),
                            null);
                }
            }
            else {
                int i = 0;
                for (Object o : iterableValues) {
                    chain.encode(doc, o, String.valueOf(i++), null);
                }
            }

            List<Element> elements = doc.build().getElements();
            if (!elements.isEmpty() || mapr.getOptions().storeEmpties) {
                // Now convert to an array.
                ArrayBuilder ab = builder.pushArray(name);
                for (Element e : elements) {
                    ab.add(e);
                }
            }
        }
    }
}
