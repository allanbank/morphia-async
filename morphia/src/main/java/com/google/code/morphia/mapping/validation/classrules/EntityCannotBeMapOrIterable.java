/**
 * 
 */
package com.google.code.morphia.mapping.validation.classrules;

import java.util.Map;
import java.util.Set;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.mapping.validation.ClassConstraint;
import com.google.code.morphia.mapping.validation.ConstraintViolation;
import com.google.code.morphia.mapping.validation.ConstraintViolation.Level;
import com.google.code.morphia.state.MappedClass;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * 
 */
public class EntityCannotBeMapOrIterable implements ClassConstraint {

    @Override
    public void check(final MappedClass mc, final Set<ConstraintViolation> ve) {

        if (mc.hasAnnotation(Entity.class)
                && (Map.class.isAssignableFrom(mc.getMappedClass()) || Iterable.class
                        .isAssignableFrom(mc.getMappedClass()))) {
            ve.add(new ConstraintViolation(Level.FATAL, mc, this.getClass(),
                    "Entities cannot implement Map/Iterable"));
        }

    }
}
