package taskmanagement.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TimeUtilsTest {

    @Test
    void testTimeToStringWithNonNullTime() {
        LocalDateTime time = LocalDateTime.of(2024, 7, 15, 10, 0);
        String expected = "10:00 15.07.24";
        String actual = TimeUtils.timeToString(time);
        assertEquals(expected, actual, "Время должно быть отформатировано корректно");
    }

    @Test
    void testTimeToStringWithNullTime() {
        String expected = "null";
        String actual = TimeUtils.timeToString(null);
        assertEquals(expected, actual, "Время null должно быть отформатировано как 'null'");
    }

    @Test
    void testStringToTimeWithNonNull() {
        String dateTimeString = "14:30 27.07.24";
        LocalDateTime expected = LocalDateTime.of(2024, 7, 27, 14, 30);
        assertEquals(expected, TimeUtils.stringToTime(dateTimeString));
    }

    @Test
    void testStringToTimeWithNull() {
        String dateTimeString = "null";
        assertNull(TimeUtils.stringToTime(dateTimeString));
    }
}
