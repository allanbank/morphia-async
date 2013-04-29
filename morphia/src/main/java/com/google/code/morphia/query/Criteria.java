package com.google.code.morphia.query;

import java.util.Map;

public interface Criteria {
    void addTo(Map<String, Object> obj);

    void attach(CriteriaContainerImpl container);
}
