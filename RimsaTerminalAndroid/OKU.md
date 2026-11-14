# APK oluşturma (Android Studio)
1. Android Studio'yu açın: File > Open > bu klasörü seçin (Gradle senkronizasyonu bitsin).
2. Build > Build Bundle(s) / APK(s) > Build APK(s).
3. Çıktı: app/build/outputs/apk/debug/app-debug.apk -> telefona atıp kurun (Bilinmeyen kaynaklara izin verin).
Arayüz: app/src/main/assets/index.html (prototip). Bu proje bu ortamda derlenmedi, ilk açılışta sürüm uyarısı çıkarsa Android Studio'nun önerdiği güncellemeyi kabul edin.

# Kamera ile barkod
Kamera düğmesi (📷) Google'ın hazır barkod tarayıcısını (ML Kit) açar; Google Play hizmetleri olan telefonlarda çalışır.
İlk kullanımda tarayıcı bileşeni kendiliğinden indirilebilir (internet gerekir). Kamera izni ayrıca gerekmez.
Sayfa window.Android.scan() ile tetikler, sonuç window.onNativeScan(değer) ile geri döner.
