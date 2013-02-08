/*
 *         Copyright 2010-2013  Uwe Schaefer 
 *           and Allanbank Consulting, Inc.
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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * supposed to be used on a Long or long field for optimistic locking.
 * 
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @copyright 2010-2013, Uwe Schaefer and Allanbank Consulting, Inc., All Rights
 *            Reserved
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Version {
    /** The ignored field name. */
    public static final String IGNORED_FIELDNAME = Property.IGNORED_FIELDNAME;

    /** The mapped name for the field. */
    String value() default IGNORED_FIELDNAME;
}
