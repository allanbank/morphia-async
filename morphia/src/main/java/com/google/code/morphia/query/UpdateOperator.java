package com.google.code.morphia.query;

/**
 * 
 * @author Scott Hernandez
 * 
 */
public enum UpdateOperator {
    ADD_TO_SET("$addToSet"), ADD_TO_SET_EACH("$addToSet"), EACH("$each"), Foo(
            "$foo"), INC("$inc"), POP("$pop"), PULL("$pull"), PULL_ALL(
            "$pullAll"), // fake to indicate that the value should be wrapped in
                         // an $each
    PUSH("$push"), PUSH_ALL("$pushAll"), SET("$set"), UNSET("$unset");

    public static UpdateOperator fromString(final String val) {
        for (int i = 0; i < values().length; i++) {
            final UpdateOperator fo = values()[i];
            if (fo.equals(val)) {
                return fo;
            }
        }
        return null;
    }

    private String value;

    private UpdateOperator(final String val) {
        value = val;
    }

    public String val() {
        return value;
    }

    private boolean equals(final String val) {
        return value.equals(val);
    }
}