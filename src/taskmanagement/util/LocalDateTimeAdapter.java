package taskmanagement.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        out.value(TimeUtils.timeToString(value));
    }

    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        return TimeUtils.stringToTime(in.nextString());
    }
}
