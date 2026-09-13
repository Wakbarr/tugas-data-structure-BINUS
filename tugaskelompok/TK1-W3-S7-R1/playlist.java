/*
 * Tugas Kelompok 1 - Week 3
 *
 * Nama Anggota Kelompok :
 * 1. LUTHFIANDRA ARDANA 2902819243
 * 2. IQBAL HERMAWAN 2902827895
 * 3. RIDHO AL HAMDI 2902823032
 * 4. AKBAR WIDIANTO 2902823934
 *
 */

import java.util.Scanner;

// ======================================================
// Class Lagu
// Merepresentasikan satu lagu dengan atribut judul, artis, dan durasi.
// Semua atribut dibuat private (enkapsulasi), jadi cara satu-satunya
// buat ambil atau ubah nilainya cuma lewat getter dan setter.
// ======================================================
class Lagu {
    private String judul;
    private String artis;
    private double durasi; // dalam satuan menit

    public Lagu(String judul, String artis, double durasi) {
        this.judul = judul;
        this.artis = artis;
        this.durasi = durasi;
    }

    // ---- Getter ----
    public String getJudul() {
        return judul;
    }

    public String getArtis() {
        return artis;
    }

    public double getDurasi() {
        return durasi;
    }

    // ---- Setter ----
    public void setJudul(String judul) {
        this.judul = judul;
    }

    public void setArtis(String artis) {
        this.artis = artis;
    }

    public void setDurasi(double durasi) {
        this.durasi = durasi;
    }

    // Menampilkan informasi lengkap dari lagu ini.
    public void tampilkanInfo() {
        System.out.println("Judul  : " + judul);
        System.out.println("Artis  : " + artis);
        System.out.println("Durasi : " + durasi + " menit");
    }
}

// ======================================================
// Class User (parent class)
// Class ini menyimpan atribut yang dipakai bersama oleh Admin dan Member,
// yaitu nama pengguna. Method tampilkanAkses() sengaja dibuat kosong/umum
// di sini supaya nanti di-override (polymorphism) oleh masing-masing
// class turunannya sesuai hak akses mereka.
// ======================================================
class User {
    private String nama;

    public User(String nama) {
        this.nama = nama;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    // Method umum yang akan di-override oleh Admin dan Member.
    // Ini contoh polymorphism: method sama, tapi hasilnya beda
    // tergantung objek turunan mana yang memanggilnya.
    public void tampilkanAkses() {
        System.out.println(nama + " adalah pengguna umum sistem playlist.");
    }
}

// ======================================================
// Class Admin (child class dari User)
// Admin punya tugas menambahkan lagu baru ke playlist dan
// melihat semua lagu yang tersimpan.
// ======================================================
class Admin extends User {

    public Admin(String nama) {
        super(nama); // inheritance: manfaatin constructor dari User
    }

    // Menambahkan lagu baru ke dalam array playlist pada index kosong pertama.
    public boolean tambahkanLagu(Lagu[] playlist, Lagu lagoBaru) {
        for (int i = 0; i < playlist.length; i++) {
            if (playlist[i] == null) {
                playlist[i] = lagoBaru;
                System.out.println("Lagu \"" + lagoBaru.getJudul() + "\" berhasil ditambahkan oleh admin " + getNama() + ".");
                return true;
            }
        }
        System.out.println("Gagal menambahkan lagu, playlist sudah penuh.");
        return false;
    }

    // Menampilkan seluruh lagu yang ada di playlist.
    public void lihatSemuaLagu(Lagu[] playlist) {
        System.out.println("=== Daftar Semua Lagu (Akses Admin) ===");
        for (Lagu lagu : playlist) {
            if (lagu != null) {
                lagu.tampilkanInfo();
                System.out.println("-----------------------------");
            }
        }
    }

