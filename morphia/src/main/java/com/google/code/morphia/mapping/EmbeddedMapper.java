/**
 * 
 */
package com.google.code.morphia.mapping;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.bson.builder.ArrayBuilder;
import com.allanbank.mongodb.bson.builder.BuilderFactory;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.google.code.morphia.mapping.cache.EntityCache;
import com.google.code.morphia.utils.IterHelper;
import com.google.code.morphia.utils.IterHelper.MapIterCallback;
import com.google.code.morphia.utils.ReflectionUtils;

@SuppressWarnings({ "unchecked", "rawtypes" })
class EmbeddedMapper implements CustomMapper {
    public void toDocument(final Object entity, final MappedField mf,
            final DocumentBuilder builder,
            Map<Object, Document> involvedObjects, Mapper mapr) {
        String name = mf.getNameToStore();

        Object fieldValue = mf.getFieldValue(entity);

        if (mf.isMap()) {
            writeMap(mf, builder, involvedObjects, name, fieldValue, mapr);
        }
        else if (mf.isMultipleValues()) {
            writeCollection(mf, builder, involvedObjects, name, fieldValue,
                    mapr);
        }
        else {
            // run converters
            if (mapr.converters.hasDbObjectConverter(mf)
                    || mapr.converters.hasDbObjectConverter(entity.getClass())) {
                mapr.converters.toDBObject(entity, mf, builder,
                        mapr.getOptions());
                return;
            }

            Document dbObj = fieldValue == null ? null : mapr.toDBObject(
                    fieldValue, involvedObjects);
            if (dbObj != null) {
                if (!shouldSaveClassName(fieldValue, dbObj, mf)) {
                    DocumentBuilder b = BuilderFactory.start(dbObj);
                    b.remove(Mapper.CLASS_NAME_FIELDNAME);
                    dbObj = b.build();
                }
                if (dbObj.getElements().size() > 0
                        || mapr.getOptions().storeEmpties) {
                    builder.add(name, dbObj);
                }
            }
        }
    }

    private void writeCollection(final MappedField mf,
            final DocumentBuilder dbObject,
            Map<Object, Document> involvedObjects, String name,
            Object fieldValue, Mapper mapr) {
        Iterable coll = null;

        if (fieldValue != null)
            if (mf.isArray)
                coll = Arrays.asList((Object[]) fieldValue);
            else
                coll = (Iterable) fieldValue;

        if (coll != null) {
            ArrayBuilder ab = dbObject.pushArray(name);
            for (Object o : coll) {
                if (null == o) {
                    ab.addNull();
                }
                else if (mapr.converters.hasSimpleValueConverter(mf)
                        || mapr.converters
                                .hasSimpleValueConverter(o.getClass())) {
                    DocumentBuilder b = BuilderFactory.start();
                    mapr.converters.encode(b, o, "fake", mf);

                    ab.add(b.build().get(name));
                }
                else {
                    Object val;
                    if (Collection.class.isAssignableFrom(o.getClass())
                            || Map.class.isAssignableFrom(o.getClass()))
                        val = mapr.toMongoObject(o, true);
                    else
                        val = mapr.toDBObject(o, involvedObjects);

                    if (val instanceof Document
                            && !shouldSaveClassName(o, val, mf)) {
                        DocumentBuilder db = BuilderFactory
                                .start(((Document) val));
                        db.remove(Mapper.CLASS_NAME_FIELDNAME);
                        val = db.build();
                    }

                    ab.add(val);
                }
            }
            if (ab.build().length == 0 && mapr.getOptions().storeEmpties) {
                dbObject.remove(name);
            }
        }
    }

