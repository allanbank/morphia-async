/**
 * 
 */
package com.google.code.morphia.converters;

import java.util.Locale;
import java.util.StringTokenizer;

import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.ElementType;
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
public class LocaleConverter extends TypeConverter<Locale> implements
        SimpleValueConverter {

    /**
     * Creates a new LocaleConverter.
     */
    public LocaleConverter() {
        super(Locale.class);
    }

    @Override
    public Locale decode(Class targetClass, Element val,
            MappedField optionalExtraInfo) throws MappingException {
        if ((val == null) || (val.getType() == ElementType.NULL)) {
            return null;
        }
        else if (val.getType() == ElementType.STRING) {
            return parseLocale(((StringElement) val).getValue());
        }
        else if (val.getType() == ElementType.SYMBOL) {
            return parseLocale(((SymbolElement) val).getSymbol());
        }

        throw new MappingException("Could not figure out how to map a "
                + val.getClass().getSimpleName() + " into a URI.");
    }

    @Override
    public void encode(DocumentBuilder builder, String name, Locale value,
            MappedField optionalExtraInfo) {
        if (value == null) {
            builder.addNull(name);
        }
        else {
            builder.add(name, value.toString());
        }
    }

    /**
     * Parse the string into a Locale.
     * 
     * @param localeString
     *            The Locale string to parse.
     * @return The parsed locale.
     */
    public static Locale parseLocale(final String localeString) {
        if ((localeString != null) && (localeString.length() > 0)) {
            StringTokenizer st = new StringTokenizer(localeString, "_");
            String language = st.hasMoreElements() ? st.nextToken() : Locale
                    .getDefault().getLanguage();
            String country = st.hasMoreElements() ? st.nextToken() : "";
            String variant = st.hasMoreElements() ? st.nextToken() : "";
            return new Locale(language, country, variant);
        }
        return null;
    }
}
