package org.yazhen.dynamodb.extract.utils.file;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

public interface LocalResultFileWriter extends Closeable {

    String getFileName();

    String getAbsolutePath();

    String getFileKey();

    void appendToLocalFile(final List<String> contents) throws IOException;
}
