using System.Collections.Concurrent;
using System.Security.Cryptography;
using System.Text;
using Microsoft.Data.SqlClient;

var builder = WebApplication.CreateBuilder(args);
builder.Services.AddCors(o => o.AddDefaultPolicy(p => p.AllowAnyOrigin().AllowAnyHeader().AllowAnyMethod()));
var app = builder.Build();
app.UseCors();

var cs = builder.Configuration.GetConnectionString("Db")
         ?? throw new Exception("ConnectionStrings:Db ayari yok (appsettings.json)");
var tokens = new ConcurrentDictionary<string, (int id, string kod, DateTime son)>();

// Orijinal terminal uygulamasindaki parola sifreleme:
// Rijndael (AES), anahtar "KaliteKontrol2009" 24 karaktere '*' ile tamamlanir,
// IV = 16 sifir bayt, CBC, PKCS7, sonuc Base64.
static string Sifrele(string metin)
{
    using var aes = Aes.Create();
    aes.Key = Encoding.UTF8.GetBytes("KaliteKontrol2009".PadRight(24, '*'));
    aes.IV = new byte[16];
    aes.Mode = CipherMode.CBC;
    aes.Padding = PaddingMode.PKCS7;
    var veri = Encoding.UTF8.GetBytes(metin);
    return Convert.ToBase64String(aes.CreateEncryptor().TransformFinalBlock(veri, 0, veri.Length));
}

app.MapGet("/api/ping", () => Results.Ok(new { ok = true, zaman = DateTime.Now }));

app.MapGet("/api/kullanicilar", async () =>
{
    var liste = new List<object>();
    try
    {
        await using var c = new SqlConnection(cs);
        await c.OpenAsync();
        await using var cmd = new SqlCommand(
            "SELECT ID, KullaniciKodu FROM tblKullanici WHERE Durum=1 ORDER BY KullaniciKodu ASC", c);
        await using var r = await cmd.ExecuteReaderAsync();
        while (await r.ReadAsync())
            liste.Add(new { id = Convert.ToInt32(r["ID"]), kod = r["KullaniciKodu"].ToString() });
        return Results.Ok(liste);
    }
    catch (Exception ex)
    {
        Console.WriteLine("kullanicilar: " + ex.Message);
        return Results.Json(new { hata = "Veritabanina baglanilamadi" }, statusCode: 500);
    }
});

app.MapPost("/api/giris", async (GirisIstek g) =>
{
    if (string.IsNullOrWhiteSpace(g.kod) || string.IsNullOrEmpty(g.parola))
        return Results.Json(new { hata = "Kullanici adi ve sifre gerekli" }, statusCode: 400);
    try
    {
        await using var c = new SqlConnection(cs);
        await c.OpenAsync();
        await using var cmd = new SqlCommand(
            "SELECT ID FROM tblKullanici WHERE KullaniciKodu=@k AND Parola=@p AND Durum=1", c);
        cmd.Parameters.AddWithValue("@k", g.kod);
        cmd.Parameters.AddWithValue("@p", Sifrele(g.parola));
        var sonuc = await cmd.ExecuteScalarAsync();
        if (sonuc is null)
        {
            await Task.Delay(400);
            return Results.Json(new { hata = "Kullanici adi ve sifre hatali" }, statusCode: 401);
        }
        var token = Guid.NewGuid().ToString("N");
        var id = Convert.ToInt32(sonuc);
        tokens[token] = (id, g.kod, DateTime.Now.AddHours(12));
        return Results.Ok(new { token, id, kod = g.kod });
    }
    catch (Exception ex)
    {
        Console.WriteLine("giris: " + ex.Message);
        return Results.Json(new { hata = "Veritabanina baglanilamadi" }, statusCode: 500);
    }
});

app.Run();

record GirisIstek(string kod, string parola);
