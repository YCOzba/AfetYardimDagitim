package afetyonetim.dagitim;

import afetyonetim.model.YardimTalebi;

import java.util.List;
import java.util.Map;

// Strategy: dağıtımın nasıl yapılacağı bu arayüzü uygulayan sınıfa göre değişiyor
public interface DagitimStratejisi {

    String getAd();

    String getAciklama();

    /**
     * Aynı kaynak türündeki bekleyen talepler arasında eldeki stoğu paylaştırır.
     * Talepleri değiştirmez, sadece hangi talebe ne kadar verileceğini döndürür.
     * Dönen miktarların toplamı stoğu geçmez.
     */
    Map<YardimTalebi, Integer> dagit(List<YardimTalebi> talepler, int stok);
}
