package com.google.code.morphia.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.allanbank.mongodb.ClosableIterator;
import com.allanbank.mongodb.MongoCollection;
import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.bson.DocumentAssignable;
import com.allanbank.mongodb.bson.builder.BuilderFactory;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.allanbank.mongodb.builder.Find;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.Key;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.impl.DatastoreImpl;
import com.google.code.morphia.mapping.cache.EntityCache;
import com.google.code.morphia.state.MappedClass;

/**
 * <p>
 * Implementation of Query
 * </p>
 * 
 * @author Scott Hernandez
 * 
 * @param <T>
 *            The type we will be querying for, and returning.
 */
public class QueryImpl<T> extends CriteriaContainerImpl implements Query<T>,
        Criteria {

    private static final Logger log = Logger.getLogger(QueryImpl.class
            .getName());

    public static Map<String, Integer> parseSortString(final String str) {
        final Map<String, Integer> ret = new HashMap<String, Integer>();
        final String[] parts = str.split(",");
        for (String s : parts) {
            s = s.trim();
            int dir = 1;

            if (s.startsWith("-")) {
                dir = -1;
                s = s.substring(1).trim();
            }

            ret.put(s, Integer.valueOf(dir));
        }
        return ret;
    }

    private Map<String, Object> baseQuery = null;
    private int batchSize = 0;

    private EntityCache cache;
    private Class<T> clazz = null;
    private MongoCollection dbColl = null;
    private DatastoreImpl ds = null;
    private String[] fields = null;
    private Boolean includeFields = null;
    private String indexHint;
    private int limit = -1;
    private boolean noTimeout = false;
    private int offset = 0;
    private boolean slaveOk = false;
    private boolean snapshotted = false;
    private Map<String, Integer> sort = null;
    private boolean validateName = true;

    private boolean validateType = true;

    public QueryImpl(final Class<T> clazz, final MongoCollection coll,
            final Datastore ds) {
        super(CriteriaJoin.AND);

        this.query = this;
        this.clazz = clazz;
        this.ds = ((DatastoreImpl) ds);
        this.dbColl = coll;
        this.cache = this.ds.getMapper().createEntityCache();

        final MappedClass mc = this.ds.getMapper().getMappedClass(clazz);
        final Entity entAn = mc == null ? null : mc.getEntityAnnotation();
        if (entAn != null) {
            this.slaveOk = this.ds.getMapper().getMappedClass(clazz)
                    .getEntityAnnotation().queryNonPrimary();
        }
    }

    public QueryImpl(final Class<T> clazz, final MongoCollection coll,
            final Datastore ds, final int offset, final int limit) {
        this(clazz, coll, ds);
        this.offset = offset;
        this.limit = limit;
    }

    public QueryImpl(final Class<T> clazz, final MongoCollection coll,
            final DatastoreImpl ds, final Map<String, Object> baseQuery) {
        this(clazz, coll, ds);
        this.baseQuery = baseQuery;
    }

    @Override
    public List<Key<T>> asKeyList() {
        final List<Key<T>> results = new ArrayList<Key<T>>();
        for (final Key<T> key : fetchKeys()) {
            results.add(key);
        }
        return results;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> asList() {
        final List<T> results = new ArrayList<T>();
        final MorphiaIterator<T, T> iter = (MorphiaIterator<T, T>) fetch()
                .iterator();
        for (final T ent : iter) {
            results.add(ent);
        }

        if (log.isTraceEnabled()) {
            log.trace(String
                    .format("\nasList: %s \t %d entities, iterator time: driver %n ms, mapper %n ms \n cache: %s \n for $s \n ",
                            dbColl.getName(), results.size(), iter
                                    .getDriverTime(), iter.getMapperTime(),
                            cache.stats().toString(), getQueryObject()));
        }

        return results;
    }

    @Override
    public Query<T> batchSize(final int value) {
        this.batchSize = value;
        return this;
    }

    @Override
    public QueryImpl<T> clone() {
        final QueryImpl<T> n = new QueryImpl<T>(clazz, dbColl, ds);
        n.attachedTo = attachedTo;
        n.baseQuery = baseQuery;
        n.batchSize = batchSize;
        n.cache = cache;
        n.fields = fields;
        n.includeFields = includeFields;
        n.indexHint = indexHint;
        n.limit = limit;
        n.noTimeout = noTimeout;
        n.offset = offset;
        n.query = n;
        n.slaveOk = slaveOk;
        n.snapshotted = snapshotted;
        n.sort = sort;
        n.validateName = validateName;
        n.validateType = validateType;
        return n;
    }

    @Override
    public long countAll() {
        final Document query = getQueryObject();
        if (log.isTraceEnabled()) {
            log.trace("Executing count(" + dbColl.getName() + ") for query: "
                    + query);
        }
        return dbColl.count(query);
    }

    @Override
    public FieldEnd<? extends CriteriaContainerImpl> criteria(final String field) {
        return this.criteria(field, this.validateName);
    }

    /**
     * Disable snapshotted mode (default mode). This will be faster but changes
     * made during the cursor may cause duplicates.
     **/
    @Override
    public Query<T> disableSnapshotMode() {
        snapshotted = false;
        return this;
    }

    /** Disables cursor timeout on server. */
    @Override
    public Query<T> disableTimeout() {
        noTimeout = false;
        return this;
    }

    @Override
    public Query<T> disableValidation() {
        validateName = validateType = false;
        return this;
    }

    /**
     * Enabled snapshotted mode where duplicate results (which may be updated
     * during the lifetime of the cursor) will not be returned. Not compatible
     * with order/sort and hint.
     **/
    @Override
    public Query<T> enableSnapshotMode() {
        snapshotted = true;
        return this;
    }

    /** Enables cursor timeout on server. */
    @Override
    public Query<T> enableTimeout() {
        noTimeout = true;
        return this;
    }

    @Override
    public Query<T> enableValidation() {
        validateName = validateType = true;
        return this;
    }

    @Override
    public Iterable<T> fetch() {
        final ClosableIterator<Document> cursor = prepareCursor();
        if (log.isTraceEnabled()) {
            log.trace("Getting cursor(" + dbColl.getName() + ")  for query.");
        }

        return new MorphiaIterator<T, T>(cursor, ds.getMapper(), clazz,
                dbColl.getName(), cache);
    }

    @Override
    public Iterable<T> fetchEmptyEntities() {
        final String[] oldFields = fields;
        final Boolean oldInclude = includeFields;
        fields = new String[] { Mapper.ID_KEY };
        includeFields = true;
        final Iterable<T> res = fetch();
        fields = oldFields;
        includeFields = oldInclude;
        return res;
    }

    @Override
    public Iterable<Key<T>> fetchKeys() {
        final String[] oldFields = fields;
        final Boolean oldInclude = includeFields;
        fields = new String[] { Mapper.ID_KEY };
        includeFields = true;
        final ClosableIterator<Document> cursor = prepareCursor();

        if (log.isTraceEnabled()) {
            log.trace("Getting cursor(" + dbColl.getName() + ") for query.");
        }

        fields = oldFields;
        includeFields = oldInclude;
        return new MorphiaKeyIterator<T>(cursor, ds.getMapper(), clazz,
                dbColl.getName());
    }

    @Override
    public FieldEnd<? extends Query<T>> field(final String name) {
        return this.field(name, this.validateName);
    }

    @Override
    public Query<T> filter(final String condition, final Object value) {
        final String[] parts = condition.trim().split(" ");
        if ((parts.length < 1) || (parts.length > 6)) {
            throw new IllegalArgumentException("'" + condition
                    + "' is not a legal filter condition");
        }

        final String prop = parts[0].trim();
        final FilterOperator op = (parts.length == 2) ? this
                .translate(parts[1]) : FilterOperator.EQUAL;

        this.add(new FieldCriteria(this, prop, op, value, this.validateName,
                this.validateType));

        return this;
    }

    @Override
    public T get() {
        final int oldLimit = limit;
        limit = 1;
        final Iterator<T> it = fetch().iterator();
        limit = oldLimit;
        return (it.hasNext()) ? it.next() : null;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public MongoCollection getCollection() {
        return dbColl;
    }

    public DatastoreImpl getDatastore() {
        return ds;
    }

    @Override
    public Class<T> getEntityClass() {
        return this.clazz;
    }

    public Document getFieldsObject() {
        if ((fields == null) || (fields.length == 0)) {
            return null;
        }

        final DocumentBuilder fieldsFilter = BuilderFactory.start();
        for (final String field : this.fields) {
            fieldsFilter.add(field, (includeFields));
        }

        return fieldsFilter.build();
    }

    @Override
    public Key<T> getKey() {
        final int oldLimit = limit;
        limit = 1;
        final Iterator<Key<T>> it = fetchKeys().iterator();
        limit = oldLimit;
        return (it.hasNext()) ? it.next() : null;
    }

    public int getLimit() {
        return limit;
    }

    public int getOffset() {
        return offset;
    }

    public Document getQueryObject() {
        final Map<String, Object> obj = new HashMap<String, Object>();

        if (this.baseQuery != null) {
            obj.putAll(this.baseQuery);
        }

        this.addTo(obj);

        final DocumentBuilder builder = BuilderFactory.start();
        for (final Map.Entry<String, Object> entry : obj.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }

        return builder.build();
    }

    public Document getSortObject() {
        if (sort == null) {
            return null;
        }
        final DocumentBuilder sortDoc = BuilderFactory.start();
        for (final Map.Entry<String, Integer> field : this.sort.entrySet()) {
            sortDoc.add(field.getKey(), field.getValue());
        }

        return sortDoc.build();
    }

    // TODO: test this.
    @Override
    public Query<T> hintIndex(final String idxName) {
        indexHint = idxName;
        return this;
    }

    public boolean isValidatingNames() {
        return validateName;
    }

    public boolean isValidatingTypes() {
        return validateType;
    }

    @Override
    public Iterator<T> iterator() {
        return fetch().iterator();
    }

    @Override
    public Query<T> limit(final int value) {
        this.limit = value;
        return this;
    }

    @Override
    public Query<T> offset(final int value) {
        this.offset = value;
        return this;
    }

    @Override
    public Query<T> order(final String condition) {
        if (snapshotted) {
            throw new QueryException(
                    "order cannot be used on a snapshotted query.");
        }

        // TODO: validate names and translate from java names.
        sort = parseSortString(condition);

        return this;
    }

    public ClosableIterator<Document> prepareCursor() {
        final Document query = getQueryObject();
        final Document fields = getFieldsObject();

        if (log.isTraceEnabled()) {
            log.trace("Running query(" + dbColl.getName() + ") : " + query
                    + ", fields:" + fields + ",off:" + offset + ",limit:"
                    + limit);
        }

        final Find.Builder builder = new Find.Builder(query);
        builder.setReturnFields(fields);

        if (offset > 0) {
            builder.setNumberToSkip(offset);
        }
        if (limit > 0) {
            builder.setLimit(limit);
        }
        if (batchSize > 0) {
            builder.setBatchSize(batchSize);
        }
        if (snapshotted) {
            builder.snapshot();
        }
        if (sort != null) {
            builder.setSort(getSortObject());
        }
        if (indexHint != null) {
            builder.setHint(indexHint);
        }

        // Check for bad options.
        if (snapshotted && ((sort != null) || (indexHint != null))) {
            log.warning("Snapshotted query should not have hint/sort.");
        }

        return dbColl.find(builder.build());
    }

    @Override
    public Query<T> queryNonPrimary() {
        slaveOk = true;
        return this;
    }

    @Override
    public Query<T> queryPrimaryOnly() {
        slaveOk = false;
        return this;
    }

    @Override
    public Query<T> retrievedFields(final boolean include,
            final String... fields) {
        if ((includeFields != null) && (include != includeFields)) {
            throw new IllegalStateException(
                    "You cannot mix include and excluded fields together!");
        }
        this.includeFields = include;
        this.fields = fields;
        return this;
    }

    public void setQueryObject(final Map<String, Object> query) {
        this.baseQuery = query;
    }

    @Override
    public Query<T> skip(final int value) {
        this.offset = value;
        return this;
    }

    @Override
    public String toString() {
        return this.getQueryObject().toString();
    }

    @Override
    public Query<T> where(final String js) {
        this.add(new WhereCriteria(js));
        return this;
    }

    @Override
    public Query<T> where(final String js, final DocumentAssignable scope) {
        this.add(new WhereCriteria(js));
        return this;
    }

    /**
     * Converts the textual operator (">", "<=", etc) into a FilterOperator.
     * Forgiving about the syntax; != and <> are NOT_EQUAL, = and == are EQUAL.
     */
    protected FilterOperator translate(String operator) {
        operator = operator.trim();

        if (operator.equals("=") || operator.equals("==")) {
            return FilterOperator.EQUAL;
        }
        else if (operator.equals(">")) {
            return FilterOperator.GREATER_THAN;
        }
        else if (operator.equals(">=")) {
            return FilterOperator.GREATER_THAN_OR_EQUAL;
        }
        else if (operator.equals("<")) {
            return FilterOperator.LESS_THAN;
        }
        else if (operator.equals("<=")) {
            return FilterOperator.LESS_THAN_OR_EQUAL;
        }
        else if (operator.equals("!=") || operator.equals("<>")) {
            return FilterOperator.NOT_EQUAL;
        }
        else if (operator.toLowerCase().equals("in")) {
            return FilterOperator.IN;
        }
        else if (operator.toLowerCase().equals("nin")) {
            return FilterOperator.NOT_IN;
        }
        else if (operator.toLowerCase().equals("all")) {
            return FilterOperator.ALL;
        }
        else if (operator.toLowerCase().equals("exists")) {
            return FilterOperator.EXISTS;
        }
        else if (operator.toLowerCase().equals("elem")) {
            return FilterOperator.ELEMENT_MATCH;
        }
        else if (operator.toLowerCase().equals("size")) {
            return FilterOperator.SIZE;
        }
        else if (operator.toLowerCase().equals("within")) {
            return FilterOperator.WITHIN;
        }
        else if (operator.toLowerCase().equals("near")) {
            return FilterOperator.NEAR;
        }
        else {
            throw new IllegalArgumentException("Unknown operator '" + operator
                    + "'");
        }
    }

    QueryImpl<T> disableTypeValidation() {
        validateType = false;
        return this;
    }

    QueryImpl<T> validateNames() {
        validateName = true;
        return this;
    }

    private FieldEnd<? extends CriteriaContainerImpl> criteria(
            final String field, final boolean validate) {
        final CriteriaContainerImpl container = new CriteriaContainerImpl(this,
                CriteriaJoin.AND);
        this.add(container);

        return new FieldEndImpl<CriteriaContainerImpl>(this, field, container,
                validate);
    }

    private FieldEnd<? extends Query<T>> field(final String field,
            final boolean validate) {
        return new FieldEndImpl<QueryImpl<T>>(this, field, this, validate);
    }
}
