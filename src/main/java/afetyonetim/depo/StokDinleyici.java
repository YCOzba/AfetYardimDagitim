package afetyonetim.depo;

import afetyonetim.model.KaynakTuru;

// Observer: stok kritik seviyenin altına inince Depo bu arayüzü uygulayan sınıflara haber veriyor
public interface StokDinleyici {

    void kritikStok(KaynakTuru tur, int kalanMiktar);
}