    // Override method tampilkanAkses() dari class User (polymorphism).
    @Override
    public void tampilkanAkses() {
        System.out.println(getNama() + " login sebagai ADMIN. Bisa menambahkan lagu dan melihat semua data lagu.");
    }
}

// ======================================================
// Class Member (child class dari User)
// Member cuma bisa melihat daftar lagu dan mencari lagu
// berdasarkan judul, tidak bisa menambahkan lagu baru.
// ======================================================
class Member extends User {

    public Member(String nama) {
        super(nama); // inheritance: manfaatin constructor dari User
    }

    // Menampilkan daftar judul lagu yang tersedia di playlist.
    public void lihatDaftarLagu(Lagu[] playlist) {
        System.out.println("=== Daftar Lagu yang Tersedia ===");
        for (Lagu lagu : playlist) {
            if (lagu != null) {
                System.out.println("- " + lagu.getJudul() + " (" + lagu.getArtis() + ")");
            }
        }
    }

    // Mencari lagu berdasarkan judul yang dimasukkan member.
    public void cariLaguByJudul(Lagu[] playlist, String kataKunci) {
        boolean ketemu = false;
        System.out.println("Hasil pencarian untuk \"" + kataKunci + "\":");
        for (Lagu lagu : playlist) {
            if (lagu != null && lagu.getJudul().toLowerCase().contains(kataKunci.toLowerCase())) {
                lagu.tampilkanInfo();
                System.out.println("-----------------------------");
                ketemu = true;
            }
        }
        if (!ketemu) {
            System.out.println("Lagu tidak ditemukan.");
        }
    }

    // Menghitung rata-rata durasi seluruh lagu yang ada di playlist.
    public double hitungRataRataDurasi(Lagu[] playlist) {
        double total = 0;
        int jumlahLagu = 0;
        for (Lagu lagu : playlist) {
            if (lagu != null) {
                total += lagu.getDurasi();
                jumlahLagu++;
            }
        }
        return jumlahLagu == 0 ? 0 : total / jumlahLagu;
    }

    // Override method tampilkanAkses() dari class User (polymorphism).
    @Override
    public void tampilkanAkses() {
        System.out.println(getNama() + " login sebagai MEMBER. Bisa melihat & mencari lagu, tidak bisa menambahkan lagu.");
    }
}

// ======================================================
// Class utama Playlist
// Berisi method main() yang menjalankan simulasi sistem playlist.
// ======================================================
public class playlist {
    public static void main(String[] args) {
        // Array untuk menyimpan kumpulan objek Lagu (data structure: array).
        Lagu[] playlist = new Lagu[10];

        // Membuat objek Admin dan Member (inheritance dari class User).
        Admin admin = new Admin("Rio");
        Member member = new Member("Sinta");

        System.out.println("========================================");
        System.out.println("   SISTEM MANAJEMEN PLAYLIST MUSIK");
        System.out.println("========================================\n");

        // Polymorphism: method tampilkanAkses() yang sama, tapi hasilnya
        // beda karena dipanggil dari objek Admin dan Member.
        admin.tampilkanAkses();
        member.tampilkanAkses();
        System.out.println();

        // Admin menambahkan beberapa lagu ke playlist.
        admin.tambahkanLagu(playlist, new Lagu("Bohemian Rhapsody", "Queen", 5.9));
        admin.tambahkanLagu(playlist, new Lagu("Shape of You", "Ed Sheeran", 3.8));
        admin.tambahkanLagu(playlist, new Lagu("Blinding Lights", "The Weeknd", 3.4));
        System.out.println();

        // Admin melihat semua lagu yang sudah ditambahkan.
        admin.lihatSemuaLagu(playlist);
        System.out.println();

        // Member melihat daftar lagu yang tersedia.
        member.lihatDaftarLagu(playlist);
        System.out.println();

        // Member mencari lagu berdasarkan judul.
        member.cariLaguByJudul(playlist, "shape");
        System.out.println();

        // Member menghitung rata-rata durasi lagu dalam playlist.
        double rataRata = member.hitungRataRataDurasi(playlist);
        System.out.printf("Rata-rata durasi lagu dalam playlist: %.2f menit%n", rataRata);
    }
}