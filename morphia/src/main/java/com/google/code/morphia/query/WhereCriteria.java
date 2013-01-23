package com.google.code.morphia.query;

import java.util.Map;

import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.bson.DocumentAssignable;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.allanbank.mongodb.bson.element.JavaScriptElement;
import com.allanbank.mongodb.bson.element.JavaScriptWithScopeElement;

public class WhereCriteria extends AbstractCriteria implements Criteria {

    private String js;
    private Document scope;

    public WhereCriteria(String js) {
        this.js = js;
    }

    public WhereCriteria(String js, DocumentAssignable scope) {
        this.js = js;
        this.scope = scope.asDocument();
    }

    public void addTo(Map<String, Object> obj) {
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
