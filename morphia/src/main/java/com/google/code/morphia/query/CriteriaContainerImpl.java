package com.google.code.morphia.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CriteriaContainerImpl extends AbstractCriteria implements
        Criteria, CriteriaContainer {
    protected QueryImpl<?> query;
    private final List<Criteria> children;

    private final CriteriaJoin joinMethod;

    protected CriteriaContainerImpl(final CriteriaJoin joinMethod) {
        this.joinMethod = joinMethod;
        this.children = new ArrayList<Criteria>();
    }

    protected CriteriaContainerImpl(final QueryImpl<?> query,
            final CriteriaJoin joinMethod) {
        this(joinMethod);
        this.query = query;
    }

    @Override
    public void add(final Criteria... criteria) {
        for (final Criteria c : criteria) {
            c.attach(this);
            this.children.add(c);
        }
    }

    @Override
    public void addTo(final Map<String, Object> obj) {
        if (this.joinMethod == CriteriaJoin.AND) {
            for (final Criteria child : this.children) {
                child.addTo(obj);
            }

        }
        else if (this.joinMethod == CriteriaJoin.OR) {
            final ArrayList or = new ArrayList();

            for (final Criteria child : this.children) {
                final Map<String, Object> container = new HashMap<String, Object>();
                child.addTo(container);
                or.add(container);
            }

            obj.put("$or", or);
        }
    }

    @Override
    public CriteriaContainer and(final Criteria... criteria) {
        return collect(CriteriaJoin.AND, criteria);
    }

    @Override
    public FieldEnd<? extends CriteriaContainer> criteria(final String name) {
        return this.criteria(name, this.query.isValidatingNames());
    }

    @Override
    public CriteriaContainer or(final Criteria... criteria) {
        return collect(CriteriaJoin.OR, criteria);
    }

    public void remove(final Criteria criteria) {
        this.children.remove(criteria);
    }

    private CriteriaContainer collect(final CriteriaJoin cj,
            final Criteria... criteria) {
        final CriteriaContainerImpl parent = new CriteriaContainerImpl(
                this.query, cj);

        for (final Criteria c : criteria) {
            parent.add(c);
        }

        add(parent);

        return parent;
    }

    private FieldEnd<? extends CriteriaContainer> criteria(final String field,
            final boolean validateName) {
        return new FieldEndImpl<CriteriaContainerImpl>(this.query, field, this,
                validateName);
    }
}
