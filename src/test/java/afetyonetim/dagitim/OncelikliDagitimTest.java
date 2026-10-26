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

class OncelikliDagitimTest {

    private static final LocalDate BUGUN = LocalDate.of(2026, 10, 26);

    private final DagitimStratejisi strateji = new OncelikliDagitim(new OncelikHesaplayici(BUGUN));
    private final AfetBolgesi bolge = new AfetBolgesi("B1", "Merkez", "Hatay", 10000, 4);

    @Test
    void stokYetmezseEnAcilTalepOnceTamamenKarsilanir() {
        YardimTalebi dusuk = new YardimTalebi("T1", bolge, KaynakTuru.SU, 100, Aciliyet.DUSUK, BUGUN);
        YardimTalebi kritik = new YardimTalebi("T2", bolge, KaynakTuru.SU, 100, Aciliyet.KRITIK, BUGUN);

        Map<YardimTalebi, Integer> sonuc = strateji.dagit(List.of(dusuk, kritik), 120);

        assertEquals(100, sonuc.get(kritik));
        assertEquals(20, sonuc.get(dusuk));
    }

    @Test
    void stokYeterliyseHerkesIstedigiKadarAlir() {
        YardimTalebi t1 = new YardimTalebi("T1", bolge, KaynakTuru.GIDA, 30, Aciliyet.ORTA, BUGUN);
        YardimTalebi t2 = new YardimTalebi("T2", bolge, KaynakTuru.GIDA, 50, Aciliyet.YUKSEK, BUGUN);

        Map<YardimTalebi, Integer> sonuc = strateji.dagit(List.of(t1, t2), 1000);

        assertEquals(30, sonuc.get(t1));
        assertEquals(50, sonuc.get(t2));
    }

    @Test
    void tamamlanmisTalebeTekrarDagitimYapilmaz() {
        YardimTalebi tamamlanan = new YardimTalebi("T1", bolge, KaynakTuru.SU, 50, Aciliyet.KRITIK, BUGUN);
        tamamlanan.karsila(50);
        YardimTalebi bekleyen = new YardimTalebi("T2", bolge, KaynakTuru.SU, 80, Aciliyet.DUSUK, BUGUN);

        Map<YardimTalebi, Integer> sonuc = strateji.dagit(List.of(tamamlanan, bekleyen), 500);

        assertFalse(sonuc.containsKey(tamamlanan));
        assertEquals(80, sonuc.get(bekleyen));
    }
}
