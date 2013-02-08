/**
 * 
 */
package com.google.code.morphia.mapping.validation.fieldrules;

import java.util.Set;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Property;
import com.google.code.morphia.annotations.Reference;
import com.google.code.morphia.mapping.validation.ConstraintViolation;
import com.google.code.morphia.mapping.validation.ConstraintViolation.Level;
import com.google.code.morphia.state.MappedClass;
import com.google.code.morphia.state.MappedField;

/** @author ScottHenandez */
public class IdDoesNotMix extends FieldConstraint {

    @Override
    protected void check(MappedClass mc, MappedField mf,
            Set<ConstraintViolation> ve) {
        // an @Id field can not be a Value, Reference, or Embedded
        if (mf.hasAnnotation(Id.class))
            if (mf.hasAnnotation(Reference.class)
                    || mf.hasAnnotation(Embedded.class)
                    || mf.hasAnnotation(Property.class))
                ve.add(new ConstraintViolation(Level.FATAL, mc, mf, this
                        .getClass(), mf.getField().getName()
                        + " is annotated as @" + Id.class.getSimpleName()
                        + " and cannot be mixed with "
                        + "other annotations (like @Reference)"));
    }
}
