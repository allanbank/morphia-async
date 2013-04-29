package com.google.code.morphia.query;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.code.morphia.state.MappedClass;
import com.google.code.morphia.state.MappedField;
import com.google.code.morphia.utils.ReflectionUtils;

public class FieldCriteria extends AbstractCriteria implements Criteria {
    private static final Logr log = MorphiaLoggerFactory
            .get(FieldCriteria.class);

    protected final String field;
    protected final FilterOperator operator;
    protected final Object value;

    @SuppressWarnings("unchecked")
    protected FieldCriteria(final QueryImpl<?> query, String field,
            final FilterOperator op, final Object value,
            final boolean validateNames, final boolean validateTypes) {
        final StringBuffer sb = new StringBuffer(field); // validate might
                                                         // modify prop string
                                                         // to translate java
                                                         // field name to db
                                                         // field name
        final MappedField mf = Mapper.validate(query.getEntityClass(), query
                .getDatastore().getMapper(), sb, op, value, validateNames,
                validateTypes);
        field = sb.toString();

        final Mapper mapr = query.getDatastore().getMapper();

        MappedClass mc = null;
        try {
            if ((value != null)
                    && !ReflectionUtils.isPropertyType(value.getClass())
                    && !ReflectionUtils.implementsInterface(value.getClass(),
                            Iterable.class)) {
                if ((mf != null) && !mf.isTypeMongoCompatible()) {
                    mc = mapr.getMappedClass((mf.isSingleValue()) ? mf
                            .getType() : mf.getSubClass());
                }
                else {
                    mc = mapr.getMappedClass(value);
                }
            }
        }
        catch (final Exception e) {
            // Ignore these. It is likely they related to mapping validation
            // that is unimportant for queries (the query will fail/return-empty
            // anyway)
            log.debug("Error during mapping of filter criteria: ", e);
        }

        Object mappedValue = mapr.toMongoObject(mf, mc, value);

        final Class<?> type = (mappedValue == null) ? null : mappedValue
                .getClass();

        // convert single values into lists for $in/$nin
        if ((type != null)
                && ((op == FilterOperator.IN) || (op == FilterOperator.NOT_IN))
                && !type.isArray() && !Iterable.class.isAssignableFrom(type)) {
            mappedValue = Collections.singletonList(mappedValue);
        }

        // TODO: investigate and/or add option to control this.
        if ((op == FilterOperator.ELEMENT_MATCH)
                && (mappedValue instanceof Map)) {
            ((Map) mappedValue).remove(Mapper.ID_KEY);
        }

        this.field = field;
        this.operator = op;
        this.value = mappedValue;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addTo(final Map<String, Object> obj) {
        if (FilterOperator.EQUAL.equals(this.operator)) {
            obj.put(this.field, this.value); // no operator, prop equals value

        }
        else {
            Object inner = obj.get(this.field); // operator within inner object

            if (!(inner instanceof Map)) {
                inner = new HashMap<String, Object>();
                obj.put(this.field, inner);
            }

            ((Map<String, Object>) inner).put(this.operator.val(), this.value);
        }
    }

    @Override
    public String toString() {
        return this.field + " " + this.operator.val() + " " + this.value;
    }
}
