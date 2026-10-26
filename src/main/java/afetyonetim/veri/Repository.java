package afetyonetim.veri;

import java.util.List;

// servis katmanı verinin nerede tutulduğunu bilmiyor, sadece bu arayüzü kullanıyor
public interface Repository<T> {

    List<T> hepsiniYukle();

    void hepsiniKaydet(List<T> liste);
}
