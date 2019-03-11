package org.yazhen.dynamodb.extract.dynamodb.document.parser;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface DynamoDBDocumentParser {
    /**
     * Parses the supplied {@link ScanResult} into a {@link List} of {@link String}.
     * @param scanResult A non-null instance of {@link ScanResult} from {@link AmazonDynamoDB#scan} call.
     * @return A non-null {@link List} of {@link String} that is parsed from the supplied {@link ScanResult}
     */
    List<String> parseToListOfString(final ScanResult scanResult) throws UnsupportedEncodingException;
}
