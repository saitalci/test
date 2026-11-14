# APK'yı bilgisayara program kurmadan üretme (GitHub, ücretsiz)

1. github.com'da ücretsiz hesap açın (varsa giriş yapın).
2. Sağ üstte "+" > "New repository" > ad: rimsa-terminal > "Private" seçin > "Create repository".
3. Açılan sayfada "uploading an existing file" bağlantısına tıklayın.
4. Bu zip'i bilgisayarınızda açın. İçindeki TÜM dosya ve klasörleri
   (.github, app, build.gradle, settings.gradle, gradle.properties ...) sürükleyip bırakın.
   Not: ".github" klasörü gizli sayılabilir; görünmüyorsa dosya yöneticisinde "gizli dosyaları göster" açın.
5. "Commit changes" düğmesine basın.
6. Üstteki "Actions" sekmesine gidin. "APK olustur" çalışmaya başlar (3-6 dakika). Yeşil tik olunca tıklayın.
7. Sayfanın altında "Artifacts" bölümünde "RimsaTerminal-apk" dosyasını indirin, zip'ten çıkan app-debug.apk dosyasını telefona gönderin.
8. Telefonda APK'ya dokunun, "Bilinmeyen kaynaklardan yüklemeye izin ver" deyip kurun.

Kırmızı çarpı çıkarsa o adımın üstüne tıklayıp hata metnini bana gönderin, düzeltirim.

# Alternatif: Android Studio
File > Open > bu klasör > Build > Build APK(s). Çıktı: app/build/outputs/apk/debug/app-debug.apk
