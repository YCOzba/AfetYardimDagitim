package afetyonetim.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class KaynakTest {

    @Test
    void mevcuttanFazlaStokDusulemez() {
        Gida gida = new Gida("K1", "Konserve", 10, LocalDate.of(2099, 1, 1));

        assertThrows(YetersizStokException.class, () -> gida.stokDus(11));
        assertEquals(10, gida.getMiktar()); // hata olunca stok değişmemeli
    }

    @Test
    void stokEkleyipDusunceMiktarDogruHesaplanir() {
        Su su = new Su("K1", "Pet şişe su", 100, LocalDate.of(2099, 1, 1));
        su.stokEkle(50);
        su.stokDus(30);

        assertEquals(120, su.getMiktar());
    }

    @Test
    void negatifMiktarlaKaynakOlusturulamaz() {
        assertThrows(IllegalArgumentException.class,
                () -> new Su("K1", "Su", -5, LocalDate.of(2099, 1, 1)));
    }

    @Test
    void tarihiGecmisGidaKullanilamaz() {
        Gida gida = new Gida("K1", "Ekmek", 50, LocalDate.of(2026, 10, 1));

        assertTrue(gida.kullanilabilirMi(LocalDate.of(2026, 10, 1))); // son gün hala kullanılabilir
        assertFalse(gida.kullanilabilirMi(LocalDate.of(2026, 10, 2)));
    }

    @Test
    void barinmaMalzemesininTarihSiniriYoktur() {
        Kaynak cadir = new BarinmaMalzemesi("K1", "Çadır", 10, 5);

        assertTrue(cadir.kullanilabilirMi(LocalDate.of(2100, 1, 1)));
        assertEquals("5 kişilik", cadir.getAciklama());
    }
}
