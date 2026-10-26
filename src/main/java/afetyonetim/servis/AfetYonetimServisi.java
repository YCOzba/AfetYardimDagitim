package afetyonetim.servis;

import afetyonetim.dagitim.DagitimStratejisi;
import afetyonetim.depo.Depo;
import afetyonetim.model.Aciliyet;
import afetyonetim.model.AfetBolgesi;
import afetyonetim.model.DagitimKaydi;
import afetyonetim.model.Kaynak;
import afetyonetim.model.KaynakFactory;
import afetyonetim.model.KaynakTuru;
import afetyonetim.model.TarihliKaynak;
import afetyonetim.model.YardimTalebi;
import afetyonetim.veri.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Menü ile alt katmanlar arasındaki sınıf. Kaynak/bölge/talep işlemlerini ve dağıtımı yönetir,
 * her değişiklikten sonra verileri dosyaya kaydeder.
 */
public class AfetYonetimServisi {

    private final Depo depo;
    private final Repository<Kaynak> kaynakRepository;
    private final Repository<AfetBolgesi> bolgeRepository;
    private final Repository<YardimTalebi> talepRepository;
    private final Repository<DagitimKaydi> dagitimRepository;

    private final List<AfetBolgesi> bolgeler;
    private final List<YardimTalebi> talepler;
    private final List<DagitimKaydi> dagitimlar;

    public AfetYonetimServisi(Depo depo,
                              Repository<Kaynak> kaynakRepository,
                              Repository<AfetBolgesi> bolgeRepository,
                              Repository<YardimTalebi> talepRepository,
                              Repository<DagitimKaydi> dagitimRepository) {
        this.depo = depo;
        this.kaynakRepository = kaynakRepository;
        this.bolgeRepository = bolgeRepository;
        this.talepRepository = talepRepository;
        this.dagitimRepository = dagitimRepository;

        // program açılırken kayıtlı veriler yükleniyor
        for (Kaynak k : kaynakRepository.hepsiniYukle()) {
            depo.kaynakEkle(k);
        }
        this.bolgeler = bolgeRepository.hepsiniYukle();
        this.talepler = talepRepository.hepsiniYukle();
        this.dagitimlar = dagitimRepository.hepsiniYukle();
    }

    // ---------------- kaynak işlemleri ----------------

    public Kaynak kaynakEkle(KaynakTuru tur, String ad, int miktar, String ekBilgi) {
        List<String> idler = new ArrayList<>();
        for (Kaynak k : depo.getKaynaklar()) {
            idler.add(k.getId());
        }
        Kaynak yeni = KaynakFactory.olustur(tur, yeniId("K", idler), ad, miktar, ekBilgi);

        // aynı malzeme (aynı ad ve skt/kapasite) zaten varsa yeni kayıt açmak yerine stoğu artır
        for (Kaynak mevcut : depo.getKaynaklar()) {
            if (mevcut.getTur() == tur && mevcut.getAd().equalsIgnoreCase(yeni.getAd())
                    && mevcut.getEkBilgi().equals(yeni.getEkBilgi())) {
                mevcut.stokEkle(miktar);
                kaynakRepository.hepsiniKaydet(depo.getKaynaklar());
                return mevcut;
            }
        }

        depo.kaynakEkle(yeni);
        kaynakRepository.hepsiniKaydet(depo.getKaynaklar());
        return yeni;
    }

    public List<Kaynak> getKaynaklar() {
        return depo.getKaynaklar();
    }

    public int kullanilabilirStok(KaynakTuru tur) {
        return depo.kullanilabilirStok(tur, LocalDate.now());
    }

    public List<TarihliKaynak> sktYaklasanlar(int gun) {
        return depo.sktYaklasanlar(LocalDate.now(), gun);
    }

    public List<Kaynak> tarihiGecenler() {
        return depo.tarihiGecenler(LocalDate.now());
    }

    // ---------------- bölge işlemleri ----------------

    public AfetBolgesi bolgeEkle(String ad, String il, int etkilenenKisi, int hasarSeviyesi) {
        List<String> idler = new ArrayList<>();
        for (AfetBolgesi b : bolgeler) {
            idler.add(b.getId());
        }
        AfetBolgesi bolge = new AfetBolgesi(yeniId("B", idler), ad, il, etkilenenKisi, hasarSeviyesi);
        bolgeler.add(bolge);
        bolgeRepository.hepsiniKaydet(bolgeler);
        return bolge;
    }

    public void bolgeGuncelle(String bolgeId, int etkilenenKisi, int hasarSeviyesi) {
        AfetBolgesi bolge = bolgeBul(bolgeId);
        bolge.setEtkilenenKisi(etkilenenKisi);
        bolge.setHasarSeviyesi(hasarSeviyesi);
        bolgeRepository.hepsiniKaydet(bolgeler);
    }

