package com.google.code.morphia.query;

import java.util.Map;

import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.bson.DocumentAssignable;
import com.allanbank.mongodb.bson.element.JavaScriptElement;
import com.allanbank.mongodb.bson.element.JavaScriptWithScopeElement;

public class WhereCriteria extends AbstractCriteria implements Criteria {

    private final String js;
    private Document scope;

    public WhereCriteria(final String js) {
        this.js = js;
    }

    public WhereCriteria(final String js, final DocumentAssignable scope) {
        this.js = js;
        this.scope = scope.asDocument();
    }

    @Override
    public void addTo(final Map<String, Object> obj) {
        if (scope != null) {
            obj.put(FilterOperator.WHERE.val(), new JavaScriptWithScopeElement(
                    FilterOperator.WHERE.val(), js, this.scope));
        }
        else {
            obj.put(FilterOperator.WHERE.val(), new JavaScriptElement(
                    FilterOperator.WHERE.val(), js));
        }
    }
}
