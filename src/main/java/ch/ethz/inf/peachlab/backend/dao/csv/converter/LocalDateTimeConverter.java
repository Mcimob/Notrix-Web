package ch.ethz.inf.peachlab.backend.dao.csv.converter;
import com.opencsv.bean.AbstractBeanField;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeConverter extends AbstractBeanField<LocalDateTime, String> {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");

    @Override
    protected LocalDateTime convert(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(value.trim(), FORMATTER);
    }
}
