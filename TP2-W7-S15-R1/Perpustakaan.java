import java.util.Scanner;

/**
 * Node = satu simpul pada Single Linked List.
 * Setiap node menyimpan data satu buku dan penunjuk ke node berikutnya.
 */
class Node {
    String kodeBuku;
    String judul;
    String penulis;
    Node next; // penunjuk ke node berikutnya (null jika node terakhir)

    Node(String kodeBuku, String judul, String penulis) {
        this.kodeBuku = kodeBuku;
        this.judul = judul;
        this.penulis = penulis;
        this.next = null;
    }
}

/**
 * LinkedList = Single Linked List untuk menyimpan daftar buku.
 * Memakai pointer head (awal) dan tail (akhir) agar penambahan di akhir
 * cukup O(1).
 */
class LinkedList {
    private Node head; // node pertama
    private Node tail; // node terakhir
    private int size;  // jumlah buku

    boolean isEmpty() {
        return head == null;
    }

    int getSize() {
        return size;
    }

    /** Push: menambah buku baru di AKHIR daftar. */
    void push(String kode, String judul, String penulis) {
        Node baru = new Node(kode, judul, penulis);
        if (isEmpty()) {
            head = baru;       // daftar kosong: node baru jadi head sekaligus tail
        } else {
            tail.next = baru;  // sambungkan node lama terakhir ke node baru
        }
        tail = baru;
        size++;
    }

    /** Pop: menghapus buku TERAKHIR. Mengembalikan node yang dihapus. */
    Node pop() {
        if (isEmpty()) {
            return null;       // tidak ada data untuk dihapus
        }
        Node dihapus = tail;
        if (head == tail) {
            head = null;       // hanya satu node: daftar menjadi kosong
            tail = null;
        } else {
            Node bantu = head; // cari node sebelum tail
            while (bantu.next != tail) {
                bantu = bantu.next;
            }
            bantu.next = null; // putus hubungan ke node terakhir
            tail = bantu;
        }
        size--;
        return dihapus;
    }

    /** Mencari buku berdasarkan kode (tidak membedakan huruf besar/kecil). */
    Node cari(String kode) {
        Node bantu = head;
        while (bantu != null) {
            if (bantu.kodeBuku.equalsIgnoreCase(kode)) {
                return bantu;
            }
            bantu = bantu.next;
        }
        return null;           // tidak ditemukan
    }

    /** Menampilkan semua buku sesuai urutan input, lalu total buku. */
    void tampilkan() {
        if (isEmpty()) {
            System.out.println("Daftar buku masih kosong.");
        } else {
            System.out.println("Daftar Buku:");
            Node bantu = head;
            while (bantu != null) {
                System.out.println(format(bantu));
                bantu = bantu.next;
            }
        }
        System.out.println("Total Buku: " + size);
    }

    /** Format satu baris data buku. */
    static String format(Node n) {
        return "Kode: " + n.kodeBuku + " | Judul: " + n.judul
                + " | Penulis: " + n.penulis;
    }
}

/** Program utama: menu pengelolaan daftar buku perpustakaan. */
public class Perpustakaan {
    private static final int MAKS_KODE = 5;   // panjang maksimal kodeBuku
    private static final int MIN_BUKU = 5;    // jumlah data minimal yang diuji

    private static final Scanner input = new Scanner(System.in);

    /** Membaca teks tidak kosong dari pengguna. */
    private static String bacaTeks(String pesan) {
        while (true) {
            System.out.print(pesan);
            String s = input.nextLine().trim();
            if (!s.isEmpty()) {
                return s;
            }
            System.out.println("Input tidak boleh kosong. Coba lagi.");
        }
    }

    /** Membaca kodeBuku: tidak kosong, maksimal 5 karakter, dan unik. */
    private static String bacaKode(LinkedList daftar) {
        while (true) {
            String kode = bacaTeks("Masukkan Kode Buku: ");
            if (kode.length() > MAKS_KODE) {
                System.out.println("Kode buku maksimal " + MAKS_KODE
                        + " karakter! Coba lagi.");
            } else if (daftar.cari(kode) != null) {
                System.out.println("Kode buku sudah ada! Gunakan kode lain.");
            } else {
                return kode;
            }
        }
    }

    private static void tampilMenu() {
        System.out.println();
        System.out.println("===== SISTEM DATA BUKU =====");
        System.out.println("1. Tambah Buku");
        System.out.println("2. Hapus Buku");
        System.out.println("3. Cari Buku");
        System.out.println("4. Lihat Semua Buku");
        System.out.println("5. Keluar");
    }

    public static void main(String[] args) {
        LinkedList daftar = new LinkedList();
        int pilihan = 0;

        while (pilihan != 5) {
            tampilMenu();
            System.out.print("Pilih menu: ");
            String masukan = input.nextLine().trim();
            try {
                pilihan = Integer.parseInt(masukan);
            } catch (NumberFormatException e) {
                pilihan = 0;   // bukan angka: dianggap menu tidak valid
            }
            System.out.println();

            switch (pilihan) {
                case 1: // Tambah Buku (Push)
                    String kode = bacaKode(daftar);
                    String judul = bacaTeks("Masukkan Judul: ");
                    String penulis = bacaTeks("Masukkan Penulis: ");
                    daftar.push(kode, judul, penulis);
                    System.out.println("Data berhasil ditambahkan!");
                    break;

                case 2: // Hapus Buku (Pop)
                    Node dihapus = daftar.pop();
                    if (dihapus == null) {
                        System.out.println("Tidak ada data untuk dihapus.");
                    } else {
                        System.out.println("Buku terakhir berhasil dihapus:");
                        System.out.println(LinkedList.format(dihapus));
                    }
                    break;

                case 3: // Cari Buku
                    String cariKode = bacaTeks("Masukkan Kode Buku yang dicari: ");
                    Node hasil = daftar.cari(cariKode);
                    if (hasil == null) {
                        System.out.println("Buku tidak ditemukan.");
                    } else {
                        System.out.println("Buku ditemukan!");
                        System.out.println(LinkedList.format(hasil));
                    }
                    break;

                case 4: // Tampilkan Semua Buku
                    daftar.tampilkan();
                    break;

                case 5: // Keluar
                    System.out.println("Terima kasih. Program selesai.");
                    if (daftar.getSize() < MIN_BUKU) {
                        System.out.println("Minimal " + MIN_BUKU + " buku, saat ini baru "
                                + daftar.getSize() + ".");
                    }
                    break;

                default:
                    System.out.println("Menu tidak valid. Pilih angka 1-5.");
            }
        }
        input.close();
    }
}