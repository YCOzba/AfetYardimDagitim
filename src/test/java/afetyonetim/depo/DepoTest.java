package afetyonetim.depo;

import afetyonetim.model.Gida;
import afetyonetim.model.Ilac;
import afetyonetim.model.KaynakTuru;
import afetyonetim.model.YetersizStokException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DepoTest {

    private static final LocalDate BUGUN = LocalDate.of(2026, 10, 26);

    private Depo depo;

    @BeforeEach
    void hazirla() {
        depo = new Depo();
    }

    @Test
    void sktYakinOlanKaynakOnceKullanilir() {
        Gida uzak = new Gida("K1", "Konserve", 50, LocalDate.of(2027, 6, 1));
        Gida yakin = new Gida("K2", "Hazır yemek", 50, LocalDate.of(2026, 11, 15));
        depo.kaynakEkle(uzak);
        depo.kaynakEkle(yakin);

        depo.stokCek(KaynakTuru.GIDA, 60, BUGUN);

        assertEquals(0, yakin.getMiktar());
        assertEquals(40, uzak.getMiktar());
        assertFalse(depo.getKaynaklar().contains(yakin)); // biten kalem depodan çıkmalı
    }

    @Test
    void tarihiGecmisKaynakStogaSayilmaz() {
        depo.kaynakEkle(new Gida("K1", "Süt", 30, LocalDate.of(2026, 10, 20)));
        depo.kaynakEkle(new Gida("K2", "Konserve", 70, LocalDate.of(2027, 1, 1)));

        assertEquals(70, depo.kullanilabilirStok(KaynakTuru.GIDA, BUGUN));
        assertEquals(1, depo.tarihiGecenler(BUGUN).size());
    }

    @Test
    void yetersizStoktaHataVerilirVeStokDegismez() {
        Gida gida = new Gida("K1", "Konserve", 40, LocalDate.of(2027, 1, 1));
        depo.kaynakEkle(gida);

        assertThrows(YetersizStokException.class, () -> depo.stokCek(KaynakTuru.GIDA, 41, BUGUN));
        assertEquals(40, gida.getMiktar());
    }

    @Test
    void stokKritikSeviyeninAltinaDusunceDinleyiciHaberdarEdilir() {
        List<String> uyarilar = new ArrayList<>();
        depo.dinleyiciEkle((tur, kalan) -> uyarilar.add(tur + ":" + kalan));
        depo.kaynakEkle(new Ilac("K1", "Ağrı kesici", 100, LocalDate.of(2027, 1, 1), false));

        depo.stokCek(KaynakTuru.ILAC, 50, BUGUN); // kalan 50, ilaç için kritik seviye 30
        assertTrue(uyarilar.isEmpty());

        depo.stokCek(KaynakTuru.ILAC, 30, BUGUN); // kalan 20
        assertEquals(List.of("ILAC:20"), uyarilar);
    }
}
