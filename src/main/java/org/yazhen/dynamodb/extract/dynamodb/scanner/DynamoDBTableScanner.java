package org.yazhen.dynamodb.extract.dynamodb.scanner;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.util.StringUtils;

import java.util.Map;

/**
 * An interface allowing consumers to scan the given DynamoDB table.
 */
public interface DynamoDBTableScanner {
    int DEFAULT_SCAN_SIZE = 50;

    /**
     * @return A non-blank {@link String} that represents the table name.
     */
    String getTableName();

    /**
     * @return A non-null {@link AmazonDynamoDB} instance that allows customers
     *         to access the DynamoDB.
     */
    AmazonDynamoDB getAmazonDynamoDB();

    /**
     * @return An integer that specifies the scan batch size. Default to 50 if
     *         implementation does not override the method.
     */
    default int getScanLimit() {
        return DEFAULT_SCAN_SIZE;
    }

    /**
     * Scan the DynamoDB table, and return the scan result.
     *
     * @param lastEvaluatedKey
     *            A {@link Map} instance that represents the last evaluated key.
     * @return A non-null, but potentially empty instance of {@link ScanResult}
     *         that encapsulates the scanned result.
     */
    default ScanResult loadNextPage(final Map<String, AttributeValue> lastEvaluatedKey) {
        if (StringUtils.isNullOrEmpty(getTableName())) {
            throw new IllegalArgumentException("Cannot scan table with blank table name.");
        }
        final ScanRequest request = new ScanRequest().withTableName(getTableName()).withLimit(getScanLimit())
                .withExclusiveStartKey(lastEvaluatedKey);
        return getAmazonDynamoDB().scan(request);
    }
}
