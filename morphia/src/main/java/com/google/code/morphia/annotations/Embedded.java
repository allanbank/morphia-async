/*
 *         Copyright 2010-2013  Olafur Gauti Gudmundsson, 
 *           Scott Hernandez and Allanbank Consulting, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.code.morphia.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicated that the field should be persisted with the outer object instead of
 * using a reference to the value.
 * 
 * @author Olafur Gauti Gudmundsson
 * @author Scott Hernandez
 * @copyright 2010-2013, Olafur Gauti Gudmundsson, Scott Hernandez and Allanbank
 *            Consulting, Inc., All Rights Reserved
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE })
public @interface Embedded {

    /** The ignored field name. */
    public static final String IGNORED_FIELDNAME = Property.IGNORED_FIELDNAME;

    /**
     * The name of the Mongo value to store the field. Defaults to the name of
     * the field being annotated.
     * 
     * @return the name of the Mongo value storing the field value (use on
     *         fields only, not applicable for Type level)
     */
    String value() default IGNORED_FIELDNAME;

    /** Specify the concrete class to instantiate. */
    Class<?> concreteClass() default Object.class;
}
