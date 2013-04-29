/**
 * 
 */
package com.google.code.morphia.mapping.validation.classrules;

import java.util.Set;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.mapping.validation.ClassConstraint;
import com.google.code.morphia.mapping.validation.ConstraintViolation;
import com.google.code.morphia.mapping.validation.ConstraintViolation.Level;
import com.google.code.morphia.state.MappedClass;

/**
 * Validates that the {@link MappedClass} does not have both the {@link Entity}
 * and {@link Embedded} annotations.
 * 
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * 
 */
public class EntityAndEmbed implements ClassConstraint {

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to validate that themapped class does not have both the
     * {@link Entity} and {@link Embedded} annotations.
     * </p>
     */
    @Override
    public void check(final MappedClass mc, final Set<ConstraintViolation> ve) {

        if (mc.hasAnnotation(Entity.class) && mc.hasAnnotation(Embedded.class)) {
            ve.add(new ConstraintViolation(Level.FATAL, mc, this.getClass(),
                    "Cannot have both @" + Entity.class.getSimpleName()
                            + " and @" + Embedded.class.getSimpleName()
                            + " annotation at class level."));
        }

    }
}
