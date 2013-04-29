/**
 * 
 */
package com.google.code.morphia.mapping.lazy.proxy;

/**
 * @author Uwe Schäfer, (schaefer@thomas-daily.de)
 * 
 */
@SuppressWarnings("unchecked")
public interface ProxiedReference {
    Class __getReferenceObjClass();

    boolean __isFetched();

    Object __unwrap();
}