    private void writeMap(final MappedField mf, final DocumentBuilder dbObject,
            Map<Object, Document> involvedObjects, String name,
            Object fieldValue, Mapper mapr) {
        Map<String, Object> map = (Map<String, Object>) fieldValue;
        if (map != null) {
            BasicDBObject values = new BasicDBObject();

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                Object entryVal = entry.getValue();
                Object val;

                if (entryVal == null)
                    val = null;
                else if (mapr.converters.hasSimpleValueConverter(mf)
                        || mapr.converters.hasSimpleValueConverter(entryVal
                                .getClass()))
                    val = mapr.converters.encode(entryVal);
                else {
                    if (Map.class.isAssignableFrom(entryVal.getClass())
                            || Collection.class.isAssignableFrom(entryVal
                                    .getClass()))
                        val = mapr.toMongoObject(entryVal, true);
                    else
                        val = mapr.toDBObject(entryVal, involvedObjects);

                    if (!shouldSaveClassName(entryVal, val, mf))
                        ((DBObject) val)
                                .removeField(Mapper.CLASS_NAME_FIELDNAME);
                }

                String strKey = mapr.converters.encode(entry.getKey())
                        .toString();
                values.put(strKey, val);
            }

            if (values.size() > 0 || mapr.getOptions().storeEmpties)
                dbObject.put(name, values);
        }
    }

    public void fromDocument(final Document dbObject, final MappedField mf,
            final Object entity, EntityCache cache, Mapper mapr) {
        try {
            if (mf.isMap()) {
                readMap(dbObject, mf, entity, cache, mapr);
            }
            else if (mf.isMultipleValues()) {
                readCollection(dbObject, mf, entity, cache, mapr);
            }
            else {
                // single element

                Object dbVal = mf.getDbObjectValue(dbObject);
                if (dbVal != null) {
                    boolean isDBObject = dbVal instanceof DBObject
                            && !(dbVal instanceof BasicDBList);

                    // run converters
                    if (isDBObject
                            && (mapr.converters.hasDbObjectConverter(mf) || mapr.converters
                                    .hasDbObjectConverter(mf.getType()))) {
                        mapr.converters.fromDBObject(((DBObject) dbVal), mf,
                                entity);
                        return;
                    }
                    else {
                        Object refObj = null;
                        if (mapr.converters.hasSimpleValueConverter(mf)
                                || mapr.converters.hasSimpleValueConverter(mf
                                        .getType()))
                            refObj = mapr.converters.decode(mf.getType(),
                                    dbVal, mf);
                        else {
                            refObj = mapr.getOptions().objectFactory
                                    .createInstance(mapr, mf,
                                            ((DBObject) dbVal));
                            refObj = mapr.fromDb(((DBObject) dbVal), refObj,
                                    cache);
                        }
                        if (refObj != null) {
                            mf.setFieldValue(entity, refObj);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void readCollection(final DBObject dbObject, final MappedField mf,
            final Object entity, EntityCache cache, Mapper mapr) {
        // multiple documents in a List
        Collection values = mf.isSet() ? mapr.getOptions().objectFactory
                .createSet(mf) : mapr.getOptions().objectFactory.createList(mf);

        Object dbVal = mf.getDbObjectValue(dbObject);
        if (dbVal != null) {

            List dbVals = null;
            if (dbVal instanceof List)
                dbVals = (List) dbVal;
            else {
                dbVals = new BasicDBList();
                dbVals.add(dbVal);
            }

            for (Object o : dbVals) {

                DBObject dbObj = (DBObject) o;
                Object newEntity = null;

                if (dbObj != null) {
                    // run converters
                    if (mapr.converters.hasSimpleValueConverter(mf)
                            || mapr.converters.hasSimpleValueConverter(mf
                                    .getSubClass()))
                        newEntity = mapr.converters.decode(mf.getSubClass(),
                                dbObj, mf);
                    else {
                        newEntity = readMapOrCollectionOrEntity(dbObj, mf,
                                cache, mapr);
                    }
                }

                values.add(newEntity);
            }
        }
        if (values.size() > 0) {
            if (mf.getType().isArray()) {
                mf.setFieldValue(entity, ReflectionUtils.convertToArray(
                        mf.getSubClass(), ReflectionUtils.iterToList(values)));
            }
            else {
                mf.setFieldValue(entity, values);
            }
        }
    }

    private void readMap(final DBObject dbObject, final MappedField mf,
            final Object entity, final EntityCache cache, final Mapper mapr) {
        final Map map = mapr.getOptions().objectFactory.createMap(mf);

        DBObject dbObj = (DBObject) mf.getDbObjectValue(dbObject);
        new IterHelper<Object, Object>().loopMap((Object) dbObj,
                new MapIterCallback<Object, Object>() {
                    @Override
                    public void eval(Object key, Object val) {
                        Object newEntity = null;

                        // run converters
                        if (val != null) {
                            if (mapr.converters.hasSimpleValueConverter(mf)
                                    || mapr.converters
                                            .hasSimpleValueConverter(mf
                                                    .getSubClass()))
                                newEntity = mapr.converters.decode(
                                        mf.getSubClass(), val, mf);
                            else {
                                if (val instanceof DBObject)
                                    newEntity = readMapOrCollectionOrEntity(
                                            (DBObject) val, mf, cache, mapr);
                                else
                                    throw new MappingException(
                                            "Embedded element isn't a DBObject! How can it be that is a "
                                                    + val.getClass());

                            }
                        }

                        Object objKey = mapr.converters.decode(
                                mf.getMapKeyClass(), key);
                        map.put(objKey, newEntity);
                    }
                });

        if (map.size() > 0) {
            mf.setFieldValue(entity, map);
        }
    }

    private Object readMapOrCollectionOrEntity(DBObject dbObj, MappedField mf,
            EntityCache cache, Mapper mapr) {
        if (Map.class.isAssignableFrom(mf.getSubClass())
                || Iterable.class.isAssignableFrom(mf.getSubClass())) {
            MapOrCollectionMF mocMF = new MapOrCollectionMF(
                    (ParameterizedType) mf.getSubType());
            mapr.fromDb(dbObj, mocMF, cache);
            return mocMF.getValue();
        }
        else {
            Object newEntity = mapr.getOptions().objectFactory.createInstance(
                    mapr, mf, dbObj);
            return mapr.fromDb(dbObj, newEntity, cache);
        }
    }

    public static boolean shouldSaveClassName(Object rawVal,
            Object convertedVal, MappedField mf) {
        if (rawVal == null || mf == null)
            return true;
        if (mf.isSingleValue())
            return !(mf.getType().equals(rawVal.getClass()) && !(convertedVal instanceof BasicDBList));
        else if (convertedVal != null && convertedVal instanceof DBObject
                && !mf.getSubClass().isInterface()
                && !Modifier.isAbstract(mf.getSubClass().getModifiers())
                && mf.getSubClass().equals(rawVal.getClass()))
            return false;
        else
            return true;
    }

}
