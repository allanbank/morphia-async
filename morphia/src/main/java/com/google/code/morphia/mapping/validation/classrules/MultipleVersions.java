/**
 * 
 */
package com.google.code.morphia.mapping.validation.classrules;

import java.util.Set;

import com.google.code.morphia.annotations.Version;
import com.google.code.morphia.mapping.validation.ClassConstraint;
import com.google.code.morphia.mapping.validation.ConstraintViolation;
import com.google.code.morphia.mapping.validation.ConstraintViolation.Level;
import com.google.code.morphia.state.MappedClass;
import com.google.code.morphia.state.MappedField;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * 
 */
public class MultipleVersions implements ClassConstraint {

    @Override
    public void check(final MappedClass mc, final Set<ConstraintViolation> ve) {
        MappedField versionField = null;
        for (final MappedField f : mc.getFields()) {
            if (f.hasAnnotation(Version.class)) {
                if (versionField == null) {
                    versionField = f;
                }
                else {
                    ve.add(new ConstraintViolation(Level.FATAL, mc, this
                            .getClass(), "Multiple @" + Version.class
                            + " annotations are not allowed. ("
                            + versionField.getField().getName() + ", and "
                            + f.getField().getName() + ")."));
                }
            }
        }
    }
}
