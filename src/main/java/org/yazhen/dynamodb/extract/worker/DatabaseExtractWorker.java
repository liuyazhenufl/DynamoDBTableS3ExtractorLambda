package org.yazhen.dynamodb.extract.worker;

import java.io.IOException;

public interface DatabaseExtractWorker {
    /**
     * Performs the full DynamoDB table extraction, stores to local file, and
     * uploads to S3 bucket.
     */
    public void performExtract() throws IOException;

    /**
     * @return The name of the table that is extracted.
     */
    public String getTableName();
}
