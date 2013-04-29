package com.google.code.morphia.query;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.code.morphia.utils.Assert;

public class FieldEndImpl<T extends CriteriaContainerImpl> implements
        FieldEnd<T> {
    private static final Logr log = MorphiaLoggerFactory
            .get(FieldEndImpl.class);

    private final String field;
    private final QueryImpl<?> query;
    private final T target;

    private final boolean validateName;

    public FieldEndImpl(final QueryImpl<?> query, final String field,
            final T target, final boolean validateName) {
        this.query = query;
        this.field = field;
        this.target = target;
        this.validateName = validateName;
    }

    @Override
    public T contains(final String string) {
        Assert.parametersNotNull("val", string);
        return addCrit(FilterOperator.EQUAL, Pattern.compile(string));
    }

    @Override
    public T containsIgnoreCase(final String string) {
        Assert.parametersNotNull("val", string);
        return addCrit(FilterOperator.EQUAL,
                Pattern.compile(string, Pattern.CASE_INSENSITIVE));
    }

    @Override
    public T doesNotExist() {
        return addCrit(FilterOperator.EXISTS, false);
    }

    @Override
    public T endsWith(final String suffix) {
        Assert.parametersNotNull("val", suffix);
        return addCrit(FilterOperator.EQUAL, Pattern.compile(suffix + "$"));
    }

    @Override
    public T endsWithIgnoreCase(final String suffix) {
        Assert.parametersNotNull("val", suffix);
        return addCrit(FilterOperator.EQUAL,
                Pattern.compile(suffix + "$", Pattern.CASE_INSENSITIVE));
    }

    @Override
    public T equal(final Object val) {
        return addCrit(FilterOperator.EQUAL, val);
    }

    @Override
    public T exists() {
        return addCrit(FilterOperator.EXISTS, true);
    }

    @Override
    public T greaterThan(final Object val) {
        Assert.parametersNotNull("val", val);
        return addCrit(FilterOperator.GREATER_THAN, val);
    }

    @Override
    public T greaterThanOrEq(final Object val) {
        Assert.parametersNotNull("val", val);
        return addCrit(FilterOperator.GREATER_THAN_OR_EQUAL, val);
    }

    @Override
    public T hasAllOf(final Iterable<?> vals) {
        Assert.parametersNotNull("vals", vals);
        Assert.parameterNotEmpty(vals, "vals");
        return addCrit(FilterOperator.ALL, vals);
    }

    @Override
    public T hasAnyOf(final Iterable<?> vals) {
        Assert.parametersNotNull("vals", vals);
        // Assert.parameterNotEmpty(vals,"vals"); //it is valid but will never
        // return any results.
        if (log.isWarningEnabled()) {
            if (!vals.iterator().hasNext()) {
                log.warning("Specified an empty list/collection with the '"
                        + field + "' criteria");
            }
        }
        return addCrit(FilterOperator.IN, vals);
    }

    @Override
    public T hasNoneOf(final Iterable<?> vals) {
        Assert.parametersNotNull("vals", vals);
        Assert.parameterNotEmpty(vals, "vals");
        return addCrit(FilterOperator.NOT_IN, vals);
    }

    @Override
    public T hasThisElement(final Object val) {
        Assert.parametersNotNull("val", val);
        return addCrit(FilterOperator.ELEMENT_MATCH, val);
    }

    @Override
    public T hasThisOne(final Object val) {
        return addCrit(FilterOperator.EQUAL, val);
    }

    @Override
    public T in(final Iterable<?> vals) {
        return this.hasAnyOf(vals);
    }

    @Override
    public T lessThan(final Object val) {
        Assert.parametersNotNull("val", val);
        return addCrit(FilterOperator.LESS_THAN, val);
    }

    @Override
    public T lessThanOrEq(final Object val) {
        Assert.parametersNotNull("val", val);
        return addCrit(FilterOperator.LESS_THAN_OR_EQUAL, val);
    }

    @Override
    public T near(final double x, final double y) {
        return near(x, y, false);
    }

    @Override
    public T near(final double x, final double y, final boolean spherical) {
        return addGeoCrit(FilterOperator.NEAR, new double[] { x, y },
                spherical ? opts("$sphere", true) : null);
    }

    @Override
    public T near(final double x, final double y, final double radius) {
        return near(x, y, radius, false);
    }

    @Override
    public T near(final double x, final double y, final double radius,
            final boolean spherical) {
        return addGeoCrit(FilterOperator.NEAR, new double[] { x, y },
                spherical ? opts("$sphere", true, "$maxDistance", radius)
                        : null);
    }

    @Override
    public T notEqual(final Object val) {
        return addCrit(FilterOperator.NOT_EQUAL, val);
    }

    @Override
    public T notIn(final Iterable<?> vals) {
        return this.hasNoneOf(vals);
    }

    @Override
    public T sizeEq(final int val) {
        Assert.parametersNotNull("val", val);
        return addCrit(FilterOperator.SIZE, val);
    }

    @Override
    public T startsWith(final String prefix) {
        Assert.parametersNotNull("val", prefix);
        return addCrit(FilterOperator.EQUAL, Pattern.compile("^" + prefix));
    }

    @Override
    public T startsWithIgnoreCase(final String prefix) {
        Assert.parametersNotNull("val", prefix);
        return addCrit(FilterOperator.EQUAL,
                Pattern.compile("^" + prefix, Pattern.CASE_INSENSITIVE));
    }

    @Override
    public T within(final double x, final double y, final double radius) {
        return within(x, y, radius, false);
    }

    @Override
    public T within(final double x, final double y, final double radius,
            final boolean spherical) {
        return addGeoCrit(FilterOperator.WITHIN_CIRCLE, new Object[] {
                new double[] { x, y }, radius },
                spherical ? opts("$sphere", true) : null);
    }

    @Override
    public T within(final double x1, final double y1, final double x2,
            final double y2) {
        return addGeoCrit(FilterOperator.WITHIN_BOX, new double[][] {
                new double[] { x1, y1 }, new double[] { x2, y2 } }, null);
    }

    /** Add a criteria */
    private T addCrit(final FilterOperator op, final Object val) {
        target.add(new FieldCriteria(query, field, op, val, validateName, query
                .isValidatingTypes()));
        return target;
    }

    private T addGeoCrit(final FilterOperator op, final Object val,
            final Map<String, Object> opts) {
        target.add(new GeoFieldCriteria(query, field, op, val, validateName,
                false, opts));
        return target;
    }

    private Map<String, Object> opts(final String s, final Object v) {
        final Map<String, Object> opts = new HashMap<String, Object>();
        opts.put(s, v);
        return opts;
    }

    private Map<String, Object> opts(final String s1, final Object v1,
            final String s2, final Object v2) {
        final Map<String, Object> opts = new HashMap<String, Object>();
        opts.put(s1, v1);
        opts.put(s2, v2);
        return opts;
    }
}
