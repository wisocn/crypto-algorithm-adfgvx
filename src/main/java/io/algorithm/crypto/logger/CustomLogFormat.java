package io.algorithm.crypto.logger;

import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.Date;

public class CustomLogFormat extends Formatter {

    private static final String PATTERN = "dd.MM.yyyy'-'HH:mm";

    @Override
    public String format(final LogRecord record) {
        return String.format(
                "[%1$s] %2$-7s %3$s\n",
                new SimpleDateFormat(PATTERN).format(
                        new Date(record.getMillis())),
                record.getLevel().getName(), formatMessage(record));
    }
}
