package afetyonetim.dagitim;

import afetyonetim.model.YardimTalebi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Stok herkese yetmediğinde tek bir bölgeye her şeyi vermek yerine stok paylaştırılır.
 * Her talebin ağırlığı = öncelik puanı * kalan ihtiyaç.
 * Bir talebin payı ihtiyacından fazla çıkarsa artan kısım sonraki turda diğer taleplere dağıtılır.
 */
public class OransalDagitim implements DagitimStratejisi {

    private final OncelikHesaplayici hesaplayici;

    public OransalDagitim(OncelikHesaplayici hesaplayici) {
        this.hesaplayici = hesaplayici;
    }

    @Override
    public String getAd() {
        return "Oransal";
    }

    @Override
    public String getAciklama() {
        return "stok, taleplerin öncelik puanı ve ihtiyacı oranında paylaştırılır";
    }

    @Override
    public Map<YardimTalebi, Integer> dagit(List<YardimTalebi> talepler, int stok) {
        Map<YardimTalebi, Integer> verilen = new LinkedHashMap<>();

        List<YardimTalebi> aktifler = new ArrayList<>();
        for (YardimTalebi t : hesaplayici.sirala(talepler)) {
            if (!t.tamamlandiMi()) {
                aktifler.add(t);
            }
        }

        int kalanStok = stok;
        while (kalanStok > 0 && !aktifler.isEmpty()) {
            Map<YardimTalebi, Double> agirliklar = new HashMap<>();
            double toplamAgirlik = 0;
            for (YardimTalebi t : aktifler) {
                double agirlik = hesaplayici.puanHesapla(t) * eksik(t, verilen);
                agirliklar.put(t, agirlik);
                toplamAgirlik += agirlik;
            }

            int buTurdaDagitilan = 0;
            for (YardimTalebi t : aktifler) {
                int pay = (int) (kalanStok * agirliklar.get(t) / toplamAgirlik);
                pay = Math.min(pay, eksik(t, verilen));
                if (pay > 0) {
                    verilen.merge(t, pay, Integer::sum);
                    buTurdaDagitilan += pay;
                }
            }

            // aşağı yuvarlama yüzünden kimseye pay çıkmadıysa kalan birimleri öncelik sırasıyla birer birer ver
            if (buTurdaDagitilan == 0) {
                for (YardimTalebi t : aktifler) {
                    if (buTurdaDagitilan == kalanStok) {
                        break;
                    }
                    verilen.merge(t, 1, Integer::sum);
                    buTurdaDagitilan++;
                }
            }

            kalanStok -= buTurdaDagitilan;
            aktifler.removeIf(t -> eksik(t, verilen) == 0);
        }
        return verilen;
    }

    private int eksik(YardimTalebi talep, Map<YardimTalebi, Integer> verilen) {
        return talep.kalanIhtiyac() - verilen.getOrDefault(talep, 0);
    }
}
