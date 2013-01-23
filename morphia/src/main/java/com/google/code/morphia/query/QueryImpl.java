package com.google.code.morphia.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.allanbank.mongodb.ClosableIterator;
import com.allanbank.mongodb.MongoCollection;
import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.bson.DocumentAssignable;
import com.allanbank.mongodb.bson.builder.BuilderFactory;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.allanbank.mongodb.builder.Find;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.DatastoreImpl;
import com.google.code.morphia.Key;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.logging.Logr;
import com.google.code.morphia.logging.MorphiaLoggerFactory;
import com.google.code.morphia.mapping.MappedClass;
import com.google.code.morphia.mapping.Mapper;
import com.google.code.morphia.mapping.cache.EntityCache;

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
    private static final Logr log = MorphiaLoggerFactory.get(QueryImpl.class);

    private EntityCache cache;
    private boolean validateName = true;
    private boolean validateType = true;

    private String[] fields = null;
    private Boolean includeFields = null;
    private Map<String, Integer> sort = null;
    private DatastoreImpl ds = null;
    private MongoCollection dbColl = null;
    private int offset = 0;
    private int limit = -1;
    private int batchSize = 0;
    private String indexHint;
    private Class<T> clazz = null;
    private Map<String, Object> baseQuery = null;
    private boolean snapshotted = false;
    private boolean slaveOk = false;
    private boolean noTimeout = false;

    public QueryImpl(Class<T> clazz, MongoCollection coll, Datastore ds) {
        super(CriteriaJoin.AND);

        this.query = this;
        this.clazz = clazz;
        this.ds = ((DatastoreImpl) ds);
        this.dbColl = coll;
        this.cache = this.ds.getMapper().createEntityCache();

        MappedClass mc = this.ds.getMapper().getMappedClass(clazz);
        Entity entAn = mc == null ? null : mc.getEntityAnnotation();
        if (entAn != null)
            this.slaveOk = this.ds.getMapper().getMappedClass(clazz)
                    .getEntityAnnotation().queryNonPrimary();
    }

    public QueryImpl(Class<T> clazz, MongoCollection coll, Datastore ds,
            int offset, int limit) {
        this(clazz, coll, ds);
        this.offset = offset;
        this.limit = limit;
    }

    public QueryImpl(Class<T> clazz, MongoCollection coll, DatastoreImpl ds,
            Map<String, Object> baseQuery) {
        this(clazz, coll, ds);
        this.baseQuery = baseQuery;
    }

    @Override
    public QueryImpl<T> clone() {
        QueryImpl<T> n = new QueryImpl<T>(clazz, dbColl, ds);
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

    public MongoCollection getCollection() {
        return dbColl;
    }

    public void setQueryObject(Map<String, Object> query) {
        this.baseQuery = query;
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }

    public Document getQueryObject() {
        Map<String, Object> obj = new HashMap<String, Object>();

        if (this.baseQuery != null) {
            obj.putAll(this.baseQuery);
        }

        this.addTo(obj);

        DocumentBuilder builder = BuilderFactory.start();
        for (Map.Entry<String, Object> entry : obj.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }

        return builder.build();
    }

    public DatastoreImpl getDatastore() {
        return ds;
    }

    public Document getFieldsObject() {
        if (fields == null || fields.length == 0)
            return null;

        DocumentBuilder fieldsFilter = BuilderFactory.start();
        for (String field : this.fields) {
            fieldsFilter.add(field, (includeFields));
        }

        return fieldsFilter.build();
    }

    public Document getSortObject() {
        if (sort == null) {
            return null;
        }
        DocumentBuilder sortDoc = BuilderFactory.start();
        for (Map.Entry<String, Integer> field : this.sort.entrySet()) {
            sortDoc.add(field.getKey(), field.getValue());
        }

        return sortDoc.build();
    }

    public boolean isValidatingNames() {
        return validateName;
    }

    public boolean isValidatingTypes() {
        return validateType;
    }

    public long countAll() {
        Document query = getQueryObject();
        if (log.isTraceEnabled())
            log.trace("Executing count(" + dbColl.getName() + ") for query: "
                    + query);
        return dbColl.count(query);
    }

    public ClosableIterator<Document> prepareCursor() {
        Document query = getQueryObject();
        Document fields = getFieldsObject();

        if (log.isTraceEnabled())
            log.trace("Running query(" + dbColl.getName() + ") : " + query
                    + ", fields:" + fields + ",off:" + offset + ",limit:"
                    + limit);

        Find.Builder builder = new Find.Builder(query);
        builder.setReturnFields(fields);

        if (offset > 0)
            builder.setNumberToSkip(offset);
        if (limit > 0)
            builder.setLimit(limit);
        if (batchSize > 0)
            builder.setBatchSize(batchSize);
        if (snapshotted)
            builder.snapshot();
        if (sort != null)
            builder.setSort(getSortObject());
        if (indexHint != null)
            builder.setHint(indexHint);

        // Check for bad options.
        if (snapshotted && (sort != null || indexHint != null))
            log.warning("Snapshotted query should not have hint/sort.");

        return dbColl.find(builder.build());
    }

    public Iterable<T> fetch() {
        ClosableIterator<Document> cursor = prepareCursor();
        if (log.isTraceEnabled())
            log.trace("Getting cursor(" + dbColl.getName() + ")  for query.");

        return new MorphiaIterator<T, T>(cursor, ds.getMapper(), clazz,
                dbColl.getName(), cache);
    }

    public Iterable<Key<T>> fetchKeys() {
        String[] oldFields = fields;
        Boolean oldInclude = includeFields;
        fields = new String[] { Mapper.ID_KEY };
        includeFields = true;
        ClosableIterator<Document> cursor = prepareCursor();

        if (log.isTraceEnabled())
            log.trace("Getting cursor(" + dbColl.getName() + ") for query.");

        fields = oldFields;
        includeFields = oldInclude;
        return new MorphiaKeyIterator<T>(cursor, ds.getMapper(), clazz,
                dbColl.getName());
    }

    @SuppressWarnings("unchecked")
    public List<T> asList() {
        List<T> results = new ArrayList<T>();
        MorphiaIterator<T, T> iter = (MorphiaIterator<T, T>) fetch().iterator();
        for (T ent : iter)
            results.add(ent);

        if (log.isTraceEnabled())
            log.trace(String
                    .format("\nasList: %s \t %d entities, iterator time: driver %n ms, mapper %n ms \n cache: %s \n for $s \n ",
                            dbColl.getName(), results.size(), iter
                                    .getDriverTime(), iter.getMapperTime(),
                            cache.stats().toString(), getQueryObject()));

        return results;
    }

    public List<Key<T>> asKeyList() {
        List<Key<T>> results = new ArrayList<Key<T>>();
        for (Key<T> key : fetchKeys())
            results.add(key);
        return results;
    }

    public Iterable<T> fetchEmptyEntities() {
        String[] oldFields = fields;
        Boolean oldInclude = includeFields;
        fields = new String[] { Mapper.ID_KEY };
        includeFields = true;
        Iterable<T> res = fetch();
        fields = oldFields;
        includeFields = oldInclude;
        return res;
    }

    /**
     * Converts the textual operator (">", "<=", etc) into a FilterOperator.
     * Forgiving about the syntax; != and <> are NOT_EQUAL, = and == are EQUAL.
     */
    protected FilterOperator translate(String operator) {
        operator = operator.trim();

        if (operator.equals("=") || operator.equals("=="))
            return FilterOperator.EQUAL;
        else if (operator.equals(">"))
            return FilterOperator.GREATER_THAN;
        else if (operator.equals(">="))
            return FilterOperator.GREATER_THAN_OR_EQUAL;
        else if (operator.equals("<"))
            return FilterOperator.LESS_THAN;
        else if (operator.equals("<="))
            return FilterOperator.LESS_THAN_OR_EQUAL;
        else if (operator.equals("!=") || operator.equals("<>"))
            return FilterOperator.NOT_EQUAL;
        else if (operator.toLowerCase().equals("in"))
            return FilterOperator.IN;
        else if (operator.toLowerCase().equals("nin"))
            return FilterOperator.NOT_IN;
        else if (operator.toLowerCase().equals("all"))
            return FilterOperator.ALL;
        else if (operator.toLowerCase().equals("exists"))
            return FilterOperator.EXISTS;
        else if (operator.toLowerCase().equals("elem"))
            return FilterOperator.ELEMENT_MATCH;
        else if (operator.toLowerCase().equals("size"))
            return FilterOperator.SIZE;
        else if (operator.toLowerCase().equals("within"))
            return FilterOperator.WITHIN;
        else if (operator.toLowerCase().equals("near"))
            return FilterOperator.NEAR;
        else
            throw new IllegalArgumentException("Unknown operator '" + operator
                    + "'");
    }

    public Query<T> filter(String condition, Object value) {
        String[] parts = condition.trim().split(" ");
        if (parts.length < 1 || parts.length > 6)
            throw new IllegalArgumentException("'" + condition
                    + "' is not a legal filter condition");

        String prop = parts[0].trim();
        FilterOperator op = (parts.length == 2) ? this.translate(parts[1])
                : FilterOperator.EQUAL;

        this.add(new FieldCriteria(this, prop, op, value, this.validateName,
                this.validateType));

        return this;
    }

    public Query<T> where(String js, DocumentAssignable scope) {
        this.add(new WhereCriteria(js));
        return this;
    }

    public Query<T> where(String js) {
        this.add(new WhereCriteria(js));
        return this;
    }

    public Query<T> enableValidation() {
        validateName = validateType = true;
        return this;
    }

    public Query<T> disableValidation() {
        validateName = validateType = false;
        return this;
    }

    QueryImpl<T> validateNames() {
        validateName = true;
        return this;
    }

    QueryImpl<T> disableTypeValidation() {
        validateType = false;
        return this;
    }

    public T get() {
        int oldLimit = limit;
        limit = 1;
        Iterator<T> it = fetch().iterator();
        limit = oldLimit;
        return (it.hasNext()) ? it.next() : null;
    }

    public Key<T> getKey() {
        int oldLimit = limit;
        limit = 1;
        Iterator<Key<T>> it = fetchKeys().iterator();
        limit = oldLimit;
        return (it.hasNext()) ? it.next() : null;
    }

    public Query<T> limit(int value) {
        this.limit = value;
        return this;
    }

    public Query<T> batchSize(int value) {
        this.batchSize = value;
        return this;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public Query<T> skip(int value) {
        this.offset = value;
        return this;
    }

    public Query<T> offset(int value) {
        this.offset = value;
        return this;
    }

    public Query<T> order(String condition) {
        if (snapshotted)
            throw new QueryException(
                    "order cannot be used on a snapshotted query.");

        // TODO: validate names and translate from java names.
        sort = parseSortString(condition);

        return this;
    }

    public static Map<String, Integer> parseSortString(String str) {
        Map<String, Integer> ret = new HashMap<String, Integer>();
        String[] parts = str.split(",");
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

    public Iterator<T> iterator() {
        return fetch().iterator();
    }

    public Class<T> getEntityClass() {
        return this.clazz;
    }

    public String toString() {
        return this.getQueryObject().toString();
    }

    public FieldEnd<? extends Query<T>> field(String name) {
        return this.field(name, this.validateName);
    }

    private FieldEnd<? extends Query<T>> field(String field, boolean validate) {
        return new FieldEndImpl<QueryImpl<T>>(this, field, this, validate);
    }

    public FieldEnd<? extends CriteriaContainerImpl> criteria(String field) {
        return this.criteria(field, this.validateName);
    }

    private FieldEnd<? extends CriteriaContainerImpl> criteria(String field,
            boolean validate) {
        CriteriaContainerImpl container = new CriteriaContainerImpl(this,
                CriteriaJoin.AND);
        this.add(container);

        return new FieldEndImpl<CriteriaContainerImpl>(this, field, container,
                validate);
    }

    // TODO: test this.
    public Query<T> hintIndex(String idxName) {
        indexHint = idxName;
        return this;
    }

    public Query<T> retrievedFields(boolean include, String... fields) {
        if (includeFields != null && include != includeFields)
            throw new IllegalStateException(
                    "You cannot mix include and excluded fields together!");
        this.includeFields = include;
        this.fields = fields;
        return this;
    }

    /**
     * Enabled snapshotted mode where duplicate results (which may be updated
     * during the lifetime of the cursor) will not be returned. Not compatible
     * with order/sort and hint.
     **/
    public Query<T> enableSnapshotMode() {
        snapshotted = true;
        return this;
    }

    /**
     * Disable snapshotted mode (default mode). This will be faster but changes
     * made during the cursor may cause duplicates.
     **/
    public Query<T> disableSnapshotMode() {
        snapshotted = false;
        return this;
    }

    public Query<T> queryNonPrimary() {
        slaveOk = true;
        return this;
    }

    public Query<T> queryPrimaryOnly() {
        slaveOk = false;
        return this;
    }

    /** Disables cursor timeout on server. */
    public Query<T> disableTimeout() {
        noTimeout = false;
        return this;
    }

    /** Enables cursor timeout on server. */
    public Query<T> enableTimeout() {
        noTimeout = true;
        return this;
    }
}
