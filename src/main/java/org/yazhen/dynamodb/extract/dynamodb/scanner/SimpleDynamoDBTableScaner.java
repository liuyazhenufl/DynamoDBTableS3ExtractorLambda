package org.yazhen.dynamodb.extract.dynamodb.scanner;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;

/**
 * Default implementation of {@link DynamoDBTableScanner} interface with default batch size.
 */
public class SimpleDynamoDBTableScaner implements DynamoDBTableScanner {
    private String tableName;
    private AmazonDynamoDB amazonDynamoDB;

    public SimpleDynamoDBTableScaner(final String tableName, final AmazonDynamoDB amazonDynamoDB) {
        this.tableName = tableName;
        this.amazonDynamoDB = amazonDynamoDB;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getTableName() {
        return tableName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AmazonDynamoDB getAmazonDynamoDB() {
        return amazonDynamoDB;
    }
}
