package br.com.gabezy.smbintegrationspring.utils;

import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;
import java.util.Objects;

public class DateUtils {

    private static DateTimeFormatter DATE_ARQUIVO_INTERFACE_FORMATTER;
    private static DateTimeFormatter DATE_PROTHEUS_FORMATTER;

    static {
        DATE_ARQUIVO_INTERFACE_FORMATTER = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("yyyyMMdd")
                .toFormatter(new Locale("pt", "BR"));

        DATE_PROTHEUS_FORMATTER = new DateTimeFormatterBuilder()
                .appendPattern("yyyy/MM/dd")
                .toFormatter(new Locale("pt", "BR"));
    }


    public static LocalDate converteStringParaLocalDate(String data) {
        if (Objects.isNull(data) || !StringUtils.hasLength(data)) return null;
        return LocalDate.parse(data, DATE_ARQUIVO_INTERFACE_FORMATTER);
    }

    public static String converteLocalDateParaProtheus(LocalDate data) {
        if (Objects.isNull(data)) return null;
        return data.format(DATE_PROTHEUS_FORMATTER);
    }

}
