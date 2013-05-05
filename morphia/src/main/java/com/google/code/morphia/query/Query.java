package com.google.code.morphia.query;

import com.allanbank.mongodb.bson.DocumentAssignable;

/**
 * @author Scott Hernandez
 */
public interface Query<T> extends QueryResults<T>, Cloneable, DocumentAssignable {
    CriteriaContainer and(Criteria... criteria);

    /**
     * Batch-size of the fetched result (cursor).
     * 
     * @param value
     *            must be >= 0. A value of 0 indicates the server default.
     */
    Query<T> batchSize(int value);

    Query<T> clone();

    /** Criteria builder interface */
    FieldEnd<? extends CriteriaContainerImpl> criteria(String field);

    /**
     * Disable snapshotted mode (default mode). This will be faster but changes
     * made during the cursor may cause duplicates.
     **/
    Query<T> disableSnapshotMode();

    /** Disables cursor timeout on server. */
    Query<T> disableTimeout();

    /** Turns off validation (for all calls made after) */
    Query<T> disableValidation();

    /**
     * Enabled snapshotted mode where duplicate results (which may be updated
     * during the lifetime of the cursor) will not be returned. Not compatible
     * with order/sort and hint.
     **/
    Query<T> enableSnapshotMode();

    /** Enables cursor timeout on server. */
    Query<T> enableTimeout();

    /**
     * Turns on validation (for all calls made after); by default validation is
     * on
     */
    Query<T> enableValidation();

    /**
     * Fluent query interface:
     * {@code createQuery(Ent.class).field("count").greaterThan(7)...}
     */
    FieldEnd<? extends Query<T>> field(String field);

    /**
     * <p>
     * Create a filter based on the specified condition and value.
     * </p>
     * <p>
     * <b>Note</b>: Property is in the form of "name op" ("age >").
     * </p>
     * <p>
     * Valid operators are ["=", "==","!=", "<>", ">", "<", ">=", "<=", "in",
     * "nin", "all", "size", "exists"]
     * </p>
     * <p>
     * Examples:
     * </p>
     * 
     * <ul>
     * <li>{@code filter("yearsOfOperation >", 5)}</li>
     * <li>{@code filter("rooms.maxBeds >=", 2)}</li>
     * <li>{@code filter("rooms.bathrooms exists", 1)}</li>
     * <li>{@code filter("stars in", new Long[] 3,4}) //3 and 4 stars
     * (midrange?)}</li>
     * <li>{@code filter("age >=", age)}</li>
     * <li>{@code filter("age =", age)}</li>
     * <li>{@code filter("age", age)} (if no operator, = is assumed)</li>
     * <li>{@code filter("age !=", age)}</li>
     * <li>{@code filter("age in", ageList)}</li>
     * <li>{@code filter("customers.loyaltyYears in", yearsList)}</li>
     * </ul>
     * 
     * <p>
     * You can filter on id properties <strong>if</strong> this query is
     * restricted to a Class<T>.
     */
    Query<T> filter(String condition, Object value);

    Class<T> getEntityClass();

    /** Hints as to which index should be used. */
    Query<T> hintIndex(String idxName);

    /**
     * Limit the fetched result set to a certain number of values.
     * 
     * @param value
     *            must be >= 0. A value of 0 indicates no limit.
     */
    Query<T> limit(int value);

    /**
     * Starts the query results at a particular zero-based offset.
     * 
     * @param value
     *            must be >= 0
     */
    Query<T> offset(int value);

    CriteriaContainer or(Criteria... criteria);

    /**
     * <p>
     * Sorts based on a property (defines return order). Examples:
     * </p>
     * 
     * <ul>
     * <li>{@code order("age")}</li>
     * <li>{@code order("-age")} (descending order)</li>
     * <li>{@code order("age,date")}</li>
     * <li>{@code order("age,-date")} (age ascending, date descending)</li>
     * </ul>
     */
    Query<T> order(String condition);

    /** Route query to non-primary node */
    Query<T> queryNonPrimary();

    /** Route query to primary node */
    Query<T> queryPrimaryOnly();

    /** Limits the fields retrieved */
    Query<T> retrievedFields(boolean include, String... fields);

    @Deprecated
    Query<T> skip(int value);

    /**
     * <p>
     * Generates a string that consistently and uniquely specifies this query.
     * There is no way to convert this string back into a query and there is no
     * guarantee that the string will be consistent across versions.
     * </p>
     * 
     * <p>
     * In particular, this value is useful as a key for a simple memcache query
     * cache.
     * </p>
     */
    @Override
    String toString();

    /** Limit the query using this javascript block; only one per query */
    Query<T> where(String js);

    /** Limit the query using this javascript block; only one per query */
    Query<T> where(String js, DocumentAssignable scope);
}