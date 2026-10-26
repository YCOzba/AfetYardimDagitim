package afetyonetim.dagitim;

import afetyonetim.model.Aciliyet;
import afetyonetim.model.AfetBolgesi;
import afetyonetim.model.KaynakTuru;
import afetyonetim.model.YardimTalebi;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OncelikHesaplayiciTest {

    private static final LocalDate BUGUN = LocalDate.of(2026, 10, 26);

    private final OncelikHesaplayici hesaplayici = new OncelikHesaplayici(BUGUN);
    private final AfetBolgesi bolge = new AfetBolgesi("B1", "Merkez", "Hatay", 5000, 3);

    @Test
    void puanFormuleGoreHesaplanir() {
        AfetBolgesi agirHasarli = new AfetBolgesi("B2", "Antakya", "Hatay", 48000, 5);
        YardimTalebi talep = new YardimTalebi("T1", agirHasarli, KaynakTuru.SU, 100, Aciliyet.KRITIK, BUGUN.minusDays(2));

        // (40 + 5*6 + 20 (nüfus sınırı) + 2*2) * 1.4 = 131.6
        assertEquals(131.6, hesaplayici.puanHesapla(talep), 0.0001);
    }

    @Test
    void kritikTalepDusukTaleptenOnceSiralanir() {
        YardimTalebi dusuk = new YardimTalebi("T1", bolge, KaynakTuru.GIDA, 100, Aciliyet.DUSUK, BUGUN);
        YardimTalebi kritik = new YardimTalebi("T2", bolge, KaynakTuru.GIDA, 100, Aciliyet.KRITIK, BUGUN);

        List<YardimTalebi> sirali = hesaplayici.sirala(List.of(dusuk, kritik));

        assertEquals("T2", sirali.get(0).getId());
    }

    @Test
    void hasarSeviyesiYuksekBolgeninTalebiDahaYuksekPuanAlir() {
        AfetBolgesi hafif = new AfetBolgesi("B2", "Köy", "Malatya", 5000, 1);
        AfetBolgesi agir = new AfetBolgesi("B3", "Merkez", "Malatya", 5000, 5);
        YardimTalebi t1 = new YardimTalebi("T1", hafif, KaynakTuru.ILAC, 50, Aciliyet.ORTA, BUGUN);
        YardimTalebi t2 = new YardimTalebi("T2", agir, KaynakTuru.ILAC, 50, Aciliyet.ORTA, BUGUN);

        assertTrue(hesaplayici.puanHesapla(t2) > hesaplayici.puanHesapla(t1));
    }

    @Test
    void beklemePuaniSiniriAsmaz() {
        YardimTalebi besGun = new YardimTalebi("T1", bolge, KaynakTuru.SU, 100, Aciliyet.ORTA, BUGUN.minusDays(5));
        YardimTalebi otuzGun = new YardimTalebi("T2", bolge, KaynakTuru.SU, 100, Aciliyet.ORTA, BUGUN.minusDays(30));

        // ikisi de en fazla 10 bekleme puanı almalı
        assertEquals(hesaplayici.puanHesapla(besGun), hesaplayici.puanHesapla(otuzGun), 0.0001);
    }
}
