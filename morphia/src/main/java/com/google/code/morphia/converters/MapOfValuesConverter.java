/**
 * 
 */
package com.google.code.morphia.converters;

import java.util.Map;

import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.ElementType;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.allanbank.mongodb.bson.element.DocumentElement;
import com.google.code.morphia.mapping.MappedField;
import com.google.code.morphia.mapping.MappingException;
import com.google.code.morphia.utils.ReflectionUtils;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class MapOfValuesConverter extends TypeConverter<Map<?, ?>> {
    /** The converter for sub fields. */
    private final DefaultConverters converters;

    /**
     * Creates a new MapOfValuesConverter.
     * 
     * @param converters
     *            The converter for sub fields.
     */
    public MapOfValuesConverter(DefaultConverters converters) {
        this.converters = converters;
    }

    @Override
    protected boolean isSupported(Class<?> c, MappedField optionalExtraInfo) {
        if (optionalExtraInfo != null) {
            return optionalExtraInfo.isMap();
        }
        return ReflectionUtils.implementsInterface(c, Map.class);
    }

    @Override
    public Map<?, ?> decode(Class targetClass, Element val, final MappedField mf)
            throws MappingException {
        if ((val == null) || (val.getType() == ElementType.NULL)) {
            return null;
        }
        else if (val.getType() == ElementType.DOCUMENT) {
            final Map values = mapr.getOptions().objectFactory.createMap(mf);

            for (Element e : ((DocumentElement) val)) {
                // FIXME - Handle maps with non string keys.
                // Object objKey = converters.decode(mf.getMapKeyClass(),key);
                values.put(e.getName(), converters.decode(mf.getSubClass(), e));
            }
            return values;
        }

        throw new MappingException("Could not figure out how to map a "
                + val.getClass().getSimpleName() + " into a Map.");
    }

    @Override
    public void encode(DocumentBuilder builder, String name, Map<?, ?> value,
            MappedField mf) {
        if (value == null) {
            builder.addNull(name);
        }
        else {
            DocumentBuilder subDoc = builder.push(mf.getNameToStore());
            for (Map.Entry entry : value.entrySet()) {
                // FIXME - Handle maps with non string keys.
                // Object objKey = converters.decode(mf.getMapKeyClass(),key);
                String strKey = entry.getKey().toString();
                converters.encode(subDoc, entry.getValue(), strKey, null);
            }
        }
    }
}