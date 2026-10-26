package afetyonetim.arayuz;

import afetyonetim.dagitim.DagitimStratejisi;
import afetyonetim.dagitim.OncelikHesaplayici;
import afetyonetim.model.Aciliyet;
import afetyonetim.model.AfetBolgesi;
import afetyonetim.model.DagitimKaydi;
import afetyonetim.model.Kaynak;
import afetyonetim.model.KaynakTuru;
import afetyonetim.model.TalepDurumu;
import afetyonetim.model.TarihliKaynak;
import afetyonetim.model.YardimTalebi;
import afetyonetim.servis.AfetYonetimServisi;
import afetyonetim.util.TarihFormat;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

// sadece kullanıcıdan veri alıp ekrana yazdırıyor, asıl işler serviste
public class KonsolMenu {

    private static final int SKT_UYARI_GUNU = 30;

    private final AfetYonetimServisi servis;
    private final OncelikHesaplayici hesaplayici;
    private final List<DagitimStratejisi> stratejiler;
    private final Scanner scanner = new Scanner(System.in);

    public KonsolMenu(AfetYonetimServisi servis, OncelikHesaplayici hesaplayici,
                      List<DagitimStratejisi> stratejiler) {
        this.servis = servis;
        this.hesaplayici = hesaplayici;
        this.stratejiler = stratejiler;
    }

    public void baslat() {
        System.out.println("==================================================");
        System.out.println("     AFET VE ACİL DURUM YARDIM DAĞITIM SİSTEMİ");
        System.out.println("==================================================");

        boolean devam = true;
        while (devam) {
            menuyuYazdir();
            int secim = sayiOku("Seçiminiz: ", 0, 10);
            System.out.println();
            try {
                switch (secim) {
                    case 1 -> kaynakEkle();
                    case 2 -> stokListele();
                    case 3 -> bolgeEkle();
                    case 4 -> bolgeleriListele();
                    case 5 -> bolgeGuncelle();
                    case 6 -> talepOlustur();
                    case 7 -> bekleyenTalepleriListele();
                    case 8 -> dagitimYap();
                    case 9 -> dagitimGecmisi();
                    case 10 -> genelRapor();
                    case 0 -> devam = false;
                    default -> System.out.println("Geçersiz seçim");
                }
            } catch (RuntimeException e) {
                System.out.println("Hata: " + e.getMessage());
            }
        }
        System.out.println("Programdan çıkılıyor. Veriler 'veri' klasörüne kaydedildi.");
    }

    private void menuyuYazdir() {
        System.out.println();
        System.out.println("------------------- ANA MENÜ -------------------");
        System.out.println(" 1- Depoya kaynak ekle (bağış / tedarik girişi)");
        System.out.println(" 2- Depo stok durumu");
        System.out.println(" 3- Afet bölgesi ekle");
        System.out.println(" 4- Afet bölgelerini listele");
        System.out.println(" 5- Bölge bilgilerini güncelle");
        System.out.println(" 6- Yardım talebi oluştur");
        System.out.println(" 7- Bekleyen talepler (öncelik sırasına göre)");
        System.out.println(" 8- Dağıtım yap");
        System.out.println(" 9- Dağıtım geçmişi");
        System.out.println("10- Genel durum raporu");
        System.out.println(" 0- Çıkış");
    }

    // ---------------- kaynak ----------------

    private void kaynakEkle() {
        KaynakTuru tur = turSec();
        String ad = metinOku("Kaynak adı: ");
        int miktar = sayiOku("Miktar: ", 1, 1_000_000);

        String ekBilgi = switch (tur) {
            case GIDA, SU -> sktOku().toString();
            case ILAC -> sktOku() + "," + evetMi("Soğuk zincir gerekiyor mu? (e/h): ");
            case BARINMA -> String.valueOf(sayiOku("Kaç kişilik? (battaniye gibi tek kişilik ise 1): ", 1, 50));
        };

        Kaynak kaynak = servis.kaynakEkle(tur, ad, miktar, ekBilgi);
        System.out.println("Kaydedildi -> " + kaynak + " | " + kaynak.getAciklama());
    }

