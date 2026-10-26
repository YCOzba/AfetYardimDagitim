package afetyonetim.model;

public enum TalepDurumu {

    BEKLIYOR("Bekliyor"),
    KISMEN_KARSILANDI("Kısmen karşılandı"),
    KARSILANDI("Karşılandı");

    private final String gorunenAd;

    TalepDurumu(String gorunenAd) {
        this.gorunenAd = gorunenAd;
    }

    public String getGorunenAd() {
        return gorunenAd;
    }
}
