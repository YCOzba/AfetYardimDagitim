package afetyonetim.veri;

import afetyonetim.model.Aciliyet;
import afetyonetim.model.AfetBolgesi;
import afetyonetim.model.KaynakTuru;
import afetyonetim.model.YardimTalebi;

import java.nio.file.Path;
import java.time.LocalDate;

public class TalepRepository extends DosyaRepository<YardimTalebi> {

    // dosyada sadece bölge id'si var, nesneyi bölge deposundan buluyoruz
    private final Repository<AfetBolgesi> bolgeRepository;

    public TalepRepository(Path dosya, Repository<AfetBolgesi> bolgeRepository) {
        super(dosya);
        this.bolgeRepository = bolgeRepository;
    }

    @Override
    protected YardimTalebi satirdanOku(String[] alanlar) {
        AfetBolgesi bolge = bolgeBul(alanlar[1]);
        return new YardimTalebi(alanlar[0], bolge, KaynakTuru.valueOf(alanlar[2]),
                Integer.parseInt(alanlar[3]), Aciliyet.valueOf(alanlar[5]),
                LocalDate.parse(alanlar[6]), Integer.parseInt(alanlar[4]));
    }

    @Override
    protected String satiraCevir(YardimTalebi t) {
        return String.join(AYRAC, t.getId(), t.getBolge().getId(), t.getKaynakTuru().name(),
                String.valueOf(t.getIstenenMiktar()), String.valueOf(t.getKarsilananMiktar()),
                t.getAciliyet().name(), t.getTalepTarihi().toString());
    }

    @Override
    protected String baslik() {
        return "id;bolgeId;kaynakTuru;istenen;karsilanan;aciliyet;tarih";
    }

    private AfetBolgesi bolgeBul(String bolgeId) {
        for (AfetBolgesi b : bolgeRepository.hepsiniYukle()) {
            if (b.getId().equals(bolgeId)) {
                return b;
            }
        }
        throw new IllegalStateException("Talebin bölgesi bulunamadı: " + bolgeId);
    }
}
