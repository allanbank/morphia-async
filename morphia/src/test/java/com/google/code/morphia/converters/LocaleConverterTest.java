/*
 *         Copyright 2010-2013 Uwe Schaefer
 *          and Allanbank Consulting, Inc.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package com.google.code.morphia.converters;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Locale;

import org.junit.Test;

import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.element.DocumentElement;
import com.allanbank.mongodb.bson.element.NullElement;
import com.allanbank.mongodb.bson.element.SymbolElement;
import com.google.code.morphia.converters.primitive.LocaleConverter;
import com.google.code.morphia.mapping.MappingException;

/**
 * Tests for the {@link LocaleConverter} class.
 * 
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @copyright 2010-2013, Uwe Schaefer, Scott Hernandez and Allanbank Consulting,
 *            Inc., All Rights Reserved
 */
public class LocaleConverterTest {
    /**
     * Tests for {@link Locale} conversion.
     */
    @Test
    public void testConversion() {
        LocaleConverter converter = new LocaleConverter();

        Locale l = Locale.CANADA_FRENCH;
        Locale l2 = converter.fromElement(Locale.class,
                converter.toElement(Locale.class, "f", l));
        assertEquals(l, l2);
    }

    /**
     * Tests for {@link Locale} conversion.
     */
    @Test
    public void testConversionWithSymbol() {
        LocaleConverter converter = new LocaleConverter();

        Locale l = Locale.CANADA_FRENCH;
        Locale l2 = converter.fromElement(Locale.class, new SymbolElement("f",
                converter.toElement(Locale.class, "f", l).getValueAsString()));
        assertEquals(l, l2);
    }

    /**
     * Tests for {@link Locale} conversion.
     */
    @Test(expected = MappingException.class)
    public void testConversionFails() {
        LocaleConverter converter = new LocaleConverter();
        converter.fromElement(Locale.class, new DocumentElement("f"));
        fail("Should have thrown a MappingException.");
    }

    /**
     * Tests for custom {@link Locale} conversion.
     */
    @Test
    public void testConvertCustomLocale() {
        LocaleConverter converter = new LocaleConverter();

        Locale l = new Locale("de", "DE", "bavarian");
        Locale l2 = converter.fromElement(Locale.class,
                converter.toElement(Locale.class, "f", l));
        assertEquals(l, l2);
        assertEquals("de", l2.getLanguage());
        assertEquals("DE", l2.getCountry());
        assertEquals("bavarian", l2.getVariant());
    }

    /**
     * Tests for {@link Locale} <code>null</code> conversion.
     */
    @Test
    public void testConvertNull() {
        LocaleConverter converter = new LocaleConverter();

        assertThat(converter.toElement(Locale.class, "f", null),
                is((Element) new NullElement("f")));

        assertThat(converter.fromElement(Locale.class, new NullElement("f")),
                nullValue(Locale.class));
        assertThat(converter.fromElement(Locale.class, null),
                nullValue(Locale.class));
    }
}
