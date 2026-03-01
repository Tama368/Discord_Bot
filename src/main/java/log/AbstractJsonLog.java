package log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;

public abstract class AbstractJsonLog<T> {

    protected final File file;
    protected final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    protected AbstractJsonLog(String path, Supplier<T> initializer) {
        this.file = new File(path);

        try {
            if (!file.exists()) {
                file.createNewFile();
                T initData = initializer.get();
                write(initData);
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot create log file", e);
        }
    }

    protected T read(Class<T> type) {
        try {
            return mapper.readValue(file, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void write(T data) {
        try {
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(file, data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}