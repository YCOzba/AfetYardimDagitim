package afetyonetim.model;

import java.time.LocalDate;

public class Su extends TarihliKaynak {

    public Su(String id, String ad, int miktar, LocalDate sonKullanmaTarihi) {
        super(id, ad, miktar, sonKullanmaTarihi);
    }

    @Override
    public KaynakTuru getTur() {
        return KaynakTuru.SU;
    }

    @Override
    public String getBirim() {
        return "litre";
    }
}
