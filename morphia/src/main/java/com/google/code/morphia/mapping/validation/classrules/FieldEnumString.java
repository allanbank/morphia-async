/**
 * 
 */
package com.google.code.morphia.mapping.validation.classrules;

import java.util.Arrays;
import java.util.List;

import com.google.code.morphia.state.MappedField;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * 
 */
public class FieldEnumString {
    private final String display;

    public FieldEnumString(final List<MappedField> fields) {
        final StringBuffer sb = new StringBuffer(128);
        for (final MappedField mappedField : fields) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(mappedField.getMappedFieldName());
        }
        this.display = sb.toString();
    }

    public FieldEnumString(final MappedField... fields) {
        this(Arrays.asList(fields));
    }

    @Override
    public String toString() {
        return display;
    }
}
