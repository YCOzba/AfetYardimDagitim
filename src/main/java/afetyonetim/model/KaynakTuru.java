package afetyonetim.model;

public enum KaynakTuru {

    GIDA("Gıda", 1.2, 50),
    SU("Su", 1.4, 500),
    ILAC("İlaç", 1.5, 30),
    BARINMA("Barınma", 1.1, 20);

    private final String gorunenAd;
    // aynı aciliyetteki iki talepten hayati olanı (ilaç, su) biraz öne almak için
    private final double oncelikKatsayisi;
    // stok bunun altına düşünce uyarı veriliyor
    private final int kritikSeviye;

    KaynakTuru(String gorunenAd, double oncelikKatsayisi, int kritikSeviye) {
        this.gorunenAd = gorunenAd;
        this.oncelikKatsayisi = oncelikKatsayisi;
        this.kritikSeviye = kritikSeviye;
    }

    public String getGorunenAd() {
        return gorunenAd;
    }

    public double getOncelikKatsayisi() {
        return oncelikKatsayisi;
    }

    public int getKritikSeviye() {
        return kritikSeviye;
    }
}
