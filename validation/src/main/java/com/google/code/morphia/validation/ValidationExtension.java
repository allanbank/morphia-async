/**
 *
 */
package com.google.code.morphia.validation;

import java.util.Set;

import javax.validation.Configuration;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import com.allanbank.mongodb.bson.Document;
import com.google.code.morphia.AbstractEntityInterceptor;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.mapping.Mapper;

/**
 * @author us@thomas-daily.de
 */
public class ValidationExtension extends AbstractEntityInterceptor
{
    private ValidatorFactory validationFactory;
    private Mapper mapper;

    @Deprecated
    public ValidationExtension()
    {
        // use the new ValidationExtension(morphia) convention
    }

    public ValidationExtension(final Morphia m)
    {
        final Configuration<?> configuration = Validation.byDefaultProvider().configure();
        this.validationFactory = configuration.buildValidatorFactory();

        m.getMapper().addInterceptor(this);
    }

    @Override
    public void prePersist(final Object ent, final Document dbObj, final Mapper mapr)
    {
        final Set validate = this.validationFactory.getValidator().validate(ent);
        if (!validate.isEmpty())
        {
            throw new VerboseJSR303ConstraintViolationException(validate);
        }
    }
}
