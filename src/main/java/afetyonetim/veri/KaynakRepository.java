package afetyonetim.veri;

import afetyonetim.model.Kaynak;
import afetyonetim.model.KaynakFactory;
import afetyonetim.model.KaynakTuru;

import java.nio.file.Path;

public class KaynakRepository extends DosyaRepository<Kaynak> {

    public KaynakRepository(Path dosya) {
        super(dosya);
    }

    @Override
    protected Kaynak satirdanOku(String[] alanlar) {
        KaynakTuru tur = KaynakTuru.valueOf(alanlar[0]);
        return KaynakFactory.olustur(tur, alanlar[1], alanlar[2], Integer.parseInt(alanlar[3]), alanlar[4]);
    }

    @Override
    protected String satiraCevir(Kaynak k) {
        return String.join(AYRAC, k.getTur().name(), k.getId(), temizle(k.getAd()),
                String.valueOf(k.getMiktar()), k.getEkBilgi());
    }

    @Override
    protected String baslik() {
        return "tur;id;ad;miktar;ekBilgi";
    }
}
