package afetyonetim.veri;

import afetyonetim.model.Aciliyet;
import afetyonetim.model.AfetBolgesi;
import afetyonetim.model.BarinmaMalzemesi;
import afetyonetim.model.Ilac;
import afetyonetim.model.Kaynak;
import afetyonetim.model.KaynakTuru;
import afetyonetim.model.Su;
import afetyonetim.model.TalepDurumu;
import afetyonetim.model.YardimTalebi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DosyaRepositoryTest {

    @TempDir
    Path klasor;

    @Test
    void kaynaklarDosyayaYazilipGeriOkunabilir() {
        Path dosya = klasor.resolve("kaynaklar.txt");
        new KaynakRepository(dosya).hepsiniKaydet(List.of(
                new Su("K1", "Pet şişe su", 500, LocalDate.of(2027, 5, 1)),
                new Ilac("K2", "İnsülin", 20, LocalDate.of(2027, 1, 30), true),
                new BarinmaMalzemesi("K3", "Çadır", 40, 6)));

        // yeni bir repository ile okuyunca aynı bilgiler gelmeli
        List<Kaynak> okunan = new KaynakRepository(dosya).hepsiniYukle();

        assertEquals(3, okunan.size());
        assertEquals("Pet şişe su", okunan.get(0).getAd());
        Ilac ilac = assertInstanceOf(Ilac.class, okunan.get(1));
        assertTrue(ilac.isSogukZincir());
        assertEquals(KaynakTuru.BARINMA, okunan.get(2).getTur());
        assertEquals(40, okunan.get(2).getMiktar());
    }

    @Test
    void taleplerBolgeleriyleBirlikteOkunur() {
        Path bolgeDosyasi = klasor.resolve("bolgeler.txt");
        Path talepDosyasi = klasor.resolve("talepler.txt");
        AfetBolgesi bolge = new AfetBolgesi("B1", "Antakya", "Hatay", 48000, 5);
        BolgeRepository bolgeRepo = new BolgeRepository(bolgeDosyasi);
        bolgeRepo.hepsiniKaydet(List.of(bolge));
        new TalepRepository(talepDosyasi, bolgeRepo).hepsiniKaydet(List.of(
                new YardimTalebi("T1", bolge, KaynakTuru.SU, 500, Aciliyet.KRITIK, LocalDate.of(2026, 10, 24), 120)));

        BolgeRepository yeniBolgeRepo = new BolgeRepository(bolgeDosyasi);
        List<YardimTalebi> okunan = new TalepRepository(talepDosyasi, yeniBolgeRepo).hepsiniYukle();

        YardimTalebi talep = okunan.get(0);
        assertEquals(120, talep.getKarsilananMiktar());
        assertEquals(TalepDurumu.KISMEN_KARSILANDI, talep.getDurum());
        // talep ile bölge listesi aynı nesneyi göstermeli
        assertSame(yeniBolgeRepo.hepsiniYukle().get(0), talep.getBolge());
    }

    @Test
    void bozukSatirAtlanirDigerleriOkunur() throws IOException {
        Path dosya = klasor.resolve("bolgeler.txt");
        Files.writeString(dosya, """
                # id;ad;il;etkilenenKisi;hasarSeviyesi
                B1;Merkez;Hatay;1000;3
                bu satır bozuk
                B2;Nurdağı;Gaziantep;500;9
                B3;Doğanşehir;Malatya;700;2
                """, StandardCharsets.UTF_8);

        List<AfetBolgesi> bolgeler = new BolgeRepository(dosya).hepsiniYukle();

        assertEquals(2, bolgeler.size());
        assertEquals("B3", bolgeler.get(1).getId());
    }
}
