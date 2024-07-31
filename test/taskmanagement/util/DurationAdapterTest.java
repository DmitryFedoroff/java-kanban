package taskmanagement.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DurationAdapterTest {

    @Test
    void testWrite() throws IOException {
        DurationAdapter adapter = new DurationAdapter();
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);

        Duration duration = Duration.ofMinutes(120);
        adapter.write(jsonWriter, duration);
        jsonWriter.close();

        assertEquals("120", stringWriter.toString(), "Должно быть записано количество минут");
    }

    @Test
    void testRead() throws IOException {
        DurationAdapter adapter = new DurationAdapter();
        String json = "120";
        StringReader stringReader = new StringReader(json);
        JsonReader jsonReader = new JsonReader(stringReader);

        Duration duration = adapter.read(jsonReader);
        jsonReader.close();

        assertEquals(Duration.ofMinutes(120), duration, "Должна быть прочитана длительность в минутах");
    }

    @Test
    void testGsonIntegration() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Duration.class, new DurationAdapter()).create();
        Duration duration = Duration.ofMinutes(90);
        String json = gson.toJson(duration);

        assertEquals("90", json, "Должна быть сериализация в JSON");

        Duration deserializedDuration = gson.fromJson(json, Duration.class);
        assertEquals(duration, deserializedDuration, "Должна быть десериализация из JSON");
    }

    @Test
    void testInvalidJson() {
        DurationAdapter adapter = new DurationAdapter();
        String invalidJson = "\"invalid\"";
        StringReader stringReader = new StringReader(invalidJson);
        JsonReader jsonReader = new JsonReader(stringReader);

        assertThrows(NumberFormatException.class, () -> adapter.read(jsonReader), "Должно быть выброшено исключение при некорректном JSON");
    }
}