    private void stokListele() {
        List<Kaynak> kaynaklar = new ArrayList<>(servis.getKaynaklar());
        if (kaynaklar.isEmpty()) {
            System.out.println("Depoda hiç kaynak yok.");
            return;
        }
        kaynaklar.sort(Comparator.comparing(Kaynak::getTur));

        System.out.printf("%-5s %-26s %-9s %13s   %s%n", "ID", "Ad", "Tür", "Miktar", "Bilgi");
        cizgiCek(75);
        for (Kaynak k : kaynaklar) {
            System.out.printf("%-5s %-26s %-9s %13s   %s%n", k.getId(), kisalt(k.getAd(), 26),
                    k.getTur().getGorunenAd(), k.getMiktar() + " " + k.getBirim(), k.getAciklama());
        }

        System.out.println();
        System.out.println("Dağıtılabilir toplam stok (tarihi geçenler hariç):");
        for (KaynakTuru tur : KaynakTuru.values()) {
            System.out.printf("  %-8s: %d%n", tur.getGorunenAd(), servis.kullanilabilirStok(tur));
        }
    }

    // ---------------- bölge ----------------

    private void bolgeEkle() {
        String ad = metinOku("Bölge adı (ilçe / mahalle): ");
        String il = metinOku("İl: ");
        int kisi = sayiOku("Etkilenen kişi sayısı: ", 0, 50_000_000);
        int hasar = sayiOku("Hasar seviyesi (1: hafif ... 5: çok ağır): ", 1, 5);

        AfetBolgesi bolge = servis.bolgeEkle(ad, il, kisi, hasar);
        System.out.println(bolge.getId() + " numarasıyla bölge eklendi.");
    }

    private void bolgeleriListele() {
        List<AfetBolgesi> bolgeler = servis.getBolgeler();
        if (bolgeler.isEmpty()) {
            System.out.println("Kayıtlı afet bölgesi yok.");
            return;
        }
        System.out.printf("%-5s %-16s %-22s %12s %7s%n", "ID", "İl", "Bölge", "Etkilenen", "Hasar");
        cizgiCek(66);
        for (AfetBolgesi b : bolgeler) {
            System.out.printf("%-5s %-16s %-22s %12d %5d/5%n", b.getId(), kisalt(b.getIl(), 16),
                    kisalt(b.getAd(), 22), b.getEtkilenenKisi(), b.getHasarSeviyesi());
        }
    }

    private void bolgeGuncelle() {
        bolgeleriListele();
        if (servis.getBolgeler().isEmpty()) {
            return;
        }
        AfetBolgesi bolge = servis.bolgeBul(metinOku("Güncellenecek bölgenin ID'si: "));
        System.out.println("Mevcut: " + bolge.getEtkilenenKisi() + " kişi, hasar " + bolge.getHasarSeviyesi());
        int kisi = sayiOku("Yeni etkilenen kişi sayısı: ", 0, 50_000_000);
        int hasar = sayiOku("Yeni hasar seviyesi (1-5): ", 1, 5);
        servis.bolgeGuncelle(bolge.getId(), kisi, hasar);
        System.out.println("Bölge bilgileri güncellendi. Bu bölgenin taleplerinin öncelik puanı da değişti.");
    }

    // ---------------- talep ----------------

    private void talepOlustur() {
        if (servis.getBolgeler().isEmpty()) {
            System.out.println("Önce afet bölgesi eklemelisiniz.");
            return;
        }
        bolgeleriListele();
        AfetBolgesi bolge = servis.bolgeBul(metinOku("Talep yapan bölgenin ID'si: "));
        KaynakTuru tur = turSec();
        int miktar = sayiOku("İhtiyaç miktarı: ", 1, 1_000_000);
        Aciliyet aciliyet = aciliyetSec();

        YardimTalebi talep = servis.talepOlustur(bolge.getId(), tur, miktar, aciliyet);
        System.out.printf("%s numaralı talep oluşturuldu. Öncelik puanı: %.1f%n",
                talep.getId(), hesaplayici.puanHesapla(talep));
    }

