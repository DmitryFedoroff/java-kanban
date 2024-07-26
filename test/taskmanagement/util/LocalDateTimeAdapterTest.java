package taskmanagement.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LocalDateTimeAdapterTest {

    @Test
    void testWrite() throws IOException {
        LocalDateTimeAdapter adapter = new LocalDateTimeAdapter();
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);

        LocalDateTime dateTime = LocalDateTime.of(2024, 7, 27, 14, 30);
        adapter.write(jsonWriter, dateTime);
        jsonWriter.close();

        assertEquals("\"14:30 27.07.24\"", stringWriter.toString(), "Должно быть записано время в формате строки");
    }

    @Test
    void testRead() throws IOException {
        LocalDateTimeAdapter adapter = new LocalDateTimeAdapter();
        String json = "\"14:30 27.07.24\"";
        StringReader stringReader = new StringReader(json);
        JsonReader jsonReader = new JsonReader(stringReader);

        LocalDateTime dateTime = adapter.read(jsonReader);
        jsonReader.close();

        assertEquals(LocalDateTime.of(2024, 7, 27, 14, 30), dateTime, "Должно быть прочитано время из строки");
    }

    @Test
    void testGsonIntegration() {
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
        LocalDateTime dateTime = LocalDateTime.of(2024, 7, 27, 14, 30);
        String json = gson.toJson(dateTime);

        assertEquals("\"14:30 27.07.24\"", json, "Должна быть сериализация в JSON");

        LocalDateTime deserializedDateTime = gson.fromJson(json, LocalDateTime.class);
        assertEquals(dateTime, deserializedDateTime, "Должна быть десериализация из JSON");
    }

    @Test
    void testInvalidJson() {
        LocalDateTimeAdapter adapter = new LocalDateTimeAdapter();
        String invalidJson = "\"invalid\"";
        StringReader stringReader = new StringReader(invalidJson);
        JsonReader jsonReader = new JsonReader(stringReader);

        assertThrows(DateTimeParseException.class, () -> adapter.read(jsonReader), "Должно быть выброшено исключение при некорректном JSON");
    }
}
