import java.util.Scanner;

/*
 * Tugas Kelompok 1 - Week 4
 *
 * Nama Anggota Kelompok :
 * 1. LUTHFIANDRA ARDANA 2902819243
 * 2. IQBAL HERMAWAN 2902827895
 * 3. RIDHO AL HAMDI 2902823032
 * 4. AKBAR WIDIANTO 2902823934
 */

// Class Lagu
class Lagu {
    private String judul;
    private String artis;
    private double durasi; 

    public Lagu(String judul, String artis, double durasi) {
        this.judul = judul;
        this.artis = artis;
        this.durasi = durasi;
    }

    public String getJudul() {
        return judul;
    }

    public double getDurasi() {
        return durasi;
    }

    // Menampilkan informasi lagu dalam format yang rapi
    public void tampilkanInfo(int nomor) {
        System.out.printf("%d. %s - %s (%.2f menit)%n", nomor, judul, artis, durasi);
    }
}

public class playlistarray {

    // Atribut: array statis lagu dengan kapasitas maksimum 10
    private Lagu[] playlist;
    private int jumlahLagu; // jumlah lagu yang sedang tersimpan di dalam array
    private static final int KAPASITAS_MAKS = 10;

    public playlistarray() {
        playlist = new Lagu[KAPASITAS_MAKS];
        jumlahLagu = 0;
    }

    /*
     * OPERASI 1: TRAVERSAL
     */
    public void tampilkanSemuaLagu() {
        System.out.println("\nDaftar lagu saat ini:");
        if (jumlahLagu == 0) {
            System.out.println("(Playlist masih kosong)");
            return;
        }
        for (int i = 0; i < jumlahLagu; i++) {
            playlist[i].tampilkanInfo(i + 1);
        }
    }

    /*
     * OPERASI 2: INSERTION
     */
    public void tambahLagu(Scanner sc) {
        if (jumlahLagu >= KAPASITAS_MAKS) {
            System.out.println("Playlist sudah penuh! Tidak dapat menambah lagu baru.");
            return;
        }
        System.out.print("Masukkan judul lagu : ");
        String judul = sc.nextLine();
        System.out.print("Masukkan artis      : ");
        String artis = sc.nextLine();
        System.out.print("Masukkan durasi (menit): ");
        double durasi = Double.parseDouble(sc.nextLine().trim());

        playlist[jumlahLagu] = new Lagu(judul, artis, durasi);
        jumlahLagu++;
        System.out.println("Lagu berhasil ditambahkan!");
        tampilkanSemuaLagu();
    }

    /*
     * OPERASI 3: DELETION
     */
    public void hapusLagu(Scanner sc) {
        if (jumlahLagu == 0) {
            System.out.println("Playlist masih kosong, tidak ada yang bisa dihapus.");
            return;
        }
        System.out.print("Masukkan judul lagu yang ingin dihapus: ");
        String judul = sc.nextLine();

        int indeks = cariIndeksLagu(judul);
        if (indeks == -1) {
            System.out.println("Lagu dengan judul \"" + judul + "\" tidak ditemukan.");
            return;
        }

        for (int i = indeks; i < jumlahLagu - 1; i++) {
            playlist[i] = playlist[i + 1];
        }
        // kosongkan slot terakhir yang sudah tidak terpakai lagi
        playlist[jumlahLagu - 1] = null;
        jumlahLagu--;
        System.out.println("Lagu \"" + judul + "\" berhasil dihapus.");
        tampilkanSemuaLagu();
    }

    /*
     * OPERASI 4: SEARCHING (Linear Search)
     */
    public void cariLagu(Scanner sc) {
        System.out.print("Masukkan judul lagu yang dicari: ");
        String judul = sc.nextLine();

        int indeks = cariIndeksLagu(judul);
        if (indeks == -1) {
            System.out.println("Lagu dengan judul \"" + judul + "\" tidak ditemukan.");
        } else {
            System.out.println("Lagu ditemukan:");
            playlist[indeks].tampilkanInfo(indeks + 1);
        }
    }

    // Fungsi bantu: linear search berdasarkan judul (case-insensitive),
    // mengembalikan indeks lagu jika ditemukan, atau -1 jika tidak ada.
    private int cariIndeksLagu(String judul) {
        for (int i = 0; i < jumlahLagu; i++) {
            if (playlist[i].getJudul().equalsIgnoreCase(judul)) {
                return i;
            }
        }
        return -1;
    }

    /*
     * SORTING (Bubble Sort)
     */
    public void urutkanLaguBerdasarkanDurasi() {
        if (jumlahLagu == 0) {
            System.out.println("Playlist masih kosong, tidak ada yang bisa diurutkan.");
            return;
        }

        System.out.println("\nSebelum diurutkan:");
        tampilkanSemuaLagu();

        for (int i = 0; i < jumlahLagu - 1; i++) {
            for (int j = 0; j < jumlahLagu - 1 - i; j++) {
                if (playlist[j].getDurasi() > playlist[j + 1].getDurasi()) {
                    Lagu temp = playlist[j];
                    playlist[j] = playlist[j + 1];
                    playlist[j + 1] = temp;
                }
            }
        }

        System.out.println("\nSesudah diurutkan (ascending berdasarkan durasi):");
        tampilkanSemuaLagu();
    }

    // Mengisi beberapa data contoh agar program dapat langsung diuji coba
    private void isiDataAwal() {
        playlist[jumlahLagu++] = new Lagu("Perfect", "Ed Sheeran", 4.23);
        playlist[jumlahLagu++] = new Lagu("Shivers", "Ed Sheeran", 3.50);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        playlistarray app = new playlistarray();
        app.isiDataAwal();

        int pilihan = 0;
        do {
            System.out.println("\n=== MENU PLAYLIST MUSIK ===");
            System.out.println("1. Tampilkan semua lagu");
            System.out.println("2. Tambah lagu baru");
            System.out.println("3. Hapus lagu berdasarkan judul");
            System.out.println("4. Cari lagu berdasarkan judul");
            System.out.println("5. Urutkan berdasarkan durasi");
            System.out.println("6. Keluar");
            System.out.print("Pilih menu: ");

            if (!sc.hasNextInt()) {
                System.out.println("Masukan tidak valid, silakan masukkan angka 1-6.");
                sc.next();
                continue;
            }
            pilihan = sc.nextInt();
            sc.nextLine(); // membuang sisa newline setelah nextInt()

            switch (pilihan) {
                case 1:
                    app.tampilkanSemuaLagu();
                    break;
                case 2:
                    app.tambahLagu(sc);
                    break;
                case 3:
                    app.hapusLagu(sc);
                    break;
                case 4:
                    app.cariLagu(sc);
                    break;
                case 5:
                    app.urutkanLaguBerdasarkanDurasi();
                    break;
                case 6:
                    System.out.println("Terima kasih telah memakai Playlist Musik!");
                    break;
                default:
                    System.out.println("Pilihan tidak valid, silakan pilih 1-6.");
            }
        } while (pilihan != 6);

        sc.close();
    }
}