    private void bekleyenTalepleriListele() {
        List<YardimTalebi> sirali = hesaplayici.sirala(servis.bekleyenTalepler());
        if (sirali.isEmpty()) {
            System.out.println("Bekleyen talep yok, bütün talepler karşılanmış.");
            return;
        }
        System.out.printf("%-4s %-5s %-20s %-8s %-8s %9s %9s %7s  %s%n",
                "Sıra", "ID", "Bölge", "Tür", "Aciliyet", "İstenen", "Kalan", "Puan", "Durum");
        cizgiCek(95);
        int sira = 1;
        for (YardimTalebi t : sirali) {
            System.out.printf("%-4d %-5s %-20s %-8s %-8s %9d %9d %7.1f  %s%n", sira++, t.getId(),
                    kisalt(t.getBolge().getAd(), 20), t.getKaynakTuru().getGorunenAd(),
                    t.getAciliyet().getGorunenAd(), t.getIstenenMiktar(), t.kalanIhtiyac(),
                    hesaplayici.puanHesapla(t), t.getDurum().getGorunenAd());
        }
    }

    // ---------------- dağıtım ----------------

    private void dagitimYap() {
        if (servis.bekleyenTalepler().isEmpty()) {
            System.out.println("Bekleyen talep olmadığı için dağıtım yapılmadı.");
            return;
        }
        System.out.println("Dağıtım yöntemi:");
        for (int i = 0; i < stratejiler.size(); i++) {
            System.out.println("  " + (i + 1) + "- " + stratejiler.get(i).getAd()
                    + " (" + stratejiler.get(i).getAciklama() + ")");
        }
        DagitimStratejisi strateji = stratejiler.get(sayiOku("Seçim: ", 1, stratejiler.size()) - 1);

        List<DagitimKaydi> kayitlar = servis.dagitimYap(strateji);
        if (kayitlar.isEmpty()) {
            System.out.println("Bekleyen talepler için depoda uygun stok bulunamadı.");
        } else {
            System.out.println();
            System.out.println(kayitlar.size() + " gönderim yapıldı:");
            dagitimTablosu(kayitlar);
        }

        boolean eksikVar = false;
        for (KaynakTuru tur : KaynakTuru.values()) {
            int eksik = servis.bekleyenIhtiyac(tur);
            if (eksik > 0) {
                if (!eksikVar) {
                    System.out.println();
                    System.out.println("Henüz karşılanamayan ihtiyaçlar:");
                    eksikVar = true;
                }
                System.out.printf("  %-8s: %d%n", tur.getGorunenAd(), eksik);
            }
        }
    }

    private void dagitimGecmisi() {
        List<DagitimKaydi> kayitlar = servis.getDagitimlar();
        if (kayitlar.isEmpty()) {
            System.out.println("Henüz dağıtım yapılmadı.");
            return;
        }
        dagitimTablosu(kayitlar);
    }

    private void dagitimTablosu(List<DagitimKaydi> kayitlar) {
        System.out.printf("%-17s %-6s %-20s %-8s %8s  %s%n", "Tarih", "Talep", "Bölge", "Tür", "Miktar", "Yöntem");
        cizgiCek(75);
        for (DagitimKaydi d : kayitlar) {
            System.out.printf("%-17s %-6s %-20s %-8s %8d  %s%n", TarihFormat.yaz(d.getTarih()), d.getTalepId(),
                    kisalt(d.getBolgeAdi(), 20), d.getKaynakTuru().getGorunenAd(), d.getMiktar(),
                    d.getStratejiAdi());
        }
    }

    // ---------------- rapor ----------------

