/**
 * 
 */
package com.google.code.morphia.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.code.morphia.converters.Converter;
import com.google.code.morphia.state.MappedField;

/**
 * 
 * @author Scott Hernandez
 */
public class UpdateOpsImpl<T> implements UpdateOperations<T> {
    Class<T> clazz;
    boolean isolated = false;
    Converter converter;
    Map<String, Map<String, Object>> ops = new HashMap<String, Map<String, Object>>();
    boolean validateNames = true;
    boolean validateTypes = true;

    public UpdateOpsImpl(final Class<T> type, final Converter converter) {
        this.converter = converter;
        this.clazz = type;
    }

    @Override
    public UpdateOperations<T> add(final String fieldExpr, final Object value) {
        return add(fieldExpr, value, false);
    }

    @Override
    public UpdateOperations<T> add(final String fieldExpr, final Object value,
            final boolean addDups) {
        if (value == null) {
            throw new QueryException("Value cannot be null.");
        }

        // Object dbObj = mapr.toMongoObject(value, true);
        add((addDups) ? UpdateOperator.PUSH : UpdateOperator.ADD_TO_SET,
                fieldExpr, value, true);
        return this;
    }

    @Override
    public UpdateOperations<T> addAll(final String fieldExpr,
            final List<?> values, final boolean addDups) {
        if ((values == null) || values.isEmpty()) {
            throw new QueryException("Values cannot be null or empty.");
        }

        // List<?> convertedValues = (List<?>)mapr.toMongoObject(values, true);
        if (addDups) {
            add(UpdateOperator.PUSH_ALL, fieldExpr, values, true);
        }
        else {
            add(UpdateOperator.ADD_TO_SET_EACH, fieldExpr, values, true);
        }
        return this;
    }

    @Override
    public UpdateOperations<T> dec(final String fieldExpr) {
        return inc(fieldExpr, -1);
    }

    @Override
    public UpdateOperations<T> disableValidation() {
        validateNames = validateTypes = false;
        return this;
    }

    @Override
    public UpdateOperations<T> enableValidation() {
        validateNames = validateTypes = true;
        return this;
    }

    public Map<String, Map<String, Object>> getOps() {
        return ops;
    }

    @Override
    public UpdateOperations<T> inc(final String fieldExpr) {
        return inc(fieldExpr, 1);
    }

    @Override
    public UpdateOperations<T> inc(final String fieldExpr, final Number value) {
        if (value == null) {
            throw new QueryException("Value cannot be null.");
        }
        add(UpdateOperator.INC, fieldExpr, value, false);
        return this;
    }

    public boolean isIsolated() {
        return isolated;
    }

    @Override
    public UpdateOperations<T> isolated() {
        isolated = true;
        return this;
    }

    @Override
    public UpdateOperations<T> removeAll(final String fieldExpr,
            final List<?> values) {
        if ((values == null) || values.isEmpty()) {
            throw new QueryException("Value cannot be null or empty.");
        }

        // List<Object> vals = toDBObjList(values);
        add(UpdateOperator.PULL_ALL, fieldExpr, values, true);
        return this;
    }

    @Override
    public UpdateOperations<T> removeAll(final String fieldExpr,
            final Object value) {
        if (value == null) {
            throw new QueryException("Value cannot be null.");
        }
        // Object dbObj = mapr.toMongoObject(value);
        add(UpdateOperator.PULL, fieldExpr, value, true);
        return this;
    }

    @Override
    public UpdateOperations<T> removeFirst(final String fieldExpr) {
        return remove(fieldExpr, true);
    }

    @Override
    public UpdateOperations<T> removeLast(final String fieldExpr) {
        return remove(fieldExpr, false);
    }

    @Override
    public UpdateOperations<T> set(final String fieldExpr, final Object value) {
        if (value == null) {
            throw new QueryException("Value cannot be null.");
        }

        // Object dbObj = mapr.toMongoObject(value, true);
        add(UpdateOperator.SET, fieldExpr, value, true);
        return this;
    }

    @SuppressWarnings("unchecked")
    public void setOps(final Map<String, Map<String, Object>> ops) {
        this.ops = ops;
    }

    @Override
    public UpdateOperations<T> unset(final String fieldExpr) {
        add(UpdateOperator.UNSET, fieldExpr, 1, false);
        return this;
    }

    // TODO Clean this up a little.
    protected void add(final UpdateOperator op, String f, final Object value,
            final boolean convert) {
        if (value == null) {
            throw new QueryException("Val cannot be null");
        }

        Object val = null;
        MappedField mf = null;
        if (validateNames || validateTypes) {
            final StringBuffer sb = new StringBuffer(f);
            mf = Mapper.validate(clazz, mapr, sb, FilterOperator.EQUAL, val,
                    validateNames, validateTypes);
            f = sb.toString();
        }

        if (convert) {
            if (UpdateOperator.PULL_ALL.equals(op) && (value instanceof List)) {
                val = toDBObjList(mf, (List<?>) value);
            }
            else {
                val = mapr.toMongoObject(mf, null, value);
            }
        }

        if (UpdateOperator.ADD_TO_SET_EACH.equals(op)) {
            val = new HashMap<String, Object>();
            ((Map<String, Object>) val).put(UpdateOperator.EACH.val(), val);
        }

        if (val == null) {
            val = value;
        }

        final String opString = op.val();

        if (!ops.containsKey(opString)) {
            ops.put(opString, new HashMap<String, Object>());
        }
        ops.get(opString).put(f, val);
    }

    protected UpdateOperations<T> remove(final String fieldExpr,
            final boolean firstNotLast) {
        add(UpdateOperator.POP, fieldExpr, (firstNotLast) ? -1 : 1, false);
        return this;
    }

    protected List<Object> toDBObjList(final MappedField mf,
            final List<?> values) {
        final ArrayList<Object> vals = new ArrayList<Object>(
                (int) (values.size() * 1.3));
        for (final Object obj : values) {
            vals.add(mapr.toMongoObject(mf, null, obj));
        }

        return vals;
    }
}
