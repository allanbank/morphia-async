/**
 * 
 */
package com.google.code.morphia.mapping.validation.fieldrules;

import java.util.Set;

import com.google.code.morphia.mapping.validation.ClassConstraint;
import com.google.code.morphia.mapping.validation.ConstraintViolation;
import com.google.code.morphia.state.MappedClass;
import com.google.code.morphia.state.MappedField;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * 
 */
public abstract class FieldConstraint implements ClassConstraint {
    public final void check(MappedClass mc, Set<ConstraintViolation> ve) {
        for (MappedField mf : mc.getFields()) {
            check(mc, mf, ve);
        }
    }

    protected abstract void check(MappedClass mc, MappedField mf,
            Set<ConstraintViolation> ve);

}
