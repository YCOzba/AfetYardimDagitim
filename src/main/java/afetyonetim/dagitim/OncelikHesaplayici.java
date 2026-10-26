package afetyonetim.dagitim;

import afetyonetim.model.AfetBolgesi;
import afetyonetim.model.YardimTalebi;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Talebin öncelik puanını hesaplar. Puan ne kadar yüksekse o kadar önce karşılanır.
 *
 * puan = (aciliyet puanı
 *         + hasar seviyesi * 6
 *         + etkilenen kişi / 1000   (en fazla 20)
 *         + bekleme günü * 2        (en fazla 10))
 *        * kaynak türü katsayısı
 */
public class OncelikHesaplayici {

    private static final int HASAR_CARPANI = 6;
    private static final double NUFUS_PUAN_SINIRI = 20;
    private static final int GUNLUK_BEKLEME_PUANI = 2;
    private static final int BEKLEME_PUAN_SINIRI = 10;

    private final LocalDate bugun;

    public OncelikHesaplayici() {
        this(LocalDate.now());
    }

    // testlerde tarihi sabitlemek için
    public OncelikHesaplayici(LocalDate bugun) {
        this.bugun = bugun;
    }

    public double puanHesapla(YardimTalebi talep) {
        AfetBolgesi bolge = talep.getBolge();

        double puan = talep.getAciliyet().getPuan();
        puan += bolge.getHasarSeviyesi() * HASAR_CARPANI;
        puan += Math.min(bolge.getEtkilenenKisi() / 1000.0, NUFUS_PUAN_SINIRI);

        // uzun süredir bekleyen talepler hep geride kalmasın diye bekleme puanı ekleniyor
        long beklenenGun = ChronoUnit.DAYS.between(talep.getTalepTarihi(), bugun);
        if (beklenenGun > 0) {
            puan += Math.min(beklenenGun * GUNLUK_BEKLEME_PUANI, BEKLEME_PUAN_SINIRI);
        }

        return puan * talep.getKaynakTuru().getOncelikKatsayisi();
    }

    // yüksek puan önce, puanlar eşitse eski talep önce
    public Comparator<YardimTalebi> karsilastirici() {
        Comparator<YardimTalebi> puanaGore = Comparator.comparingDouble(this::puanHesapla);
        return puanaGore.reversed().thenComparing(YardimTalebi::getTalepTarihi);
    }

    public List<YardimTalebi> sirala(List<YardimTalebi> talepler) {
        List<YardimTalebi> sirali = new ArrayList<>(talepler);
        sirali.sort(karsilastirici());
        return sirali;
    }
}
