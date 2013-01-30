/*
 *         Copyright 2010-2013 Allanbank Consulting, Inc.
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

/**
 * MappedMethod provides infromation on methods with callback annotations.
 * 
 * @copyright 2013, Allanbank Consulting, Inc., All Rights Reserved
 */
public class MappedMethod {

    /** If true the method should be called after the object is loaded. */
    private boolean postLoad;

    /** If true the method should be called after being persisted. */
    private boolean postPersist;

    /** If true the method should be called before the object is loaded. */
    private boolean preLoad;

    /** If true the method should be called before the object is persisted. */
    private boolean prePresist;

    /** If true the method should be called before the object is saved. */
    private boolean preSave;

    /**
     * Creates a new MappedMethod.
     */
    public MappedMethod() {
        // TODO Auto-generated constructor stub
    }

    /**
     * Returns the postLoad value.
     * 
     * @return The postLoad value.
     */
    public boolean isPostLoad() {
        return postLoad;
    }

    /**
     * Returns the postPersist value.
     * 
     * @return The postPersist value.
     */
    public boolean isPostPersist() {
        return postPersist;
    }

    /**
     * Returns the preLoad value.
     * 
     * @return The preLoad value.
     */
    public boolean isPreLoad() {
        return preLoad;
    }

    /**
     * Returns the prePresist value.
     * 
     * @return The prePresist value.
     */
    public boolean isPrePresist() {
        return prePresist;
    }

    /**
     * Returns the preSave value.
     * 
     * @return The preSave value.
     */
    public boolean isPreSave() {
        return preSave;
    }

    /**
     * Sets the value of postLoad to the new value.
     * 
     * @param postLoad
     *            The new value for the postLoad.
     */
    public void setPostLoad(final boolean postLoad) {
        this.postLoad = postLoad;
    }

    /**
     * Sets the value of postPersist to the new value.
     * 
     * @param postPersist
     *            The new value for the postPersist.
     */
    public void setPostPersist(final boolean postPersist) {
        this.postPersist = postPersist;
    }

    /**
     * Sets the value of preLoad to the new value.
     * 
     * @param preLoad
     *            The new value for the preLoad.
     */
    public void setPreLoad(final boolean preLoad) {
        this.preLoad = preLoad;
    }

    /**
     * Sets the value of prePresist to the new value.
     * 
     * @param prePresist
     *            The new value for the prePresist.
     */
    public void setPrePresist(final boolean prePresist) {
        this.prePresist = prePresist;
    }

    /**
     * Sets the value of preSave to the new value.
     * 
     * @param preSave
     *            The new value for the preSave.
     */
    public void setPreSave(final boolean preSave) {
        this.preSave = preSave;
    }

}
