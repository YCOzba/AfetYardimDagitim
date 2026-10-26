package afetyonetim.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class YardimTalebiTest {

    private YardimTalebi talep;

    @BeforeEach
    void hazirla() {
        AfetBolgesi bolge = new AfetBolgesi("B1", "Merkez", "Hatay", 1000, 3);
        talep = new YardimTalebi("T1", bolge, KaynakTuru.SU, 100, Aciliyet.YUKSEK, LocalDate.of(2026, 10, 20));
    }

    @Test
    void yeniTalepBekliyorDurumundadir() {
        assertEquals(TalepDurumu.BEKLIYOR, talep.getDurum());
        assertEquals(100, talep.kalanIhtiyac());
    }

    @Test
    void kismenKarsilananTalebinDurumuDegisir() {
        talep.karsila(40);

        assertEquals(TalepDurumu.KISMEN_KARSILANDI, talep.getDurum());
        assertEquals(60, talep.kalanIhtiyac());
    }

    @Test
    void tamamenKarsilananTalepTamamlanir() {
        talep.karsila(40);
        talep.karsila(60);

        assertTrue(talep.tamamlandiMi());
        assertEquals(TalepDurumu.KARSILANDI, talep.getDurum());
    }

    @Test
    void ihtiyactanFazlaYardimVerilemez() {
        assertThrows(IllegalArgumentException.class, () -> talep.karsila(101));
        assertEquals(0, talep.getKarsilananMiktar());
    }

    @Test
    void hasarSeviyesiBirIleBesArasindaOlmali() {
        assertThrows(IllegalArgumentException.class, () -> new AfetBolgesi("B2", "Köy", "Malatya", 100, 6));
        assertThrows(IllegalArgumentException.class, () -> new AfetBolgesi("B2", "Köy", "Malatya", 100, 0));
    }
}
