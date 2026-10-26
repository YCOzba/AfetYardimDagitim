package afetyonetim.model;

import java.time.LocalDateTime;

// hangi talebe ne zaman ne kadar yardım gönderildiğini tutar
public class DagitimKaydi {

    private final String talepId;
    private final String bolgeAdi;
    private final KaynakTuru kaynakTuru;
    private final int miktar;
    private final LocalDateTime tarih;
    private final String stratejiAdi;

    public DagitimKaydi(String talepId, String bolgeAdi, KaynakTuru kaynakTuru, int miktar,
                        LocalDateTime tarih, String stratejiAdi) {
        this.talepId = talepId;
        this.bolgeAdi = bolgeAdi;
        this.kaynakTuru = kaynakTuru;
        this.miktar = miktar;
        this.tarih = tarih;
        this.stratejiAdi = stratejiAdi;
    }

    public String getTalepId() {
        return talepId;
    }

    public String getBolgeAdi() {
        return bolgeAdi;
    }

    public KaynakTuru getKaynakTuru() {
        return kaynakTuru;
    }

    public int getMiktar() {
        return miktar;
    }

    public LocalDateTime getTarih() {
        return tarih;
    }

    public String getStratejiAdi() {
        return stratejiAdi;
    }
}
