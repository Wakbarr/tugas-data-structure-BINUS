import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;

/** Class Lagu: judul, artis, durasi (menit). */
class Lagu {
    String judul;
    String artis;
    double durasi; // dalam menit, contoh 4.52

    Lagu(String judul, String artis, double durasi) {
        this.judul = judul;
        this.artis = artis;
        this.durasi = durasi;
    }
}

public class PlaylistRekursif {

    // Atribut playlist (array objek Lagu)
    static Lagu[] playlist;

    // Penghitung pemanggilan fungsi, dipakai untuk bukti growth rate
    static long hitungPanggilan = 0;

    // ------------------------------------------------------------------
    // 1. totalDurasi
    // Tujuan          : menjumlahkan durasi seluruh lagu pada playlist.
    // Base case       : n == 0 (tidak ada lagu) -> mengembalikan 0.
    // Recursive case  : durasi lagu ke-n + totalDurasi(list, n - 1).
    // Kompleksitas    : waktu O(n) karena ada n + 1 pemanggilan,
    //                   ruang O(n) karena kedalaman call stack n.
    //                   Recurrence: T(n) = T(n-1) + c  =>  T(n) = O(n)
    // ------------------------------------------------------------------
    static double totalDurasi(Lagu[] list, int n) {
        hitungPanggilan++;
        if (n == 0) {
            return 0;
        }
        return list[n - 1].durasi + totalDurasi(list, n - 1);
    }

    // ------------------------------------------------------------------
    // 2. tampilkanMundur
    // Tujuan          : menampilkan daftar lagu dari terakhir ke pertama.
    // Base case       : index < 0 (sudah melewati lagu pertama) -> berhenti.
    // Recursive case  : cetak lagu pada index, lalu tampilkanMundur(list, index - 1).
    // Kompleksitas    : waktu O(n) karena setiap lagu dicetak tepat sekali,
    //                   ruang O(n) untuk call stack.
    //                   Recurrence: T(n) = T(n-1) + c  =>  T(n) = O(n)
    // ------------------------------------------------------------------
    static void tampilkanMundur(Lagu[] list, int index) {
        hitungPanggilan++;
        if (index < 0) {
            return;
        }
        System.out.println((index + 1) + ". " + list[index].judul);
        tampilkanMundur(list, index - 1);
    }

    // ------------------------------------------------------------------
    // 3. cariDurasiTerpanjang
    // Tujuan          : mencari durasi terpanjang di antara lagu 0..index.
    // Base case       : index == 0 (hanya satu lagu) -> durasinya sendiri.
    // Recursive case  : max(durasi lagu index, cariDurasiTerpanjang(list, index - 1)).
    // Kompleksitas    : waktu O(n) karena setiap lagu dibandingkan sekali,
    //                   ruang O(n) untuk call stack.
    //                   Recurrence: T(n) = T(n-1) + c  =>  T(n) = O(n)
    // ------------------------------------------------------------------
    static double cariDurasiTerpanjang(Lagu[] list, int index) {
        hitungPanggilan++;
        if (index == 0) {
            return list[0].durasi;
        }
        return Math.max(list[index].durasi, cariDurasiTerpanjang(list, index - 1));
    }

    // ------------------------------------------------------------------
    // Fungsi bantu: mengembalikan objek Lagu terpanjang (untuk menampilkan judulnya).
    // Base case       : index == 0 -> lagu pertama.
    // Recursive case  : bandingkan lagu index dengan lagu terpanjang dari 0..index-1.
    //                   Jika durasi sama, lagu yang lebih akhir dipilih (>=).
    // Kompleksitas    : waktu O(n), ruang O(n).
    // ------------------------------------------------------------------
    static Lagu cariLaguTerpanjang(Lagu[] list, int index) {
        if (index == 0) {
            return list[0];
        }
        Lagu sebelumnya = cariLaguTerpanjang(list, index - 1);
        return list[index].durasi >= sebelumnya.durasi ? list[index] : sebelumnya;
    }

    // ------------------------------------------------------------------
    // Data playlist. GANTI dengan data lagu dari Tugas Kelompok 1 bila berbeda.
    // ------------------------------------------------------------------
    static Lagu[] semuaLagu() {
        return new Lagu[] {
            new Lagu("Perfect",        "Ed Sheeran", 4.23),
            new Lagu("Shivers",        "Ed Sheeran", 3.50),
            new Lagu("Fix You",        "Coldplay",   4.52),
            new Lagu("Yellow",         "Coldplay",   3.58),
            new Lagu("Viva La Vida",   "Coldplay",   4.52),
            new Lagu("Photograph",     "Ed Sheeran", 4.18),
            new Lagu("Paradise",       "Coldplay",   4.38),
            new Lagu("Thinking Out Loud", "Ed Sheeran", 4.41),
            new Lagu("The Scientist",  "Coldplay",   5.09),
            new Lagu("Bad Habits",     "Ed Sheeran", 3.51)
        };
    }

    /** Mengambil n lagu pertama dari data. Jika n lebih besar dari data, lagu dibuat otomatis. */
    static Lagu[] buatPlaylist(int n) {
        Lagu[] data = semuaLagu();
        Lagu[] hasil = new Lagu[n];
        for (int i = 0; i < n; i++) {
            hasil[i] = (i < data.length)
                ? data[i]
                : new Lagu("Lagu " + (i + 1), "Artis " + (i % 7), 2.0 + (i % 50) / 10.0);
        }
        return hasil;
    }

    // ------------------------------------------------------------------
    // Pengukuran waktu (nanoTime dikonversi ke milidetik)
    // ------------------------------------------------------------------
    static double ms(long nanos) {
        return nanos / 1_000_000.0;
    }