    public AfetBolgesi bolgeBul(String bolgeId) {
        for (AfetBolgesi b : bolgeler) {
            if (b.getId().equalsIgnoreCase(bolgeId.trim())) {
                return b;
            }
        }
        throw new IllegalArgumentException(bolgeId + " numaralı bölge bulunamadı");
    }

    public List<AfetBolgesi> getBolgeler() {
        return Collections.unmodifiableList(bolgeler);
    }

    // ---------------- talep işlemleri ----------------

    public YardimTalebi talepOlustur(String bolgeId, KaynakTuru tur, int miktar, Aciliyet aciliyet) {
        AfetBolgesi bolge = bolgeBul(bolgeId);
        List<String> idler = new ArrayList<>();
        for (YardimTalebi t : talepler) {
            idler.add(t.getId());
        }
        YardimTalebi talep = new YardimTalebi(yeniId("T", idler), bolge, tur, miktar, aciliyet, LocalDate.now());
        talepler.add(talep);
        talepRepository.hepsiniKaydet(talepler);
        return talep;
    }

    public List<YardimTalebi> bekleyenTalepler() {
        List<YardimTalebi> sonuc = new ArrayList<>();
        for (YardimTalebi t : talepler) {
            if (!t.tamamlandiMi()) {
                sonuc.add(t);
            }
        }
        return sonuc;
    }

    public int bekleyenIhtiyac(KaynakTuru tur) {
        int toplam = 0;
        for (YardimTalebi t : talepler) {
            if (t.getKaynakTuru() == tur) {
                toplam += t.kalanIhtiyac();
            }
        }
        return toplam;
    }

    public List<YardimTalebi> getTalepler() {
        return Collections.unmodifiableList(talepler);
    }

    // ---------------- dağıtım ----------------

    /**
     * Her kaynak türü için bekleyen talepleri ve depodaki stoğu seçilen stratejiye verir,
     * çıkan sonuca göre depodan düşer ve talepleri günceller.
     */
    public List<DagitimKaydi> dagitimYap(DagitimStratejisi strateji) {
        LocalDate bugun = LocalDate.now();
        LocalDateTime simdi = LocalDateTime.now().withNano(0);
        List<DagitimKaydi> yeniKayitlar = new ArrayList<>();

        for (KaynakTuru tur : KaynakTuru.values()) {
            List<YardimTalebi> bekleyenler = new ArrayList<>();
            for (YardimTalebi t : talepler) {
                if (t.getKaynakTuru() == tur && !t.tamamlandiMi()) {
                    bekleyenler.add(t);
                }
            }
            int stok = depo.kullanilabilirStok(tur, bugun);
            if (bekleyenler.isEmpty() || stok == 0) {
                continue;
            }

            Map<YardimTalebi, Integer> plan = strateji.dagit(bekleyenler, stok);
            int toplam = 0;
            for (int miktar : plan.values()) {
                toplam += miktar;
            }
            if (toplam == 0) {
                continue;
            }

            // önce depodan tek seferde düşülüyor, stok yetmezse burada hata çıkar ve talepler değişmez
            depo.stokCek(tur, toplam, bugun);
            for (Map.Entry<YardimTalebi, Integer> e : plan.entrySet()) {
                if (e.getValue() <= 0) {
                    continue;
                }
                YardimTalebi talep = e.getKey();
                talep.karsila(e.getValue());
                yeniKayitlar.add(new DagitimKaydi(talep.getId(), talep.getBolge().getAd(), tur,
                        e.getValue(), simdi, strateji.getAd()));
            }
        }

        dagitimlar.addAll(yeniKayitlar);
        hepsiniKaydet();
        return yeniKayitlar;
    }

    public List<DagitimKaydi> getDagitimlar() {
        return Collections.unmodifiableList(dagitimlar);
    }

    public void hepsiniKaydet() {
        kaynakRepository.hepsiniKaydet(depo.getKaynaklar());
        bolgeRepository.hepsiniKaydet(bolgeler);
        talepRepository.hepsiniKaydet(talepler);
        dagitimRepository.hepsiniKaydet(dagitimlar);
    }

    // K1, K2 ... şeklinde sıradaki numarayı verir
    private static String yeniId(String onEk, List<String> mevcutIdler) {
        int enBuyuk = 0;
        for (String id : mevcutIdler) {
            if (id.startsWith(onEk)) {
                try {
                    enBuyuk = Math.max(enBuyuk, Integer.parseInt(id.substring(onEk.length())));
                } catch (NumberFormatException e) {
                    // farklı formatta id varsa atla
                }
            }
        }
        return onEk + (enBuyuk + 1);
    }
}
