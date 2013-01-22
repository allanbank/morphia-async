/**
 * 
 */
package com.google.code.morphia.converters;

/**
 * Provides notification that a convert is not available for a type.
 * 
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public class ConverterNotFoundException extends RuntimeException {

    /** Serialization version for the class. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new ConverterNotFoundException.
     * 
     * @param msg
     *            The details of the error.
     */
    public ConverterNotFoundException(final String msg) {
        super(msg);
    }
}
