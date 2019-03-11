package org.yazhen.dynamodb.extract.utils.s3;

import com.amazonaws.services.s3.AmazonS3;

/**
 * Factory for constructing {@link S3FileUploader} interface.
 */
public class S3FileUploaderFactory {
    /**
     * Constructs an instance of {@link SimpleFileUploaderImpl}.
     * @param bucketName The bucket name. Cannot be null or empty.
     * @param fileKeyPrefix The file key prefix. Cannot be null or empty.
     * @param localFilePath The path of the local temporary file. Cannot be null or empty, the file must exist.
     * @param amazonS3 The {@link AmazonS3} client. Cannot be null.
     * @return A non-null instance of {@link S3FileUploader}.
     * @throws IllegalArgumentException If parameter conditions are not met.
     */
    public static S3FileUploader getSimpleFileUploader(final String bucketName, final String fileKeyPrefix, final String localFilePath, final AmazonS3 amazonS3) {
        return new SimpleFileUploaderImpl(bucketName, fileKeyPrefix, localFilePath, amazonS3);
    }
}