    /** Rata-rata waktu (ms) per pemanggilan; output dimatikan agar I/O tidak mengganggu pengukuran. */
    static double[] rataRataWaktu(Lagu[] list, int ulang) {
        PrintStream asli = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        double sink = 0; // mencegah optimasi JIT membuang pemanggilan
        try {
            for (int i = 0; i < 2000; i++) {          // pemanasan JIT
                sink += totalDurasi(list, list.length);
                tampilkanMundur(list, list.length - 1);
                sink += cariDurasiTerpanjang(list, list.length - 1);
            }
            long t0 = System.nanoTime();
            for (int i = 0; i < ulang; i++) sink += totalDurasi(list, list.length);
            long t1 = System.nanoTime();
            for (int i = 0; i < ulang; i++) tampilkanMundur(list, list.length - 1);
            long t2 = System.nanoTime();
            for (int i = 0; i < ulang; i++) sink += cariDurasiTerpanjang(list, list.length - 1);
            long t3 = System.nanoTime();
            if (sink == -1) asli.println(sink);
            return new double[] { ms(t1 - t0) / ulang, ms(t2 - t1) / ulang, ms(t3 - t2) / ulang };
        } finally {
            System.setOut(asli);
        }
    }

    static String f2(double x) { return String.format(Locale.US, "%.2f", x); }
    static String f3(double x) { return String.format(Locale.US, "%.3f", x); }
    static String f6(double x) { return String.format(Locale.US, "%.6f", x); }

    // ------------------------------------------------------------------
    // Program utama
    // ------------------------------------------------------------------
    public static void main(String[] args) {

        // ===== BAGIAN A: Demonstrasi (5 lagu) =====
        playlist = buatPlaylist(5);

        System.out.println("=== ANALISIS REKURSIF PLAYLIST ===");
        System.out.println("Jumlah lagu : " + playlist.length);

        long a0 = System.nanoTime();
        double total = totalDurasi(playlist, playlist.length);
        long a1 = System.nanoTime();
        System.out.println("Total durasi : " + f2(total) + " menit");

        long b0 = System.nanoTime();
        double terpanjang = cariDurasiTerpanjang(playlist, playlist.length - 1);
        long b1 = System.nanoTime();
        Lagu lt = cariLaguTerpanjang(playlist, playlist.length - 1);
        System.out.println("Lagu terpanjang : \"" + lt.judul + "\" - " + lt.artis
                + " (" + f2(terpanjang) + " menit)");

        System.out.println();
        System.out.println("Daftar lagu (ditampilkan terbalik):");
        long c0 = System.nanoTime();
        tampilkanMundur(playlist, playlist.length - 1);
        long c1 = System.nanoTime();

        System.out.println();
        System.out.println("Execution Time (totalDurasi): " + f3(ms(a1 - a0)) + " ms");
        System.out.println("Execution Time (tampilkanMundur): " + f3(ms(c1 - c0)) + " ms");
        System.out.println("Execution Time (cariDurasiTerpanjang): " + f3(ms(b1 - b0)) + " ms");

        // ===== BAGIAN B: Eksperimen waktu eksekusi n = 3, 5, 10 =====
        System.out.println();
        System.out.println("=== EKSPERIMEN WAKTU EKSEKUSI (rata-rata 100000 pemanggilan, ms) ===");
        System.out.printf("%-8s %-18s %-20s %-24s%n", "n", "totalDurasi()", "tampilkanMundur()", "cariDurasiTerpanjang()");
        int[] ukuran = {3, 5, 10};
        for (int n : ukuran) {
            double[] w = rataRataWaktu(buatPlaylist(n), 100000);
            System.out.printf("%-8d %-18s %-20s %-24s%n", n, f6(w[0]), f6(w[1]), f6(w[2]));
        }

        // ===== BAGIAN C: Jumlah pemanggilan rekursif (bukti growth rate linear) =====
        System.out.println();
        System.out.println("=== JUMLAH PEMANGGILAN REKURSIF (growth rate) ===");
        System.out.printf("%-6s %-16s %-20s %-24s%n", "n", "totalDurasi()", "tampilkanMundur()", "cariDurasiTerpanjang()");
        PrintStream asli = System.out;
        for (int n = 2; n <= 10; n += 2) {
            Lagu[] l = buatPlaylist(n);
            System.setOut(new PrintStream(OutputStream.nullOutputStream()));
            hitungPanggilan = 0; totalDurasi(l, n);                   long p1 = hitungPanggilan;
            hitungPanggilan = 0; tampilkanMundur(l, n - 1);           long p2 = hitungPanggilan;
            hitungPanggilan = 0; cariDurasiTerpanjang(l, n - 1);      long p3 = hitungPanggilan;
            System.setOut(asli);
            System.out.printf("%-6d %-16d %-20d %-24d%n", n, p1, p2, p3);
        }

        // ===== BAGIAN D (tambahan): n besar untuk melihat tren linear =====
        System.out.println();
        System.out.println("=== EKSPERIMEN TAMBAHAN: n BESAR (rata-rata 2000 pemanggilan, ms) ===");
        System.out.printf("%-8s %-18s %-20s %-24s%n", "n", "totalDurasi()", "tampilkanMundur()", "cariDurasiTerpanjang()");
        int[] besar = {100, 500, 1000, 2000, 4000};
        for (int n : besar) {
            try {
                double[] w = rataRataWaktu(buatPlaylist(n), 2000);
                System.out.printf("%-8d %-18s %-20s %-24s%n", n, f6(w[0]), f6(w[1]), f6(w[2]));
            } catch (StackOverflowError e) {
                System.out.printf("%-8d StackOverflowError (kedalaman rekursi terlalu besar)%n", n);
            }
        }
    }
}