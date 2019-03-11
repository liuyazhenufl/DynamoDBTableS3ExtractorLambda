package org.yazhen.dynamodb.extract.injection;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.cfg4j.provider.ConfigurationProvider;
import org.cfg4j.provider.ConfigurationProviderBuilder;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.filesprovider.ConfigFilesProvider;
import org.cfg4j.source.files.FilesConfigurationSource;
import org.yazhen.dynamodb.extract.configuration.ExtractJobConfiguration;
import org.yazhen.dynamodb.extract.dynamodb.document.parser.DynamoDBDocumentParser;
import org.yazhen.dynamodb.extract.dynamodb.document.parser.DynamoDBJSONFormatDocumentParser;
import org.yazhen.dynamodb.extract.dynamodb.document.parser.StandardJSONFormatParser;
import org.yazhen.dynamodb.extract.dynamodb.scanner.DynamoDBTableScanner;
import org.yazhen.dynamodb.extract.dynamodb.scanner.SimpleDynamoDBTableScaner;
import org.yazhen.dynamodb.extract.worker.DatabaseExtractWorker;
import org.yazhen.dynamodb.extract.worker.DatabaseExtractWorkerImpl;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;

public class DynamoDBTableS3ExtractorLambdaMoudle extends AbstractModule {
    private final String runningstage;

    public DynamoDBTableS3ExtractorLambdaMoudle(final String runningstage) {
        this.runningstage = runningstage;
    }

    @Provides
    @Singleton
    ExtractJobConfiguration getExtractJobConfiguration() {
        final String root = System.getProperty("user.dir");
        ConfigFilesProvider configFilesProvider = () -> Arrays.asList(Paths.get(root+ File.separator + "DynamoDBTableS3ExtractorLambda.yaml"));
        ConfigurationSource source = new FilesConfigurationSource(configFilesProvider);
        final ConfigurationProvider configProvider = new ConfigurationProviderBuilder()
                .withConfigurationSource(source)
                .build();

        ExtractJobConfiguration config = configProvider.bind(runningstage, ExtractJobConfiguration.class);

        return config;
    }

    @Provides
    @Singleton
    AmazonS3 getAmazonS3(final ExtractJobConfiguration config) {
        final AmazonS3 s3 = AmazonS3Client.builder().standard().withRegion(config.s3BucketName()).build();
        return s3;
    }

    @Provides
    @Singleton
    AmazonDynamoDB getAmazonDynamoDB(final ExtractJobConfiguration config) {
        return AmazonDynamoDBClientBuilder.standard().withRegion(config.dynamoDBRegion()).build();
    }

    @Provides
    @Singleton
    DynamoDBTableScanner getDynamoDBTableScanner(final ExtractJobConfiguration config,
                                                 final AmazonDynamoDB amazonDynamoDB) {
        final DynamoDBTableScanner scanner = new SimpleDynamoDBTableScaner(config.dynamoDBTableName(), amazonDynamoDB);
        return scanner;
    }

    DynamoDBDocumentParser getDynamoDBJSONFormatDocumentParser() {
        return new DynamoDBJSONFormatDocumentParser();
    }

    DynamoDBDocumentParser getStandardJSONFormatParser() {
        return new StandardJSONFormatParser();
    }

    @Provides
    @Singleton
    DatabaseExtractWorker getDatabaseExtractWorker(final ExtractJobConfiguration config,
                                                   final DynamoDBTableScanner tableScanner,
                                                   final AmazonS3 amazonS3) {
        final DynamoDBDocumentParser parser = config.useDynamoDBJSONFormat() ? getDynamoDBJSONFormatDocumentParser() :
                getStandardJSONFormatParser();
        return new DatabaseExtractWorkerImpl(config.dynamoDBTableName(), config.s3BucketName(),
                tableScanner,
                amazonS3,
                new Date(), parser);
    }
}
