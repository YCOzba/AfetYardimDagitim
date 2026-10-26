package afetyonetim.model;

public class AfetBolgesi {

    private final String id;
    private final String ad;
    private final String il;
    private int etkilenenKisi;
    // 1: hafif hasar ... 5: çok ağır hasar
    private int hasarSeviyesi;

    public AfetBolgesi(String id, String ad, String il, int etkilenenKisi, int hasarSeviyesi) {
        if (ad == null || ad.isBlank()) {
            throw new IllegalArgumentException("Bölge adı boş olamaz");
        }
        this.id = id;
        this.ad = ad.trim();
        this.il = il == null ? "" : il.trim();
        setEtkilenenKisi(etkilenenKisi);
        setHasarSeviyesi(hasarSeviyesi);
    }

    public String getId() {
        return id;
    }

    public String getAd() {
        return ad;
    }

    public String getIl() {
        return il;
    }

    public int getEtkilenenKisi() {
        return etkilenenKisi;
    }

    public void setEtkilenenKisi(int etkilenenKisi) {
        if (etkilenenKisi < 0) {
            throw new IllegalArgumentException("Etkilenen kişi sayısı negatif olamaz");
        }
        this.etkilenenKisi = etkilenenKisi;
    }

    public int getHasarSeviyesi() {
        return hasarSeviyesi;
    }

    public void setHasarSeviyesi(int hasarSeviyesi) {
        if (hasarSeviyesi < 1 || hasarSeviyesi > 5) {
            throw new IllegalArgumentException("Hasar seviyesi 1 ile 5 arasında olmalı");
        }
        this.hasarSeviyesi = hasarSeviyesi;
    }

    @Override
    public String toString() {
        return il + " / " + ad;
    }
}
