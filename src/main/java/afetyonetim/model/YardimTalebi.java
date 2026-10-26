package afetyonetim.model;

import java.time.LocalDate;

public class YardimTalebi {

    private final String id;
    private final AfetBolgesi bolge;
    private final KaynakTuru kaynakTuru;
    private final int istenenMiktar;
    private final Aciliyet aciliyet;
    private final LocalDate talepTarihi;
    private int karsilananMiktar;

    public YardimTalebi(String id, AfetBolgesi bolge, KaynakTuru kaynakTuru, int istenenMiktar,
                        Aciliyet aciliyet, LocalDate talepTarihi) {
        this(id, bolge, kaynakTuru, istenenMiktar, aciliyet, talepTarihi, 0);
    }

    // dosyadan okurken daha önce karşılanan miktarla birlikte oluşturmak için
    public YardimTalebi(String id, AfetBolgesi bolge, KaynakTuru kaynakTuru, int istenenMiktar,
                        Aciliyet aciliyet, LocalDate talepTarihi, int karsilananMiktar) {
        if (bolge == null || kaynakTuru == null || aciliyet == null) {
            throw new IllegalArgumentException("Talep için bölge, kaynak türü ve aciliyet girilmeli");
        }
        if (istenenMiktar <= 0) {
            throw new IllegalArgumentException("İstenen miktar pozitif olmalı");
        }
        if (karsilananMiktar < 0 || karsilananMiktar > istenenMiktar) {
            throw new IllegalArgumentException("Karşılanan miktar hatalı");
        }
        this.id = id;
        this.bolge = bolge;
        this.kaynakTuru = kaynakTuru;
        this.istenenMiktar = istenenMiktar;
        this.aciliyet = aciliyet;
        this.talepTarihi = talepTarihi == null ? LocalDate.now() : talepTarihi;
        this.karsilananMiktar = karsilananMiktar;
    }

    public int kalanIhtiyac() {
        return istenenMiktar - karsilananMiktar;
    }

    public boolean tamamlandiMi() {
        return kalanIhtiyac() == 0;
    }

    public void karsila(int miktar) {
        if (miktar <= 0) {
            throw new IllegalArgumentException("Verilen miktar pozitif olmalı");
        }
        if (miktar > kalanIhtiyac()) {
            throw new IllegalArgumentException("Talep edilenden fazla yardım verilemez");
        }
        karsilananMiktar += miktar;
    }

    public TalepDurumu getDurum() {
        if (karsilananMiktar == 0) {
            return TalepDurumu.BEKLIYOR;
        }
        if (tamamlandiMi()) {
            return TalepDurumu.KARSILANDI;
        }
        return TalepDurumu.KISMEN_KARSILANDI;
    }

    public String getId() {
        return id;
    }

    public AfetBolgesi getBolge() {
        return bolge;
    }

    public KaynakTuru getKaynakTuru() {
        return kaynakTuru;
    }

    public int getIstenenMiktar() {
        return istenenMiktar;
    }

    public int getKarsilananMiktar() {
        return karsilananMiktar;
    }

    public Aciliyet getAciliyet() {
        return aciliyet;
    }

    public LocalDate getTalepTarihi() {
        return talepTarihi;
    }
}
