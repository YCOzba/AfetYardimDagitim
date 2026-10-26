package afetyonetim;

import afetyonetim.arayuz.KonsolMenu;
import afetyonetim.dagitim.DagitimStratejisi;
import afetyonetim.dagitim.OncelikHesaplayici;
import afetyonetim.dagitim.OncelikliDagitim;
import afetyonetim.dagitim.OransalDagitim;
import afetyonetim.depo.Depo;
import afetyonetim.depo.DosyaStokUyarisi;
import afetyonetim.depo.KonsolStokUyarisi;
import afetyonetim.model.AfetBolgesi;
import afetyonetim.model.DagitimKaydi;
import afetyonetim.model.Kaynak;
import afetyonetim.model.YardimTalebi;
import afetyonetim.servis.AfetYonetimServisi;
import afetyonetim.veri.BolgeRepository;
import afetyonetim.veri.DagitimRepository;
import afetyonetim.veri.KaynakRepository;
import afetyonetim.veri.Repository;
import afetyonetim.veri.TalepRepository;

import java.nio.file.Path;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Path veriKlasoru = Path.of("veri");

        // nesneler burada oluşturulup bağımlılıklar dışarıdan veriliyor
        Repository<Kaynak> kaynakRepository = new KaynakRepository(veriKlasoru.resolve("kaynaklar.txt"));
        Repository<AfetBolgesi> bolgeRepository = new BolgeRepository(veriKlasoru.resolve("bolgeler.txt"));
        Repository<YardimTalebi> talepRepository = new TalepRepository(veriKlasoru.resolve("talepler.txt"), bolgeRepository);
        Repository<DagitimKaydi> dagitimRepository = new DagitimRepository(veriKlasoru.resolve("dagitimlar.txt"));

        Depo depo = new Depo();
        depo.dinleyiciEkle(new KonsolStokUyarisi());
        depo.dinleyiciEkle(new DosyaStokUyarisi(veriKlasoru.resolve("uyarilar.log")));

        OncelikHesaplayici hesaplayici = new OncelikHesaplayici();
        List<DagitimStratejisi> stratejiler = List.of(
                new OncelikliDagitim(hesaplayici),
                new OransalDagitim(hesaplayici));

        AfetYonetimServisi servis = new AfetYonetimServisi(depo, kaynakRepository, bolgeRepository,
                talepRepository, dagitimRepository);

        new KonsolMenu(servis, hesaplayici, stratejiler).baslat();
    }
}
