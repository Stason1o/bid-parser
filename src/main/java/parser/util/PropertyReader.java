package parser.util;

import lombok.SneakyThrows;

import java.io.FileReader;
import java.io.InputStream;
import java.util.Properties;

public class PropertyReader {

    @SneakyThrows
    public Properties readPropertiesFromStream(final InputStream stream) {
        final Properties properties = new Properties();
        properties.load(stream);

        return properties;
    }
}
