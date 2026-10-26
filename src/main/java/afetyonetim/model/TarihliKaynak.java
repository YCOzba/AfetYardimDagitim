package afetyonetim.model;

import afetyonetim.util.TarihFormat;

import java.time.LocalDate;

// son kullanma tarihi olan kaynaklar (gıda, su, ilaç) için ara sınıf
public abstract class TarihliKaynak extends Kaynak {

    private final LocalDate sonKullanmaTarihi;

    protected TarihliKaynak(String id, String ad, int miktar, LocalDate sonKullanmaTarihi) {
        super(id, ad, miktar);
        if (sonKullanmaTarihi == null) {
            throw new IllegalArgumentException("Son kullanma tarihi girilmeli");
        }
        this.sonKullanmaTarihi = sonKullanmaTarihi;
    }

    // tarihi geçmiş ürün dağıtılmamalı
    @Override
    public boolean kullanilabilirMi(LocalDate tarih) {
        return super.kullanilabilirMi(tarih) && !sonKullanmaTarihi.isBefore(tarih);
    }

    public boolean sktYaklasiyorMu(LocalDate bugun, int gun) {
        return !sonKullanmaTarihi.isBefore(bugun) && !sonKullanmaTarihi.isAfter(bugun.plusDays(gun));
    }

    public LocalDate getSonKullanmaTarihi() {
        return sonKullanmaTarihi;
    }

    @Override
    public String getEkBilgi() {
        return sonKullanmaTarihi.toString();
    }

    @Override
    public String getAciklama() {
        return "SKT: " + TarihFormat.yaz(sonKullanmaTarihi);
    }
}
