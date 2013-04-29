/*
 *         Copyright 2010-2013  Olafur Gauti Gudmundsson, 
 *         Scott Hernandez and Allanbank Consulting, Inc.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package com.google.code.morphia.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker for fields that should be (java) serialized
 * 
 * @author Scott Hernandez
 * @copyright 2010-2013, Olafur Gauti Gudmundsson, Scott Hernandez and Allanbank
 *            Consulting, Inc., All Rights Reserved
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Serialized {
    /** The ignored field name. */
    public static final String IGNORED_FIELDNAME = Property.IGNORED_FIELDNAME;

    /** If true then the serialized field will not be compressed. */
    boolean disableCompression() default false;

    /** The name for the mapped field. Defaults to the field name. */
    String value() default IGNORED_FIELDNAME;
}
