package org.yazhen.dynamodb.extract.utils.file;

import java.io.IOException;

public class LocalResultFileWriterFactory {
    public static LocalResultFileWriter getDefaultResultFileWriter(final String fileKey) throws IOException {
        return new DefaultLocalResultFileWriterImpl(fileKey);
    }
}
