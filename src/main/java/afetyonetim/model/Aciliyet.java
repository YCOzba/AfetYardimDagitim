package afetyonetim.model;

public enum Aciliyet {

    DUSUK("Düşük", 10),
    ORTA("Orta", 20),
    YUKSEK("Yüksek", 30),
    KRITIK("Kritik", 40);

    private final String gorunenAd;
    private final int puan;

    Aciliyet(String gorunenAd, int puan) {
        this.gorunenAd = gorunenAd;
        this.puan = puan;
    }

    public String getGorunenAd() {
        return gorunenAd;
    }

    public int getPuan() {
        return puan;
    }
}
