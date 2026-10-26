package afetyonetim.dagitim;

import afetyonetim.model.Aciliyet;
import afetyonetim.model.AfetBolgesi;
import afetyonetim.model.KaynakTuru;
import afetyonetim.model.YardimTalebi;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OransalDagitimTest {

    private static final LocalDate BUGUN = LocalDate.of(2026, 10, 26);

    private final DagitimStratejisi strateji = new OransalDagitim(new OncelikHesaplayici(BUGUN));
    private final AfetBolgesi bolge = new AfetBolgesi("B1", "Merkez", "Hatay", 1000, 3);

    @Test
    void stokunTamamiDagitilirAmaKimseyeFazlaVerilmez() {
        YardimTalebi t1 = new YardimTalebi("T1", bolge, KaynakTuru.SU, 300, Aciliyet.KRITIK, BUGUN);
        YardimTalebi t2 = new YardimTalebi("T2", bolge, KaynakTuru.SU, 200, Aciliyet.ORTA, BUGUN);
        YardimTalebi t3 = new YardimTalebi("T3", bolge, KaynakTuru.SU, 100, Aciliyet.DUSUK, BUGUN);

        Map<YardimTalebi, Integer> sonuc = strateji.dagit(List.of(t1, t2, t3), 250);

        int toplam = sonuc.values().stream().mapToInt(Integer::intValue).sum();
        assertEquals(250, toplam);
        assertTrue(sonuc.get(t1) <= 300);
        assertTrue(sonuc.get(t2) <= 200);
        assertTrue(sonuc.get(t3) <= 100);
    }

    @Test
    void yuksekOncelikliTalepDahaBuyukPayAlir() {
        YardimTalebi kritik = new YardimTalebi("T1", bolge, KaynakTuru.GIDA, 100, Aciliyet.KRITIK, BUGUN);
        YardimTalebi dusuk = new YardimTalebi("T2", bolge, KaynakTuru.GIDA, 100, Aciliyet.DUSUK, BUGUN);

        Map<YardimTalebi, Integer> sonuc = strateji.dagit(List.of(dusuk, kritik), 100);

        assertTrue(sonuc.get(kritik) > sonuc.get(dusuk));
        assertTrue(sonuc.get(dusuk) > 0); // greedy'den farkı: düşük öncelikli de bir şey alıyor
    }

    @Test
    void ihtiyaciDolanTalepteArtanStokDigerineAktarilir() {
        YardimTalebi kucuk = new YardimTalebi("T1", bolge, KaynakTuru.ILAC, 10, Aciliyet.KRITIK, BUGUN);
        YardimTalebi buyuk = new YardimTalebi("T2", bolge, KaynakTuru.ILAC, 20, Aciliyet.DUSUK, BUGUN);

        Map<YardimTalebi, Integer> sonuc = strateji.dagit(List.of(kucuk, buyuk), 25);

        assertEquals(10, sonuc.get(kucuk));
        assertEquals(15, sonuc.get(buyuk));
    }
}
