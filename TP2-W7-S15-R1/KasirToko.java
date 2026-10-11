import java.util.Locale;
import java.util.Scanner;

/**
 * Node = menyimpan data satu pelanggan (kode antrian, nama, total belanja).
 * Dipakai bersama oleh Queue dan Stack.
 */
class Node {
    String kode;
    String nama;
    long total;
    Node next; // penunjuk ke node berikutnya

    Node(String kode, String nama, long total) {
        this.kode = kode;
        this.nama = nama;
        this.total = total;
        this.next = null;
    }
}

/** Queue (antrian, FIFO): masuk di belakang (rear), keluar dari depan (front). */
class Queue {
    static final int MAKS = 5;  // batas jumlah pelanggan dalam antrian
    private Node front;
    private Node rear;
    private int size;

    boolean isEmpty() {
        return front == null;
    }

    boolean isFull() {
        return size >= MAKS;
    }

    int getSize() {
        return size;
    }

    /** Enqueue: menambah pelanggan di belakang antrian. */
    void enqueue(Node baru) {
        if (isEmpty()) {
            front = baru;       // antrian kosong: front dan rear sama
        } else {
            rear.next = baru;
        }
        rear = baru;
        size++;
    }

    /** Dequeue: mengambil pelanggan dari depan antrian (null jika kosong). */
    Node dequeue() {
        if (isEmpty()) {
            return null;
        }
        Node keluar = front;
        front = front.next;
        if (front == null) {
            rear = null;        // antrian menjadi kosong
        }
        keluar.next = null;     // lepaskan dari antrian agar bisa masuk stack
        size--;
        return keluar;
    }

    /** Menampilkan antrian dari depan ke belakang. */
    void tampilkan() {
        if (isEmpty()) {
            System.out.println("Antrian kosong.");
            return;
        }
        System.out.println("Antrian Saat Ini (depan -> belakang):");
        Node bantu = front;
        int no = 1;
        while (bantu != null) {
            System.out.println(no + ". " + KasirToko.format(bantu));
            bantu = bantu.next;
            no++;
        }
        System.out.println("Jumlah dalam antrian: " + size + " dari " + MAKS);
    }

    /** Cek apakah nomor antrian sudah ada di antrian. */
    boolean ada(String kode) {
        Node bantu = front;
        while (bantu != null) {
            if (bantu.kode.equalsIgnoreCase(kode)) {
                return true;
            }
            bantu = bantu.next;
        }
        return false;
    }
}

/** Stack (tumpukan, LIFO): push dan pop hanya pada bagian atas (top). */
class Stack {
    private Node top;
    private int size;

    boolean isEmpty() {
        return top == null;
    }

    int getSize() {
        return size;
    }

    /** Push: menaruh transaksi di atas tumpukan. */
    void push(Node data) {
        data.next = top;        // node baru menunjuk ke top lama
        top = data;             // node baru menjadi top
        size++;
    }

    /** Pop: mengambil transaksi teratas (null jika kosong). */
    Node pop() {
        if (isEmpty()) {
            return null;
        }
        Node atas = top;
        top = top.next;
        atas.next = null;
        size--;
        return atas;
    }

    /** Menampilkan riwayat dari transaksi terbaru (atas) ke terlama (bawah). */
    void tampilkan() {
        if (isEmpty()) {
            System.out.println("Belum ada riwayat transaksi.");
            return;
        }
        System.out.println("Riwayat Transaksi (terbaru -> terlama):");
        Node bantu = top;
        int no = 1;
        long omzet = 0;
        while (bantu != null) {
            System.out.println(no + ". " + KasirToko.format(bantu));
            omzet += bantu.total;
            bantu = bantu.next;
            no++;
        }
        System.out.println("Jumlah transaksi: " + size);
        System.out.println("Total pendapatan: " + KasirToko.rupiah(omzet));
    }
}

/** Program utama: sistem kasir toko dengan Queue (antrian) dan Stack (riwayat). */
public class KasirToko {
    private static final int MIN_PELANGGAN = 5; // minimal pelanggan yang diuji
    private static final Scanner input = new Scanner(System.in);

