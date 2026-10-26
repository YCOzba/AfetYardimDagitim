package afetyonetim.depo;

import afetyonetim.model.Kaynak;
import afetyonetim.model.KaynakTuru;
import afetyonetim.model.TarihliKaynak;
import afetyonetim.model.YetersizStokException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// yardım malzemelerinin tutulduğu depo. stok giriş/çıkışı sadece buradan yapılıyor
public class Depo {

    private final List<Kaynak> kaynaklar = new ArrayList<>();
    private final List<StokDinleyici> dinleyiciler = new ArrayList<>();

    public void dinleyiciEkle(StokDinleyici dinleyici) {
        dinleyiciler.add(dinleyici);
    }

    public void kaynakEkle(Kaynak kaynak) {
        if (kaynak == null) {
            throw new IllegalArgumentException("Kaynak boş olamaz");
        }
        kaynaklar.add(kaynak);
    }

    public List<Kaynak> getKaynaklar() {
        return Collections.unmodifiableList(kaynaklar);
    }

    // tarihi geçmiş ürünler sayılmıyor
    public int kullanilabilirStok(KaynakTuru tur, LocalDate tarih) {
        int toplam = 0;
        for (Kaynak k : kaynaklar) {
            if (k.getTur() == tur && k.kullanilabilirMi(tarih)) {
                toplam += k.getMiktar();
            }
        }
        return toplam;
    }

    /**
     * İstenen miktarı depodan düşer. Son kullanma tarihi en yakın olan kaynaktan
     * başlanıyor (FEFO - first expired first out), böylece ürünlerin tarihi geçip çöpe gitmesi azalıyor.
     */
    public void stokCek(KaynakTuru tur, int miktar, LocalDate tarih) {
        if (miktar <= 0) {
            throw new IllegalArgumentException("Çekilecek miktar pozitif olmalı");
        }
        int mevcut = kullanilabilirStok(tur, tarih);
        if (miktar > mevcut) {
            throw new YetersizStokException(tur.getGorunenAd() + " için yeterli stok yok. Mevcut: "
                    + mevcut + ", istenen: " + miktar);
        }

        List<Kaynak> uygunlar = new ArrayList<>();
        for (Kaynak k : kaynaklar) {
            if (k.getTur() == tur && k.kullanilabilirMi(tarih)) {
                uygunlar.add(k);
            }
        }
        uygunlar.sort(Comparator.comparing(Depo::sonKullanmaTarihi));

        int kalan = miktar;
        for (Kaynak k : uygunlar) {
            if (kalan == 0) {
                break;
            }
            int alinan = Math.min(kalan, k.getMiktar());
            k.stokDus(alinan);
            kalan -= alinan;
        }
        // biten kalemleri listeden çıkar
        kaynaklar.removeIf(k -> k.getMiktar() == 0);

        int yeniStok = mevcut - miktar;
        if (yeniStok < tur.getKritikSeviye()) {
            for (StokDinleyici d : dinleyiciler) {
                d.kritikStok(tur, yeniStok);
            }
        }
    }

    public List<TarihliKaynak> sktYaklasanlar(LocalDate bugun, int gun) {
        List<TarihliKaynak> sonuc = new ArrayList<>();
        for (Kaynak k : kaynaklar) {
            if (k instanceof TarihliKaynak tk && tk.sktYaklasiyorMu(bugun, gun)) {
                sonuc.add(tk);
            }
        }
        return sonuc;
    }

    public List<Kaynak> tarihiGecenler(LocalDate bugun) {
        List<Kaynak> sonuc = new ArrayList<>();
        for (Kaynak k : kaynaklar) {
            if (k.getMiktar() > 0 && !k.kullanilabilirMi(bugun)) {
                sonuc.add(k);
            }
        }
        return sonuc;
    }

    private static LocalDate sonKullanmaTarihi(Kaynak kaynak) {
        if (kaynak instanceof TarihliKaynak tk) {
            return tk.getSonKullanmaTarihi();
        }
        return LocalDate.MAX; // tarihi olmayanlar en sona
    }
}