    private void genelRapor() {
        System.out.println("=== GENEL DURUM RAPORU (" + TarihFormat.yaz(LocalDate.now()) + ") ===");
        System.out.println();
        System.out.printf("%-8s %10s %18s %10s%n", "Tür", "Stok", "Bekleyen ihtiyaç", "Eksik");
        cizgiCek(50);
        for (KaynakTuru tur : KaynakTuru.values()) {
            int stok = servis.kullanilabilirStok(tur);
            int ihtiyac = servis.bekleyenIhtiyac(tur);
            System.out.printf("%-8s %10d %18d %10d%n", tur.getGorunenAd(), stok, ihtiyac, Math.max(0, ihtiyac - stok));
        }

        System.out.println();
        System.out.println("Talep durumları:");
        for (TalepDurumu durum : TalepDurumu.values()) {
            int sayi = 0;
            for (YardimTalebi t : servis.getTalepler()) {
                if (t.getDurum() == durum) {
                    sayi++;
                }
            }
            System.out.printf("  %-18s: %d%n", durum.getGorunenAd(), sayi);
        }

        System.out.println();
        List<TarihliKaynak> yaklasanlar = servis.sktYaklasanlar(SKT_UYARI_GUNU);
        if (yaklasanlar.isEmpty()) {
            System.out.println("Son kullanma tarihi " + SKT_UYARI_GUNU + " gün içinde dolacak kaynak yok.");
        } else {
            System.out.println("Son kullanma tarihi " + SKT_UYARI_GUNU + " gün içinde dolacaklar (önce bunlar dağıtılır):");
            for (TarihliKaynak k : yaklasanlar) {
                System.out.println("  " + k + " - " + k.getAciklama());
            }
        }

        List<Kaynak> gecenler = servis.tarihiGecenler();
        if (!gecenler.isEmpty()) {
            System.out.println();
            System.out.println("Tarihi geçtiği için dağıtılamayacak kaynaklar (imha edilmeli):");
            for (Kaynak k : gecenler) {
                System.out.println("  " + k + " - " + k.getAciklama());
            }
        }
    }

    // ---------------- yardımcı metotlar ----------------

    private KaynakTuru turSec() {
        KaynakTuru[] turler = KaynakTuru.values();
        System.out.println("Kaynak türü:");
        for (int i = 0; i < turler.length; i++) {
            System.out.println("  " + (i + 1) + "- " + turler[i].getGorunenAd());
        }
        return turler[sayiOku("Seçim: ", 1, turler.length) - 1];
    }

    private Aciliyet aciliyetSec() {
        Aciliyet[] seviyeler = Aciliyet.values();
        System.out.println("Aciliyet durumu:");
        for (int i = 0; i < seviyeler.length; i++) {
            System.out.println("  " + (i + 1) + "- " + seviyeler[i].getGorunenAd());
        }
        return seviyeler[sayiOku("Seçim: ", 1, seviyeler.length) - 1];
    }

    private int sayiOku(String mesaj, int min, int max) {
        while (true) {
            System.out.print(mesaj);
            String girdi = scanner.nextLine().trim();
            try {
                int sayi = Integer.parseInt(girdi);
                if (sayi >= min && sayi <= max) {
                    return sayi;
                }
            } catch (NumberFormatException e) {
                // aşağıda uyarı veriliyor
            }
            System.out.println("Lütfen " + min + " ile " + max + " arasında bir sayı girin.");
        }
    }

    private String metinOku(String mesaj) {
        while (true) {
            System.out.print(mesaj);
            String girdi = scanner.nextLine().trim();
            if (!girdi.isEmpty()) {
                return girdi;
            }
            System.out.println("Bu alan boş bırakılamaz.");
        }
    }

    private LocalDate sktOku() {
        while (true) {
            System.out.print("Son kullanma tarihi (gg.aa.yyyy): ");
            String girdi = scanner.nextLine().trim();
            try {
                LocalDate tarih = LocalDate.parse(girdi, TarihFormat.TARIH);
                if (!tarih.isBefore(LocalDate.now())) {
                    return tarih;
                }
                System.out.println("Son kullanma tarihi geçmiş ürün depoya alınamaz.");
            } catch (DateTimeParseException e) {
                System.out.println("Tarih gg.aa.yyyy şeklinde olmalı (ör: 15.03.2027)");
            }
        }
    }

    private boolean evetMi(String mesaj) {
        while (true) {
            System.out.print(mesaj);
            String girdi = scanner.nextLine().trim().toLowerCase();
            if (girdi.equals("e") || girdi.equals("evet")) {
                return true;
            }
            if (girdi.equals("h") || girdi.equals("hayır") || girdi.equals("hayir")) {
                return false;
            }
            System.out.println("e veya h girin.");
        }
    }

    private static String kisalt(String metin, int uzunluk) {
        if (metin.length() <= uzunluk) {
            return metin;
        }
        return metin.substring(0, uzunluk - 2) + "..";
    }

    private static void cizgiCek(int uzunluk) {
        System.out.println("-".repeat(uzunluk));
    }
}
