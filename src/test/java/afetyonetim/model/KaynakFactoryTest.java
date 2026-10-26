package afetyonetim.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KaynakFactoryTest {

    @Test
    void tureGoreDogruAltSinifOlusturulur() {
        assertInstanceOf(Gida.class, KaynakFactory.olustur(KaynakTuru.GIDA, "K1", "Kuru gıda", 10, "2027-01-01"));
        assertInstanceOf(Su.class, KaynakFactory.olustur(KaynakTuru.SU, "K2", "Su", 10, "2027-01-01"));
        assertInstanceOf(BarinmaMalzemesi.class, KaynakFactory.olustur(KaynakTuru.BARINMA, "K3", "Çadır", 10, "4"));
    }

    @Test
    void ilacinSogukZincirBilgisiOkunur() {
        Kaynak kaynak = KaynakFactory.olustur(KaynakTuru.ILAC, "K4", "İnsülin", 5, "2027-01-30,true");

        Ilac ilac = assertInstanceOf(Ilac.class, kaynak);
        assertTrue(ilac.isSogukZincir());
        assertEquals("2027-01-30,true", ilac.getEkBilgi()); // dosyaya aynı şekilde yazılmalı
    }

    @Test
    void hataliTarihVerilirseKaynakOlusmaz() {
        assertThrows(RuntimeException.class,
                () -> KaynakFactory.olustur(KaynakTuru.GIDA, "K1", "Konserve", 10, "31-12-2027"));
    }
}
