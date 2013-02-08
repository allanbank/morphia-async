/**
 * 
 */
package com.google.code.morphia.mapping.validation.fieldrules;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import com.google.code.morphia.annotations.Serialized;
import com.google.code.morphia.mapping.validation.ConstraintViolation;
import com.google.code.morphia.mapping.validation.ConstraintViolation.Level;
import com.google.code.morphia.state.MappedClass;
import com.google.code.morphia.state.MappedField;
import com.google.code.morphia.utils.ReflectionUtils;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * 
 */
public class MapNotSerializable extends FieldConstraint {

    @Override
    protected void check(MappedClass mc, MappedField mf,
            Set<ConstraintViolation> ve) {
        if (Map.class.isAssignableFrom(mf.getResolvedClass())) {
            if (mf.hasAnnotation(Serialized.class)) {
                Class<?> keyClass = ReflectionUtils.getParameterizedClass(
                        mf.getField(), 0);
                Class<?> valueClass = ReflectionUtils.getParameterizedClass(
                        mf.getField(), 1);
                if (keyClass != null) {
                    if (!Serializable.class.isAssignableFrom(keyClass))
                        ve.add(new ConstraintViolation(Level.FATAL, mc, mf,
                                this.getClass(), "Key class ("
                                        + keyClass.getName()
                                        + ") is not Serializable"));
                }
                if (valueClass != null) {
                    if (!Serializable.class.isAssignableFrom(keyClass))
                        ve.add(new ConstraintViolation(Level.FATAL, mc, mf,
                                this.getClass(), "Value class ("
                                        + valueClass.getName()
                                        + ") is not Serializable"));
                }
            }
        }
    }
}
