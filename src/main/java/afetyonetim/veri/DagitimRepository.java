package afetyonetim.veri;

import afetyonetim.model.DagitimKaydi;
import afetyonetim.model.KaynakTuru;

import java.nio.file.Path;
import java.time.LocalDateTime;

public class DagitimRepository extends DosyaRepository<DagitimKaydi> {

    public DagitimRepository(Path dosya) {
        super(dosya);
    }

    @Override
    protected DagitimKaydi satirdanOku(String[] alanlar) {
        return new DagitimKaydi(alanlar[0], alanlar[1], KaynakTuru.valueOf(alanlar[2]),
                Integer.parseInt(alanlar[3]), LocalDateTime.parse(alanlar[4]), alanlar[5]);
    }

    @Override
    protected String satiraCevir(DagitimKaydi d) {
        return String.join(AYRAC, d.getTalepId(), temizle(d.getBolgeAdi()), d.getKaynakTuru().name(),
                String.valueOf(d.getMiktar()), d.getTarih().toString(), d.getStratejiAdi());
    }

    @Override
    protected String baslik() {
        return "talepId;bolge;kaynakTuru;miktar;tarih;yontem";
    }
}
