package afetyonetim.model;

import java.time.LocalDate;

public class Gida extends TarihliKaynak {

    public Gida(String id, String ad, int miktar, LocalDate sonKullanmaTarihi) {
        super(id, ad, miktar, sonKullanmaTarihi);
    }

    @Override
    public KaynakTuru getTur() {
        return KaynakTuru.GIDA;
    }

    @Override
    public String getBirim() {
        return "koli";
    }
}
