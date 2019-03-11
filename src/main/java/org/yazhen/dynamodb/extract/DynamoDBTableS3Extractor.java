package org.yazhen.dynamodb.extract;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.yazhen.dynamodb.extract.injection.DynamoDBTableS3ExtractorLambdaMoudle;
import org.yazhen.dynamodb.extract.worker.DatabaseExtractWorker;

import java.io.IOException;

public class DynamoDBTableS3Extractor implements RequestHandler {
    @Override
    public Object handleRequest(Object input, Context context) {
        Injector injector = Guice
                .createInjector(new DynamoDBTableS3ExtractorLambdaMoudle("dev"));
        final DatabaseExtractWorker worker = injector.getInstance(DatabaseExtractWorker.class);

        try {
            worker.performExtract();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
}
