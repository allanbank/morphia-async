/*
 *         Copyright 2013 Allanbank Consulting, Inc.
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
package com.google.code.morphia.state;

import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.bson.DocumentAssignable;

/**
 * IndexState provides a holder for the fields in an index.
 * 
 * @copyright 2013, Allanbank Consulting, Inc., All Rights Reserved
 */
public class IndexState {

    /** The fields for the index. */
    private final Document fields;

    /** The name for the index. May be <code>null</code>. */
    private final String name;

    /** The options for the index. */
    private final Document options;

    /**
     * Creates a new IndexState.
     * 
     * @param name
     *            The name for the index. May be <code>null</code>.
     * @param fields
     *            The fields for the index.
     * @param options
     *            The options for the index.
     */
    public IndexState(final String name, final DocumentAssignable fields,
            final DocumentAssignable options) {
        this.name = name;
        this.fields = fields.asDocument();
        this.options = options.asDocument();
    }

    /**
     * Returns the fields for the index..
     * 
     * @return The fields for the index..
     */
    public Document getFields() {
        return fields;
    }

    /**
     * Returns the name for the index. May be <code>null</code>.
     * 
     * @return The name for the index.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the options for the index..
     * 
     * @return The options for the index..
     */
    public Document getOptions() {
        return options;
    }

}
