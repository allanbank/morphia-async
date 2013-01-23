package com.google.code.morphia.query;

import java.util.HashMap;
import java.util.Map;

public class GeoFieldCriteria extends FieldCriteria {

	Map<String, Object> opts = null;
	protected GeoFieldCriteria(QueryImpl<?> query, String field, FilterOperator op, Object value, boolean validateNames, boolean validateTypes, Map<String, Object> opts) {
		super(query, field, op, value, validateNames, validateTypes);
		this.opts = opts;
	}

	@Override
	public void addTo(Map<String,Object> obj) {
	    Map<String,Object> query = new HashMap<String, Object>();
	    Map<String, Object> subMap;
		switch (operator) {
			case NEAR:
			    query.put(FilterOperator.NEAR.val(), value);
				break;
			case WITHIN_BOX:
			    subMap = new HashMap<String, Object>();
			    subMap.put(operator.val(), value);
			    query.put(FilterOperator.WITHIN.val(), subMap);
				break;
			case WITHIN_CIRCLE:
                subMap = new HashMap<String, Object>();
                subMap.put(operator.val(), value);
                query.put(FilterOperator.WITHIN.val(), subMap);
				break;
			default:
				throw new UnsupportedOperationException(operator + " not supported for geo-query");
		}
		
		//add options...
		if (opts!=null)
			for (Map.Entry<String, Object> e : opts.entrySet())
				query.put(e.getKey(), e.getValue());
		
		obj.put(field, query);
	}
}
