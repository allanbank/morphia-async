/**
 * 
 */
package com.google.code.morphia.converters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.EnumSet;

import org.junit.Test;

import com.google.code.morphia.TestBase;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.state.MappedClassCache;
import com.google.code.morphia.testutil.TestEntity;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * 
 */
public class EnumSetTest extends TestBase {
    public enum NastyEnum {
        A {
            @Override
            public String toString() {
                return "Never use toString for other purposes than debugging";
            }
        },
        B {
            public String toString() {
                return "Never use toString for other purposes than debugging ";
            }
        },
        C, D;
    }

    public static class NastyEnumEntity extends TestEntity {
        /** Serialization version */
        private static final long serialVersionUID = 1L;
        EnumSet<NastyEnum> in = EnumSet.of(NastyEnum.B, NastyEnum.C,
                NastyEnum.D);
        EnumSet<NastyEnum> out = EnumSet.of(NastyEnum.A);
        EnumSet<NastyEnum> empty = EnumSet.noneOf(NastyEnum.class);
        EnumSet<NastyEnum> isNull;
    }

    @Test
    public void testNastyEnumConversion() throws Exception {
        NastyEnumEntity n = new NastyEnumEntity();

        Converter converter = new Converter(new MappedClassCache());
        n = converter.fromDocument(NastyEnumEntity.class,
                converter.toDocument(n));

        assertNull(n.isNull);
        assertNotNull(n.empty);
        assertNotNull(n.in);
        assertNotNull(n.out);

        assertEquals(0, n.empty.size());
        assertEquals(3, n.in.size());
        assertEquals(1, n.out.size());

        assertTrue(n.in.contains(NastyEnum.B));
        assertTrue(n.in.contains(NastyEnum.C));
        assertTrue(n.in.contains(NastyEnum.D));
        assertFalse(n.in.contains(NastyEnum.A));

        assertTrue(n.out.contains(NastyEnum.A));
        assertFalse(n.out.contains(NastyEnum.B));
        assertFalse(n.out.contains(NastyEnum.C));
        assertFalse(n.out.contains(NastyEnum.D));
    }

    @Test
    public void testNastyEnumPerisistence() throws Exception {
        NastyEnumEntity n = new NastyEnumEntity();

        ds.save(n);
        n = ds.get(n);

        assertNull(n.isNull);
        assertNotNull(n.empty);
        assertNotNull(n.in);
        assertNotNull(n.out);

        assertEquals(0, n.empty.size());
        assertEquals(3, n.in.size());
        assertEquals(1, n.out.size());

        assertTrue(n.in.contains(NastyEnum.B));
        assertTrue(n.in.contains(NastyEnum.C));
        assertTrue(n.in.contains(NastyEnum.D));
        assertFalse(n.in.contains(NastyEnum.A));

        assertTrue(n.out.contains(NastyEnum.A));
        assertFalse(n.out.contains(NastyEnum.B));
        assertFalse(n.out.contains(NastyEnum.C));
        assertFalse(n.out.contains(NastyEnum.D));

        Query<NastyEnumEntity> q = ds.find(NastyEnumEntity.class, "in",
                NastyEnum.C);
        assertEquals(1, q.countAll());
        q = ds.find(NastyEnumEntity.class, "out", NastyEnum.C);
        assertEquals(0, q.countAll());

    }
}
