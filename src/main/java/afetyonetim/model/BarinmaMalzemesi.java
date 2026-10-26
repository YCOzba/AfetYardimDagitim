package afetyonetim.model;

// çadır, battaniye, uyku tulumu gibi malzemeler. son kullanma tarihi yok
public class BarinmaMalzemesi extends Kaynak {

    private final int kisiKapasitesi;

    public BarinmaMalzemesi(String id, String ad, int miktar, int kisiKapasitesi) {
        super(id, ad, miktar);
        if (kisiKapasitesi < 1) {
            throw new IllegalArgumentException("Kapasite en az 1 kişi olmalı");
        }
        this.kisiKapasitesi = kisiKapasitesi;
    }

    @Override
    public KaynakTuru getTur() {
        return KaynakTuru.BARINMA;
    }

    @Override
    public String getBirim() {
        return "adet";
    }

    @Override
    public String getEkBilgi() {
        return String.valueOf(kisiKapasitesi);
    }

    @Override
    public String getAciklama() {
        return kisiKapasitesi + " kişilik";
    }

    public int getKisiKapasitesi() {
        return kisiKapasitesi;
    }
}
