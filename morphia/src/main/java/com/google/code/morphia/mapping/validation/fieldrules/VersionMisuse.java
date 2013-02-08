/**
 * 
 */
package com.google.code.morphia.mapping.validation.fieldrules;

import java.util.Set;

import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.ElementType;
import com.google.code.morphia.annotations.Version;
import com.google.code.morphia.converters.primitive.LongConverter;
import com.google.code.morphia.mapping.validation.ConstraintViolation;
import com.google.code.morphia.mapping.validation.ConstraintViolation.Level;
import com.google.code.morphia.state.MappedClass;
import com.google.code.morphia.state.MappedField;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * 
 */
public class VersionMisuse extends FieldConstraint {

    @Override
    protected void check(MappedClass mc, MappedField mf,
            Set<ConstraintViolation> ve) {
        if (mf.hasAnnotation(Version.class)) {
            Class<?> type = mf.getResolvedClass();
            if (Long.class.equals(type) || long.class.equals(type)) {

                // TODO: Replace this will a read ObjectFactory call -- requires
                // Mapper instance.
                try {
                    Object testInstance = mc.getMappedClass().newInstance();
                    LongConverter converter = new LongConverter();

                    Element e = converter.toElement(mc.getMappedClass(), "f",
                            (Long) mf.get(testInstance));

                    if ((e.getType() == ElementType.NULL)
                            || Long.valueOf(0).equals(e.getValueAsObject())) {

                        // check initial value
                        ve.add(new ConstraintViolation(
                                Level.FATAL,
                                mc,
                                mf,
                                this.getClass(),
                                "When using @"
                                        + Version.class.getSimpleName()
                                        + " on a Long or long field, it "
                                        + "must be initialized to null or 0 (zero)."));
                    }
                }
                catch (InstantiationException e1) {
                    ve.add(new ConstraintViolation(Level.WARNING, mc, mf, this
                            .getClass(),
                            "Could not instantiate an instance of the "
                                    + mc.getMappedClass().getName()
                                    + " class to validate the @"
                                    + Version.class.getSimpleName()
                                    + " annotation "
                                    + "is initailized to null or 0 (zero)."));
                }
                catch (IllegalAccessException e1) {
                    ve.add(new ConstraintViolation(Level.WARNING, mc, mf, this
                            .getClass(),
                            "Could not instantiate an instance of the "
                                    + mc.getMappedClass().getName()
                                    + " class to validate the @"
                                    + Version.class.getSimpleName()
                                    + " annotation "
                                    + "is initailized to null or 0 (zero)."));
                }
            }
            else
                ve.add(new ConstraintViolation(Level.FATAL, mc, mf, this
                        .getClass(), "@" + Version.class.getSimpleName()
                        + " can only be used on a Long/long field."));
        }
    }

}
