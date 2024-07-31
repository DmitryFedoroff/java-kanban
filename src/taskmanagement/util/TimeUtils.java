package taskmanagement.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");

    public static String timeToString(LocalDateTime time) {
        if (time == null) {
            return "null";
        }
        return time.format(DATE_TIME_FORMATTER);
    }

    public static LocalDateTime stringToTime(String time) {
        return "null".equals(time) ? null : LocalDateTime.parse(time, DATE_TIME_FORMATTER);
    }
}
