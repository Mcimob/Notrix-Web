package ch.ethz.inf.peachlab.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public final class DateTimeUtils {

    private DateTimeUtils() { }

    public static String formatDateTime(LocalDateTime dateTime, Locale locale) {
        return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                .localizedBy(locale)
                .format(dateTime);
    }
}
