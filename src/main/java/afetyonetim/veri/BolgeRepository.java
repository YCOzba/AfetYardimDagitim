package afetyonetim.veri;

import afetyonetim.model.AfetBolgesi;

import java.nio.file.Path;

public class BolgeRepository extends DosyaRepository<AfetBolgesi> {

    public BolgeRepository(Path dosya) {
        super(dosya);
    }

    @Override
    protected AfetBolgesi satirdanOku(String[] alanlar) {
        return new AfetBolgesi(alanlar[0], alanlar[1], alanlar[2],
                Integer.parseInt(alanlar[3]), Integer.parseInt(alanlar[4]));
    }

    @Override
    protected String satiraCevir(AfetBolgesi b) {
        return String.join(AYRAC, b.getId(), temizle(b.getAd()), temizle(b.getIl()),
                String.valueOf(b.getEtkilenenKisi()), String.valueOf(b.getHasarSeviyesi()));
    }

    @Override
    protected String baslik() {
        return "id;ad;il;etkilenenKisi;hasarSeviyesi";
    }
}
