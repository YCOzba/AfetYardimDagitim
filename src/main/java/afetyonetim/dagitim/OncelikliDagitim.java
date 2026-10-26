package afetyonetim.dagitim;

import afetyonetim.model.YardimTalebi;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Açgözlü (greedy) dağıtım: en yüksek puanlı talep kuyruktan alınır ve
 * stok yettiği kadar tamamen karşılanır, sonra sıradakine geçilir.
 */
public class OncelikliDagitim implements DagitimStratejisi {

    private final OncelikHesaplayici hesaplayici;

    public OncelikliDagitim(OncelikHesaplayici hesaplayici) {
        this.hesaplayici = hesaplayici;
    }

    @Override
    public String getAd() {
        return "Öncelikli";
    }

    @Override
    public String getAciklama() {
        return "en acil talep önce tamamen karşılanır, stok kalırsa sıradakine geçilir";
    }

    @Override
    public Map<YardimTalebi, Integer> dagit(List<YardimTalebi> talepler, int stok) {
        Map<YardimTalebi, Integer> sonuc = new LinkedHashMap<>();

        // kuyruğun başında her zaman en yüksek puanlı talep oluyor
        PriorityQueue<YardimTalebi> kuyruk = new PriorityQueue<>(hesaplayici.karsilastirici());
        for (YardimTalebi t : talepler) {
            if (!t.tamamlandiMi()) {
                kuyruk.add(t);
            }
        }

        int kalanStok = stok;
        while (kalanStok > 0 && !kuyruk.isEmpty()) {
            YardimTalebi talep = kuyruk.poll();
            int verilecek = Math.min(talep.kalanIhtiyac(), kalanStok);
            sonuc.put(talep, verilecek);
            kalanStok -= verilecek;
        }
        return sonuc;
    }
}
