package afetyonetim.model;

import java.time.LocalDate;

public class Ilac extends TarihliKaynak {

    // insülin, aşı gibi ilaçlar soğukta taşınmalı
    private final boolean sogukZincir;

    public Ilac(String id, String ad, int miktar, LocalDate sonKullanmaTarihi, boolean sogukZincir) {
        super(id, ad, miktar, sonKullanmaTarihi);
        this.sogukZincir = sogukZincir;
    }

    @Override
    public KaynakTuru getTur() {
        return KaynakTuru.ILAC;
    }

    @Override
    public String getBirim() {
        return "kutu";
    }

    @Override
    public String getEkBilgi() {
        return super.getEkBilgi() + "," + sogukZincir;
    }

    @Override
    public String getAciklama() {
        if (sogukZincir) {
            return super.getAciklama() + ", soğuk zincir";
        }
        return super.getAciklama();
    }

    public boolean isSogukZincir() {
        return sogukZincir;
    }
}
