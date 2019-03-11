package org.yazhen.dynamodb.extract.utils.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.StringUtils;
import lombok.NonNull;

import java.io.File;
import java.io.IOException;

/**
 * Simple implementation of {@link S3FileUploader} interface. The file will be uploaded with single request.
 */
public class SimpleFileUploaderImpl implements S3FileUploader {
    private final String bucketName;
    private final String fileKeyPrefix;
    private final String localFilePath;
    private final AmazonS3 amazonS3;

    SimpleFileUploaderImpl(final String bucketName, final String fileKeyPrefix, final String localFilePath, final @NonNull AmazonS3 amazonS3) {
        if (StringUtils.isNullOrEmpty(bucketName) || StringUtils.isNullOrEmpty(fileKeyPrefix) || StringUtils.isNullOrEmpty(localFilePath)) {
            throw new IllegalArgumentException("Invalid argument provided");
        }
        this.bucketName = bucketName;
        this.fileKeyPrefix = fileKeyPrefix;
        this.localFilePath = localFilePath;
        this.amazonS3 = amazonS3;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBucketName() {
        return bucketName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFileKeyPrefix() {
        return fileKeyPrefix;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AmazonS3 getAmazonS3() {
        return amazonS3;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLocalFilePath() {
        return localFilePath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void upload() throws IOException {
        if (StringUtils.isNullOrEmpty(getBucketName()) || StringUtils.isNullOrEmpty(getFileKeyPrefix()) || getAmazonS3() == null) {
            throw new IllegalArgumentException("Cannot upload with illegal configuration");
        }

        final File file = new File(getLocalFilePath());

        if (!file.exists()) {
            throw new IllegalArgumentException("The supplied path does not exist: " + getLocalFilePath());
        }

        final PutObjectRequest putRequest = new PutObjectRequest(getBucketName(), getFileKeyPrefix() + File.separator + file.getName(), file);
        putRequest.setCannedAcl(CannedAccessControlList.BucketOwnerFullControl);

        getAmazonS3().putObject(putRequest);

        uploadEmptySuccessFile(getFileKeyPrefix());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        // no need to cleanup
    }
}
