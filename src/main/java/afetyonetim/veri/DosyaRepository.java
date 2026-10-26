package afetyonetim.veri;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Verileri ';' ile ayrılmış metin dosyalarında tutan ortak sınıf (Template Method).
 * Okuma/yazma akışı burada, satırın nesneye nasıl çevrileceği alt sınıflarda.
 */
public abstract class DosyaRepository<T> implements Repository<T> {

    protected static final String AYRAC = ";";

    private final Path dosya;
    private List<T> onbellek; // dosyayı her seferinde baştan okumamak için

    protected DosyaRepository(Path dosya) {
        this.dosya = dosya;
    }

    @Override
    public List<T> hepsiniYukle() {
        if (onbellek == null) {
            onbellek = dosyadanOku();
        }
        return new ArrayList<>(onbellek);
    }

    @Override
    public void hepsiniKaydet(List<T> liste) {
        List<String> satirlar = new ArrayList<>();
        satirlar.add("# " + baslik());
        for (T nesne : liste) {
            satirlar.add(satiraCevir(nesne));
        }
        try {
            if (dosya.getParent() != null) {
                Files.createDirectories(dosya.getParent());
            }
            Files.write(dosya, satirlar, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Dosyaya yazılamadı: " + dosya, e);
        }
        onbellek = new ArrayList<>(liste);
    }

    private List<T> dosyadanOku() {
        List<T> liste = new ArrayList<>();
        if (!Files.exists(dosya)) {
            return liste;
        }
        List<String> satirlar;
        try {
            satirlar = Files.readAllLines(dosya, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Dosya okunamadı: " + dosya, e);
        }

        for (String satir : satirlar) {
            // not defteri ile kaydedilirse başa BOM karakteri gelebiliyor
            if (satir.startsWith("﻿")) {
                satir = satir.substring(1);
            }
            if (satir.isBlank() || satir.startsWith("#")) {
                continue;
            }
            try {
                liste.add(satirdanOku(satir.split(AYRAC, -1)));
            } catch (RuntimeException e) {
                // bozuk bir satır yüzünden bütün program çökmesin
                System.out.println("Uyarı: " + dosya.getFileName() + " dosyasındaki hatalı satır atlandı -> " + satir);
            }
        }
        return liste;
    }

    // metnin içinde ayıraç varsa dosya düzeni bozulmasın
    protected static String temizle(String metin) {
        return metin.replace(AYRAC, ",");
    }

    protected abstract T satirdanOku(String[] alanlar);

    protected abstract String satiraCevir(T nesne);

    protected abstract String baslik();
}
