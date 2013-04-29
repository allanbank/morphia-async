package com.google.code.morphia.query;

import java.util.List;

/**
 * <p>
 * A nicer interface to the update operations in monogodb. All these operations
 * happen at the server and can cause the server and client version of the
 * Entity to be different
 * </p>
 **/
public interface UpdateOperations<T> {
    /** adds the value to an array field */
    UpdateOperations<T> add(String fieldExpr, Object value);

    UpdateOperations<T> add(String fieldExpr, Object value, boolean addDups);

    /** adds the values to an array field */
    UpdateOperations<T> addAll(String fieldExpr, List<?> values, boolean addDups);

    /** decrements the numeric field by 1 */
    UpdateOperations<T> dec(String fieldExpr);

    /** Turns off validation (for all calls made after) */
    UpdateOperations<T> disableValidation();

    /**
     * Turns on validation (for all calls made after); by default validation is
     * on
     */
    UpdateOperations<T> enableValidation();

    /** increments the numeric field by 1 */
    UpdateOperations<T> inc(String fieldExpr);

    /** increments the numeric field by value (negatives are allowed) */
    UpdateOperations<T> inc(String fieldExpr, Number value);

    /** Enables isolation (so this update happens in one shot, without yielding) */
    UpdateOperations<T> isolated();

    /** removes the values from the array field */
    UpdateOperations<T> removeAll(String fieldExpr, List<?> values);

    /** removes the value from the array field */
    UpdateOperations<T> removeAll(String fieldExpr, Object value);

    /** removes the first value from the array */
    UpdateOperations<T> removeFirst(String fieldExpr);

    /** removes the last value from the array */
    UpdateOperations<T> removeLast(String fieldExpr);

    /** sets the field value */
    UpdateOperations<T> set(String fieldExpr, Object value);

    /** removes the field */
    UpdateOperations<T> unset(String fieldExpr);
}
