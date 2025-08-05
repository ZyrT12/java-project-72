package hexlet.code.util;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public class FormatTimestamp {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String convert(Timestamp timestamp) {
        if (timestamp == null) {
            return "—";
        }
        return timestamp.toLocalDateTime().format(FORMATTER);
    }
}
