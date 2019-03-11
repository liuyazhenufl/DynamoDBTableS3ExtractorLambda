package org.yazhen.dynamodb.extract.dynamodb.document.parser;

import com.amazonaws.services.dynamodbv2.model.ScanResult;

import java.util.List;

public class StandardJSONFormatParser implements DynamoDBDocumentParser {
    @Override
    public List<String> parseToListOfString(ScanResult scanResult) {
        return null;
    }
}
