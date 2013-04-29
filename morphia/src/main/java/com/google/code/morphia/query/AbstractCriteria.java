package com.google.code.morphia.query;

public abstract class AbstractCriteria implements Criteria {
    protected CriteriaContainerImpl attachedTo = null;

    @Override
    public void attach(final CriteriaContainerImpl container) {
        if (this.attachedTo != null) {
            this.attachedTo.remove(this);
        }

        this.attachedTo = container;
    }
}
