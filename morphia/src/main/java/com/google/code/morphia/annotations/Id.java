/*
 *         Copyright 2010-2013  Olafur Gauti Gudmundsson,
 *         Scott Hernandez, and Allanbank Consulting, Inc.
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
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker for the id (_id) field.
 * 
 * @author Olafur Gauti Gudmundsson
 * @author Scott Hernandez
 * @copyright 2010-2013, Olafur Gauti Gudmundsson, Scott Hernandez and Allanbank
 *            Consulting, Inc., All Rights Reserved
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Id {
    /** The field name used for the id. */
    public static final String ID_FIELD = "_id";
}
