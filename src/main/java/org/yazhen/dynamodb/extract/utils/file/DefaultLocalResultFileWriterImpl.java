package org.yazhen.dynamodb.extract.utils.file;

import lombok.NonNull;
import org.yazhen.dynamodb.extract.utils.Constants;

import java.io.*;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Stateful implementation, all resources are encapsulated within the class
 */
public class DefaultLocalResultFileWriterImpl implements LocalResultFileWriter {
    private final String fileName;
    private final File localResultFile;
    private final String fileKey;
    private final AtomicBoolean closed;

    private BufferedWriter writer;

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String getAbsolutePath() {
        return localResultFile.getAbsolutePath();
    }

    @Override
    public String getFileKey() {
        return fileKey;
    }

    @Override
    public void appendToLocalFile(List<String> contents) throws IOException {
        if (closed.get()) {
            throw new IllegalStateException("Local file writer has been closed.");
        }

        for (final String s : contents) {
            writer.write(s);
            writer.newLine();
        }

        writer.flush();
    }

    DefaultLocalResultFileWriterImpl(final @NonNull String fileKey) throws IOException {
        this.fileKey = fileKey;
        this.fileName = UUID.randomUUID().toString();
        localResultFile = new File(Constants.ROOT_TEMP_DIRECTORY + File.separator + fileKey + File.separator + fileName);
        closed = new AtomicBoolean(false);
        initialize();
    }

    private void initialize() throws IOException {
        localResultFile.getParentFile().mkdir();
        localResultFile.createNewFile();

        writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(localResultFile)));
    }

    @Override
    public void close() throws IOException {
        closed.set(true);
        writer.close();
    }
}
