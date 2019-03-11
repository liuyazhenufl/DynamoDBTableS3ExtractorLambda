package org.yazhen.dynamodb.extract.worker;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.util.StringUtils;
import org.yazhen.dynamodb.extract.dynamodb.document.parser.DynamoDBDocumentParser;
import org.yazhen.dynamodb.extract.dynamodb.scanner.DynamoDBTableScanner;
import org.yazhen.dynamodb.extract.utils.file.FileKeyGenerator;
import org.yazhen.dynamodb.extract.utils.file.LocalResultFileWriter;
import org.yazhen.dynamodb.extract.utils.file.LocalResultFileWriterFactory;
import org.yazhen.dynamodb.extract.utils.s3.S3FileUploader;
import org.yazhen.dynamodb.extract.utils.s3.S3FileUploaderFactory;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of {@link DatabaseExtractWorker} interface.
 */
public class DatabaseExtractWorkerImpl implements DatabaseExtractWorker {
    private final String tableName;
    private final String bucketName;
    private final DynamoDBTableScanner tableScanner;
    private final AmazonS3 amazonS3;
    private final Date extractionStartDateTime;
    private final DynamoDBDocumentParser parser;
    private static final int SLEEP_TIME_BETWEEN_DDB_CALLS = 400;

    public DatabaseExtractWorkerImpl(final String tableName, final String bucketName,
                                     final DynamoDBTableScanner tableScanner, final AmazonS3 amazonS3,
                                     final Date extractionStartDateTime,
                                     final DynamoDBDocumentParser parser) {
        if (StringUtils.isNullOrEmpty(tableName) || tableScanner == null || amazonS3 == null || extractionStartDateTime == null || parser == null) {
            throw new IllegalArgumentException("Invalid parameter provided when constructing the extract worker " +
                    "implementation.");
        }
        this.tableName = tableName;
        this.bucketName = bucketName;
        this.tableScanner = tableScanner;
        this.amazonS3 = amazonS3;
        this.extractionStartDateTime = extractionStartDateTime;
        this.parser = parser;
    }

    @Override
    public void performExtract() throws IOException {
        int processedItem = 0;
        Map<String, AttributeValue> lastEvaluatedKey = null;

        final String fileKey = FileKeyGenerator.generateFileKey(getTableName(), extractionStartDateTime);

        try (final LocalResultFileWriter writer =
                     LocalResultFileWriterFactory.getDefaultResultFileWriter(fileKey);
             final S3FileUploader uploader = S3FileUploaderFactory.getSimpleFileUploader(bucketName, fileKey,
                     writer.getAbsolutePath(), amazonS3)) {
            do {
                final ScanResult result = tableScanner.loadNextPage(lastEvaluatedKey);
                final List<String> contents = parser.parseToListOfString(result);
                writer.appendToLocalFile(contents);

                processedItem += contents.size();

                lastEvaluatedKey = result.getLastEvaluatedKey();

                Thread.sleep(SLEEP_TIME_BETWEEN_DDB_CALLS);
            } while (lastEvaluatedKey != null);

            uploader.upload();
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getTableName() {
        return tableName;
    }
}
