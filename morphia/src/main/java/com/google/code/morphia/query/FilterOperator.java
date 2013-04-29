package com.google.code.morphia.query;

/**
 * 
 * @author Scott Hernandez
 * 
 */
public enum FilterOperator {
    ALL("$all"), ELEMENT_MATCH("$elemMatch"), EQUAL("$eq"), EXISTS("$exists"), GREATER_THAN(
            "$gt"), GREATER_THAN_OR_EQUAL("$gte"), IN("$in"), LESS_THAN("$lt"), LESS_THAN_OR_EQUAL(
            "$lte"), MOD("$mod"), NEAR("$near"), NOT("$not"), NOT_EQUAL("$ne"), NOT_IN(
            "$nin"), SIZE("$size"), TYPE("$type"), WHERE("$where"), WITHIN(
            "$within"), WITHIN_BOX("$box"), WITHIN_CIRCLE("$center");

    public static FilterOperator fromString(final String val) {
        for (int i = 0; i < values().length; i++) {
            final FilterOperator fo = values()[i];
            if (fo.equals(val)) {
                return fo;
            }
        }
        return null;
    }

    private String value;

    private FilterOperator(final String val) {
        value = val;
    }

    public String val() {
        return value;
    }

    private boolean equals(final String val) {
        return value.equals(val);
    }
}