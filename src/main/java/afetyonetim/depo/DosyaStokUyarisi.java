package afetyonetim.depo;

import afetyonetim.model.KaynakTuru;
import afetyonetim.util.TarihFormat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

// kritik stok uyarılarını sonradan bakılabilsin diye log dosyasına yazar
public class DosyaStokUyarisi implements StokDinleyici {

    private final Path dosya;

    public DosyaStokUyarisi(Path dosya) {
        this.dosya = dosya;
    }

    @Override
    public void kritikStok(KaynakTuru tur, int kalanMiktar) {
        String satir = TarihFormat.yaz(LocalDateTime.now()) + " - " + tur.getGorunenAd()
                + " stoğu kritik seviyede, kalan: " + kalanMiktar + System.lineSeparator();
        try {
            if (dosya.getParent() != null) {
                Files.createDirectories(dosya.getParent());
            }
            Files.writeString(dosya, satir, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Uyarı log dosyasına yazılamadı: " + e.getMessage());
        }
    }
}
