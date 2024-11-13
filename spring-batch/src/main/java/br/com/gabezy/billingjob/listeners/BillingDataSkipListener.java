package br.com.gabezy.billingjob.listeners;

import br.com.gabezy.billingjob.domain.BillingData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.item.file.FlatFileParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class BillingDataSkipListener implements SkipListener<BillingData, BillingData> {

    private final Path skippedItemsFile;
    private static final Logger log = LoggerFactory.getLogger(BillingDataSkipListener.class);

    public BillingDataSkipListener(String skippedItemsFile) {
        this.skippedItemsFile = Paths.get(skippedItemsFile);
    }

    @Override
    public void onSkipInRead(Throwable t) {
        if (t instanceof FlatFileParseException exception) {
            String rawLine = exception.getInput();
            int lineNumber = exception.getLineNumber();
            String skippedLine = lineNumber + "|" + rawLine + System.lineSeparator();

            try {
                Files.writeString(this.skippedItemsFile, skippedLine, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
            } catch (IOException e) {
                log.error("Unable to write skipped line: {} - in path: {}", skippedLine, this.skippedItemsFile);
                throw new RuntimeException("Unable to write skipped line");
            }

        }
    }
}
