package com.google.code.morphia.mapping.validation;

import com.google.code.morphia.state.MappedClass;
import com.google.code.morphia.state.MappedField;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public class ConstraintViolation {
    private final MappedClass clazz;

    private MappedField field = null;
    private final Level level;
    private final String message;
    private final Class<? extends ClassConstraint> validator;

    public ConstraintViolation(final Level level, final MappedClass clazz,
            final Class<? extends ClassConstraint> validator,
            final String message) {
        this.level = level;
        this.clazz = clazz;
        this.message = message;
        this.validator = validator;
    }

    public ConstraintViolation(final Level level, final MappedClass clazz,
            final MappedField field,
            final Class<? extends ClassConstraint> validator,
            final String message) {
        this(level, clazz, validator, message);
        this.field = field;
    }

    public Level getLevel() {
        return level;
    }

    public String getPrefix() {
        final String fn = (field != null) ? field.getField().getName() : "";
        return clazz.getMappedClass().getName() + "." + fn;
    }

    public String render() {
        return String.format("%s complained about %s : %s",
                validator.getSimpleName(), getPrefix(), message);
    }

    public enum Level {
        FATAL, INFO, MINOR, SEVERE, WARNING;
    }
}