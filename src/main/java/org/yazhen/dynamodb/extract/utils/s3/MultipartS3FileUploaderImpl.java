package org.yazhen.dynamodb.extract.utils.s3;

import com.amazonaws.services.s3.AmazonS3;

import java.io.IOException;

public class MultipartS3FileUploaderImpl implements S3FileUploader {
    @Override
    public String getBucketName() {
        return null;
    }

    @Override
    public String getFileKeyPrefix() {
        return null;
    }

    @Override
    public AmazonS3 getAmazonS3() {
        return null;
    }

    @Override
    public String getLocalFilePath() {
        return null;
    }

    @Override
    public void upload() {

    }

    @Override
    public void uploadEmptySuccessFile(String fileKey) {

    }

    @Override
    public void close() throws IOException {

    }
}
