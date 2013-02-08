/**
 * 
 */
package com.google.code.morphia.converters;

import org.junit.Test;

import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.element.StringElement;
import com.google.code.morphia.TestBase;
import com.google.code.morphia.annotations.Converters;
import com.google.code.morphia.mapping.MappedField;
import com.google.code.morphia.mapping.MappingException;
import com.google.code.morphia.state.MappedClass;
import com.google.code.morphia.testutil.TestEntity;

/**
 * @author Uwe Schaefer
 * 
 */
public class CustomConverterDefault extends TestBase {

    private static class E extends TestEntity {
        private static final long serialVersionUID = 1L;

        // FIXME issue 100 :
        // http://code.google.com/p/morphia/issues/detail?id=100
        // check default inspection: if not declared as property,
        // morphia fails due to defaulting to embedded and expecting a non-arg
        // constructor.
        //
        // @Property
        Foo foo;

    }

    // unknown type to convert
    @Converters(FooConverter.class)
    private static class Foo {
        private String string;

        public Foo(String string) {
            this.string = string;
        }

        @Override
        public String toString() {
            return string;
        }
    }

    /**
     * FooConverter provides a custom converter for testing.
     * 
     * @copyright 2013, Allanbank Consulting, Inc., All Rights Reserved
     */
    public static class FooConverter implements FieldConverter<Foo> {

        /** tracks when a conversion is done. */
        private boolean done = false;

        /**
         * Creates a new FooConverter.
         */
        public FooConverter() {
        }

        public boolean didConversion() {
            return done;
        }

        @Override
        public boolean canConvert(MappedClass clazz,
                com.google.code.morphia.state.MappedField field) {
            return Foo.class.isAssignableFrom(field.getResolvedClass());
        }

        @Override
        public Element toElement(MappedClass clazz,
                com.google.code.morphia.state.MappedField field, String name,
                Foo value) {
            done = true;
            return new StringElement(name, value.toString());
        }

        @Override
        public Foo fromElement(MappedClass clazz,
                com.google.code.morphia.state.MappedField field, Element element) {
            return new Foo(element.getValueAsString());
        }
    }

    @Test
    public void testConversion() throws Exception {
        FooConverter fc = new FooConverter();
        morphia.getMapper().getConverters().addConverter(fc);
        morphia.map(E.class);
        E e = new E();
        e.foo = new Foo("test");
        ds.save(e);

        org.junit.Assert.assertTrue(fc.didConversion());

        e = ds.find(E.class).get();
        org.junit.Assert.assertNotNull(e.foo);
        org.junit.Assert.assertEquals(e.foo.string, "test");

    }

}
