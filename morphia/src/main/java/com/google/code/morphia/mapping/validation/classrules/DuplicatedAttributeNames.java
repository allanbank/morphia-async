package com.google.code.morphia.mapping.validation.classrules;

import java.util.HashSet;
import java.util.Set;

import com.google.code.morphia.mapping.validation.ClassConstraint;
import com.google.code.morphia.mapping.validation.ConstraintViolation;
import com.google.code.morphia.mapping.validation.ConstraintViolation.Level;
import com.google.code.morphia.state.MappedClass;
import com.google.code.morphia.state.MappedField;

/**
 * @author josephpachod
 */
public class DuplicatedAttributeNames implements ClassConstraint {

    @Override
    public void check(final MappedClass mc, final Set<ConstraintViolation> ve) {
        final Set<String> foundNames = new HashSet<String>();
        final Set<String> duplicates = new HashSet<String>();
        for (final MappedField mappedField : mc.getFields()) {
            for (final String name : mappedField.getAlsoLoadNames()) {
                // if (duplicates.contains(name)) {
                // continue;
                // }
                if (!foundNames.add(name)) {
                    ve.add(new ConstraintViolation(
                            Level.FATAL,
                            mc,
                            mappedField,
                            this.getClass(),
                            "Mapping to MongoDB field name '"
                                    + name
                                    + "' is duplicated; you cannot map different "
                                    + "java fields to the same MongoDB field."));
                    duplicates.add(name);
                }
            }
        }
    }
}
