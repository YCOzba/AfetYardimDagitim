package afetyonetim.model;

import java.time.LocalDate;

/**
 * Türüne göre doğru Kaynak alt sınıfını oluşturur.
 * Hem menüden yeni kaynak eklerken hem de dosyadan okurken kullanılıyor.
 * ekBilgi: gıda/su için "yyyy-aa-gg", ilaç için "yyyy-aa-gg,true/false", barınma için kişi kapasitesi
 */
public final class KaynakFactory {

    private KaynakFactory() {
    }

    public static Kaynak olustur(KaynakTuru tur, String id, String ad, int miktar, String ekBilgi) {
        if (tur == null) {
            throw new IllegalArgumentException("Kaynak türü seçilmeli");
        }
        return switch (tur) {
            case GIDA -> new Gida(id, ad, miktar, LocalDate.parse(ekBilgi));
            case SU -> new Su(id, ad, miktar, LocalDate.parse(ekBilgi));
            case ILAC -> {
                String[] parcalar = ekBilgi.split(",");
                boolean sogukZincir = parcalar.length > 1 && Boolean.parseBoolean(parcalar[1].trim());
                yield new Ilac(id, ad, miktar, LocalDate.parse(parcalar[0].trim()), sogukZincir);
            }
            case BARINMA -> new BarinmaMalzemesi(id, ad, miktar, Integer.parseInt(ekBilgi.trim()));
        };
    }
}
