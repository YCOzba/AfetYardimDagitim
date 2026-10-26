package afetyonetim.depo;

import afetyonetim.model.KaynakTuru;

public class KonsolStokUyarisi implements StokDinleyici {

    @Override
    public void kritikStok(KaynakTuru tur, int kalanMiktar) {
        if (kalanMiktar == 0) {
            System.out.println("!!! UYARI: " + tur.getGorunenAd() + " stoğu tükendi, yeni bağış/tedarik gerekiyor.");
        } else {
            System.out.println("!!! UYARI: " + tur.getGorunenAd() + " stoğu kritik seviyede (kalan: " + kalanMiktar + ")");
        }
    }
}
