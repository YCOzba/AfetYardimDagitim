package afetyonetim.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// ekranda tarihleri hep aynı şekilde göstermek için
public final class TarihFormat {

    public static final DateTimeFormatter TARIH = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    public static final DateTimeFormatter TARIH_SAAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private TarihFormat() {
    }

    public static String yaz(LocalDate tarih) {
        return tarih.format(TARIH);
    }

    public static String yaz(LocalDateTime tarih) {
        return tarih.format(TARIH_SAAT);
    }
}
