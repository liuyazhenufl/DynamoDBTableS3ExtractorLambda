package org.yazhen.dynamodb.extract.utils.file;

import com.amazonaws.util.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class FileKeyGenerator {
    private static final String DATE_FORMAT = "yyyy-MM-dd-HH-mm-ss";
    private static final SimpleDateFormat FORMATTER;

    static {
        FORMATTER = new SimpleDateFormat(DATE_FORMAT, Locale.ROOT);
        FORMATTER.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static String generateFileKey(final String tableName, final Date extractionDate) {
        if (StringUtils.isNullOrEmpty(tableName)) {
            throw new IllegalArgumentException("Cannot generate file key with null or empty table name.");
        }

        final Date date = extractionDate == null ? new Date() : extractionDate;

        final StringBuilder builder = new StringBuilder();
        builder.append(tableName).append(File.separator).append(FORMATTER.format(date));

        return builder.toString();
    }
}