    /** Mengubah angka menjadi format rupiah, contoh 125000 -> Rp125.000. */
    static String rupiah(long nilai) {
        return "Rp" + String.format(Locale.US, "%,d", nilai).replace(',', '.');
    }

    /** Format satu baris data pelanggan. */
    static String format(Node n) {
        return n.kode + " | " + n.nama + " | " + rupiah(n.total);
    }

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

    /** Membaca nomor antrian yang tidak kosong dan belum dipakai. */
    private static String bacaNomor(Queue antrian) {
        while (true) {
            String nomor = bacaTeks("Masukkan Nomor Antrian: ");
            if (antrian.ada(nomor)) {
                System.out.println("Nomor antrian sudah ada! Gunakan nomor lain.");
            } else {
                return nomor;
            }
        }
    }

    /** Membaca total belanja berupa bilangan bulat positif. */
    private static long bacaTotal() {
        while (true) {
            String s = bacaTeks("Masukkan Total Belanja: ");
            try {
                long nilai = Long.parseLong(s);
                if (nilai > 0) {
                    return nilai;
                }
            } catch (NumberFormatException e) {
                // jatuh ke pesan kesalahan di bawah
            }
            System.out.println("Total belanja harus berupa angka lebih dari 0.");
        }
    }

    private static void tampilMenu() {
        System.out.println();
        System.out.println("=== SISTEM KASIR TOKO ===");
        System.out.println("1. Tambah Antrian");
        System.out.println("2. Layani Pelanggan");
        System.out.println("3. Tampilkan Antrian");
        System.out.println("4. Lihat Riwayat Transaksi");
        System.out.println("5. Keluar");
    }

    public static void main(String[] args) {
        Queue antrian = new Queue();
        Stack riwayat = new Stack();
        int totalMasuk = 0; // jumlah pelanggan yang pernah didaftarkan
        int pilihan = 0;

        while (pilihan != 5) {
            tampilMenu();
            System.out.print("Pilih menu: ");
            String masukan = input.nextLine().trim();
            try {
                pilihan = Integer.parseInt(masukan);
            } catch (NumberFormatException e) {
                pilihan = 0;
            }
            System.out.println();

            switch (pilihan) {
                case 1: // Enqueue
                    if (antrian.isFull()) {
                        System.out.println("Antrian penuh! Maksimal "
                                + Queue.MAKS + " pelanggan.");
                        break;
                    }
                    String nomor = bacaNomor(antrian);
                    String nama = bacaTeks("Masukkan Nama Pelanggan: ");
                    long total = bacaTotal();
                    antrian.enqueue(new Node(nomor, nama, total));
                    totalMasuk++;
                    System.out.println("Data pelanggan ditambahkan ke antrian!");
                    break;

                case 2: // Dequeue + Push ke Stack
                    Node dilayani = antrian.dequeue();
                    if (dilayani == null) {
                        System.out.println("Antrian kosong, tidak ada "
                                + "pelanggan untuk dilayani.");
                    } else {
                        System.out.println("Melayani pelanggan " + dilayani.kode
                                + " (" + dilayani.nama + ")");
                        System.out.println("Total belanja: "
                                + rupiah(dilayani.total));
                        riwayat.push(dilayani);
                        System.out.println("Transaksi disimpan ke riwayat.");
                    }
                    break;

                case 3: // Tampilkan antrian
                    antrian.tampilkan();
                    break;

                case 4: // Tampilkan riwayat (LIFO)
                    riwayat.tampilkan();
                    break;

                case 5: // Keluar
                    System.out.println("Terima kasih. Program selesai.");
                    if (totalMasuk < MIN_PELANGGAN) {
                        System.out.println("Minimal " + MIN_PELANGGAN
                                + " pelanggan, saat ini baru " + totalMasuk + ".");
                    }
                    break;

                default:
                    System.out.println("Menu tidak valid. Pilih angka 1-5.");
            }
        }
        input.close();
    }
}