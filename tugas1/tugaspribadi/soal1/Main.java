public class Main {
    public static void main(String[] args) {
        // Objek
        Mahasiswa m1 = new Mahasiswa("Ahmad Muhazzib", "2440001", "Teknik Informatika", 3.75);
        Mahasiswa m2 = new Mahasiswa("Budi Santoso", "2440002", "Sistem Informasi", 3.40);
        Mahasiswa m3 = new Mahasiswa("Citra Wulandari", "2440003", "Desain Komunikasi Visual", 3.90);
        Mahasiswa m4 = new Mahasiswa("Joni Suhartono", "2440004", "Teknik Industri", 3.00);
        Mahasiswa m5 = new Mahasiswa("Bulan Suci Ramadhani", "2440005", "Akuntansi", 3.20);

        Mahasiswa[] daftarMahasiswa = { m1, m2, m3, m4, m5 };

        // Output
        System.out.println("=== Data Mahasiswa ===");
        for (Mahasiswa m : daftarMahasiswa) {
            m.tampilkanInfo();
        }
    }
}