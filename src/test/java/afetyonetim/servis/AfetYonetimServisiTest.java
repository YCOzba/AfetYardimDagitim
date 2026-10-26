package afetyonetim.servis;

import afetyonetim.dagitim.OncelikHesaplayici;
import afetyonetim.dagitim.OncelikliDagitim;
import afetyonetim.depo.Depo;
import afetyonetim.model.Aciliyet;
import afetyonetim.model.AfetBolgesi;
import afetyonetim.model.DagitimKaydi;
import afetyonetim.model.KaynakTuru;
import afetyonetim.model.YardimTalebi;
import afetyonetim.veri.BolgeRepository;
import afetyonetim.veri.DagitimRepository;
import afetyonetim.veri.KaynakRepository;
import afetyonetim.veri.TalepRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AfetYonetimServisiTest {

    @TempDir
    Path klasor;

    private AfetYonetimServisi servisOlustur() {
        BolgeRepository bolgeRepo = new BolgeRepository(klasor.resolve("bolgeler.txt"));
        return new AfetYonetimServisi(new Depo(),
                new KaynakRepository(klasor.resolve("kaynaklar.txt")),
                bolgeRepo,
                new TalepRepository(klasor.resolve("talepler.txt"), bolgeRepo),
                new DagitimRepository(klasor.resolve("dagitimlar.txt")));
    }

    @Test
    void dagitimSonrasiStokAzalirVeTalepKarsilanir() {
        AfetYonetimServisi servis = servisOlustur();
        servis.kaynakEkle(KaynakTuru.SU, "Pet şişe su", 1000, "2099-01-01");
        AfetBolgesi bolge = servis.bolgeEkle("Antakya", "Hatay", 20000, 5);
        YardimTalebi talep = servis.talepOlustur(bolge.getId(), KaynakTuru.SU, 600, Aciliyet.KRITIK);

        List<DagitimKaydi> kayitlar = servis.dagitimYap(new OncelikliDagitim(new OncelikHesaplayici()));

        assertEquals(1, kayitlar.size());
        assertEquals(600, kayitlar.get(0).getMiktar());
        assertEquals(400, servis.kullanilabilirStok(KaynakTuru.SU));
        assertTrue(talep.tamamlandiMi());
    }

    @Test
    void programYenidenAcilincaVerilerKorunur() {
        AfetYonetimServisi servis = servisOlustur();
        servis.kaynakEkle(KaynakTuru.GIDA, "Kuru gıda kolisi", 300, "2099-01-01");
        AfetBolgesi bolge = servis.bolgeEkle("Nurdağı", "Gaziantep", 9000, 3);
        servis.talepOlustur(bolge.getId(), KaynakTuru.GIDA, 500, Aciliyet.YUKSEK);
        servis.dagitimYap(new OncelikliDagitim(new OncelikHesaplayici()));

        // aynı klasörden yeni servis = programı kapatıp açmak gibi
        AfetYonetimServisi yeniServis = servisOlustur();

        assertEquals(0, yeniServis.kullanilabilirStok(KaynakTuru.GIDA));
        assertEquals(1, yeniServis.getBolgeler().size());
        assertEquals(200, yeniServis.bekleyenIhtiyac(KaynakTuru.GIDA));
        assertEquals(1, yeniServis.getDagitimlar().size());
    }

    @Test
    void ayniKaynakTekrarEklenirseYeniKayitAcilmazStokArtar() {
        AfetYonetimServisi servis = servisOlustur();
        servis.kaynakEkle(KaynakTuru.BARINMA, "Battaniye", 100, "1");
        servis.kaynakEkle(KaynakTuru.BARINMA, "battaniye", 50, "1");

        assertEquals(1, servis.getKaynaklar().size());
        assertEquals(150, servis.kullanilabilirStok(KaynakTuru.BARINMA));
    }
}
