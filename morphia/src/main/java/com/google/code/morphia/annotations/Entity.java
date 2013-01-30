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

import com.allanbank.mongodb.Durability;
import com.google.code.morphia.mapping.Mapper;

/**
 * Allows marking and naming the collectionName
 * 
 * @author Olafur Gauti Gudmundsson
 * @author Scott Hernandez
 * @copyright 2010-2013, Olafur Gauti Gudmundsson, Scott Hernandez and Allanbank
 *            Consulting, Inc., All Rights Reserved
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Entity {
    /**
     * The name for the persistend field. Defaults to the name of the field in
     * the class.
     */
    String value() default Mapper.IGNORED_FIELDNAME;

    /** Controls if the collection created for the entities is capped or not. */
    CappedAt cap() default @CappedAt(0);

    /**
     * To be replaced. This is a temp hack until polymorphism and discriminators
     * are implemented.
     * 
     * @deprecated See JavaDoc comment.
     */
    @Deprecated
    boolean noClassnameStored() default false;

    /** Controls if non-primary queries are OK. */
    boolean queryNonPrimary() default false;

    /**
     * Controls the default write concern or {@link Durability} to use. See
     * {@link Durability#valueOf(String)} for acceptable values.
     */
    String concern() default "";

}
