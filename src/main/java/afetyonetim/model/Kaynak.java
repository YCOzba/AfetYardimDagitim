package afetyonetim.model;

import java.time.LocalDate;

/**
 * Depodaki bütün yardım malzemelerinin ortak üst sınıfı.
 * Türü, birimi ve türe özel bilgiler alt sınıflarda belirleniyor.
 */
public abstract class Kaynak {

    private final String id;
    private final String ad;
    private int miktar;

    protected Kaynak(String id, String ad, int miktar) {
        if (ad == null || ad.isBlank()) {
            throw new IllegalArgumentException("Kaynak adı boş olamaz");
        }
        if (miktar < 0) {
            throw new IllegalArgumentException("Miktar negatif olamaz");
        }
        this.id = id;
        this.ad = ad.trim();
        this.miktar = miktar;
    }

    public abstract KaynakTuru getTur();

    public abstract String getBirim();

    // dosyaya yazarken kullanılıyor (skt, kapasite gibi türe özel alanlar)
    public abstract String getEkBilgi();

    // listelerken ekranda gösterilen açıklama
    public abstract String getAciklama();

    // stokta varsa kullanılabilir, tarihi olan kaynaklar bunu override ediyor
    public boolean kullanilabilirMi(LocalDate tarih) {
        return miktar > 0;
    }

    public void stokEkle(int eklenecek) {
        if (eklenecek <= 0) {
            throw new IllegalArgumentException("Eklenecek miktar pozitif olmalı");
        }
        miktar += eklenecek;
    }

    public void stokDus(int dusulecek) {
        if (dusulecek <= 0) {
            throw new IllegalArgumentException("Düşülecek miktar pozitif olmalı");
        }
        if (dusulecek > miktar) {
            throw new YetersizStokException(ad + " için yeterli stok yok (mevcut: " + miktar + ")");
        }
        miktar -= dusulecek;
    }

    public String getId() {
        return id;
    }

    public String getAd() {
        return ad;
    }

    public int getMiktar() {
        return miktar;
    }

    @Override
    public String toString() {
        return id + " - " + ad + " (" + miktar + " " + getBirim() + ")";
    }
}
