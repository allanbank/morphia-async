/**
 * 
 */
package com.google.code.morphia.mapping.validation.classrules;

import java.util.Set;

import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.mapping.validation.ClassConstraint;
import com.google.code.morphia.mapping.validation.ConstraintViolation;
import com.google.code.morphia.mapping.validation.ConstraintViolation.Level;
import com.google.code.morphia.state.MappedClass;
import com.google.code.morphia.state.MappedField;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public class MultipleId implements ClassConstraint {

    public void check(MappedClass mc, Set<ConstraintViolation> ve) {

        MappedField idField = null;
        for (MappedField f : mc.getFields()) {
            if (f.hasAnnotation(Id.class)) {
                if (idField == null) {
                    idField = f;
                }
                else {
                    ve.add(new ConstraintViolation(Level.FATAL, mc, this
                            .getClass(), "More than one @"
                            + Id.class.getSimpleName() + " Field found ("
                            + idField.getField().getName() + ", and "
                            + f.getField().getName() + ")."));
                }
            }
        }
    }

}
