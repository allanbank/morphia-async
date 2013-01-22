/**
 * 
 */
package com.google.code.morphia.converters;

/**
 * ConverterException provides notification of a conversion failure.
 */
public class ConverterException extends RuntimeException {

    /** Serialization version for the class. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new ConverterException.
     * 
     * @param msg
     *            The message for the exception.
     */
    public ConverterException(final String msg) {
        super(msg);
    }
}
