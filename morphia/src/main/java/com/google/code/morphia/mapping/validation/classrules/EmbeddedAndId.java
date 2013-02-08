/**
 * 
 */
package com.google.code.morphia.mapping.validation.classrules;

import java.util.Set;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.mapping.validation.ClassConstraint;
import com.google.code.morphia.mapping.validation.ConstraintViolation;
import com.google.code.morphia.mapping.validation.ConstraintViolation.Level;
import com.google.code.morphia.state.MappedClass;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * 
 */
public class EmbeddedAndId implements ClassConstraint {

    public void check(MappedClass mc, Set<ConstraintViolation> ve) {
        Class<?> clazz = mc.getMappedClass();
        Embedded embedded = clazz.getAnnotation(Embedded.class);
        if ((embedded != null) && (mc.getIdField() != null)) {
            ve.add(new ConstraintViolation(Level.FATAL, mc, this.getClass(),
                    "@" + Embedded.class.getSimpleName()
                            + " classes cannot specify a @Id field"));
        }
    }

}
