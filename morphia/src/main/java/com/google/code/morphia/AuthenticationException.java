/*
 *         Copyright 2010-2013  Allanbank Consulting, Inc.,
 *                         and others.
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
package com.google.code.morphia;

/**
 * Exception indicating that the system could not authenticate with the MongoDB
 * server.
 * 
 * @copyright 2010-2013, Allanbank Consulting, Inc., and others. All Rights
 *            Reserved
 */
public class AuthenticationException extends RuntimeException {

    /** Serialization version for the class. */
    private static final long serialVersionUID = -1039136124035282561L;

    /**
     * Creates a new AuthenticationException.
     * 
     * @param msg
     *            The authentication error message.
     */
    public AuthenticationException(final String msg) {
        super(msg);
    }
}
