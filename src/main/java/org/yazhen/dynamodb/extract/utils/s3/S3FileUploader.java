package org.yazhen.dynamodb.extract.utils.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.StringUtils;
import org.yazhen.dynamodb.extract.utils.Constants;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

public interface S3FileUploader extends Closeable {
    String SUCCESS_FILE_NAME = "_SUCCESS";

    /**
     * @return A non-null, non-empty String that specifies the S3 bucket.
     */
    String getBucketName();

    /**
     * @return A non-null, non-empty String that specifies the key prefix.
     */
    String getFileKeyPrefix();

    /**
     * @return A non-null {@link AmazonS3} instance.
     */
    AmazonS3 getAmazonS3();

    /**
     * @return A non-null, non-empty String that specifies the path of the file that needs to be uploaded.
     */
    String getLocalFilePath();

    /**
     * Uploads the file to S3 bucket.
     */
    void upload() throws IOException;

    /**
     * Uploads an empty {code}_SUCCESS{/code} file to the S3 directory. Imitates the behavior of AWS Data Pipeline.
     * @param fileKeyPrefix A non-null, non-empty string that specifies the S3 directory.
     * @throws IOException If cannot create the empty local file.
     * @throws IllegalArgumentException If parameter conditions are not met.
     */
    default void uploadEmptySuccessFile(final String fileKeyPrefix) throws IOException {
        if(StringUtils.isNullOrEmpty(fileKeyPrefix)) {
            throw new IllegalArgumentException("Cannot upload success file with null or empty prefix");
        }

        final File localSuccess = new File(Constants.ROOT_TEMP_DIRECTORY + File.separator + SUCCESS_FILE_NAME);
        localSuccess.createNewFile();

        final PutObjectRequest putRequest = new PutObjectRequest(getBucketName(), fileKeyPrefix + File.separator + SUCCESS_FILE_NAME, localSuccess);

        putRequest.setCannedAcl(CannedAccessControlList.BucketOwnerFullControl);

        getAmazonS3().putObject(putRequest);
    }
}
