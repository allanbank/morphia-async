package com.google.code.morphia.query;

public class UpdateResults<T> {
    private final long wr;

    public UpdateResults(final long wr) {
        this.wr = wr;
    }

    public String getError() {
        return "";
    }

    public boolean getHadError() {
        final String error = getError();
        return (error != null) && !error.isEmpty();
    }

    /** @return number inserted; this should be either 0/1. */
    public int getInsertedCount() {
        return !getUpdatedExisting() ? getN() : 0;
    }

    /** @return the new _id field if an insert/upsert was performed */
    public Object getNewId() {
        return null;
    }

    /** @return number updated */
    public int getUpdatedCount() {
        return getUpdatedExisting() ? getN() : 0;
    }

    /** @return true if updated, false if inserted or none effected */
    public boolean getUpdatedExisting() {
        return (wr > 0);
    }

    /** @return number of affected documents */
    protected int getN() {
        return (int) wr;
    }
}
