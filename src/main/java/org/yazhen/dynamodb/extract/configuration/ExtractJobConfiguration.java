package org.yazhen.dynamodb.extract.configuration;

public interface ExtractJobConfiguration {
    String s3BucketName();

    String s3BucketRegion();

    String dynamoDBTableName();

    String dynamoDBRegion();

    Boolean useMultiPart();

    Boolean useDynamoDBJSONFormat();

}
