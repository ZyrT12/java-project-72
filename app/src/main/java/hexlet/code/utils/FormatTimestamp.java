package hexlet.code.utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class FormatTimestamp {

    private static final DateTimeFormatter DEFAULT_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private FormatTimestamp() { }

    public static String convert(Timestamp ts) {
        if (ts == null) {
            return "";
        }
        return ts.toLocalDateTime().format(DEFAULT_FMT);
    }

    public static String convert(LocalDateTime dt) {
        if (dt == null) {
            return "";
        }
        return dt.format(DEFAULT_FMT);
    }
}
