# TK2 — Optimasi Model CNN & Aplikasi Web (CIFAR-10)

Tugas Kelompok 2, Artificial Intelligence (Week 8). Model CNN untuk klasifikasi 10 kelas CIFAR-10
dioptimasi (batch normalization, dropout, data augmentation, hyperparameter tuning, transfer learning)
lalu dipakai di aplikasi web Streamlit.

## Struktur

```
TK2-W8-S23-R0/
├── training/TK2_training.ipynb   notebook Colab: M1 baseline, M2 teroptimasi + tuning, M3 transfer learning
├── app/
│   ├── app.py                    aplikasi Streamlit
│   ├── requirements.txt
│   ├── model/                    isi dengan cifar10_best.keras + model_info.json dari results.zip
│   └── samples/                  (opsional) contoh gambar yang muncul di sidebar
├── test_images/                  gambar uji baru: <kelas>_<nomor>.jpg, di luar kelas: other_<nomor>.jpg
└── results/                      grafik & CSV dari results.zip
```

## 1. Siapkan gambar uji

Isi `test_images/` dengan ±20 gambar (2 per kelas) yang **bukan** dari CIFAR-10, ditambah 2–3 gambar
di luar 10 kelas. Nama file menentukan label asli:

```
airplane_1.jpg  automobile_1.jpg  bird_1.jpg  cat_1.jpg  deer_1.jpg
dog_1.jpg  frog_1.jpg  horse_1.jpg  ship_1.jpg  truck_1.jpg
other_1.jpg   ← gambar di luar 10 kelas
```

## 2. Training di Google Colab

1. Buka https://colab.research.google.com → **Upload** → pilih `training/TK2_training.ipynb`.
2. `Runtime → Change runtime type → T4 GPU`.
3. Upload folder `test_images/` (atau zip-nya sebagai `test_images.zip`) ke panel **Files**.
4. `Runtime → Run all`. Tuning adalah bagian paling lama.
5. Ambil screenshot di sel bertanda 📸 SS-01 s.d. SS-04.
6. `results.zip` terunduh otomatis di akhir.

Bila sesi Colab sering putus, ubah `USE_DRIVE = True` di sel konfigurasi agar hasil tuning tersimpan
di Google Drive dan bisa dilanjutkan.

## 3. Pasang hasil training

Ekstrak `results.zip`:

- `model/cifar10_best.keras` dan `model/model_info.json` → `app/model/`
- isi `results/` → `results/`

## 4. Jalankan aplikasi (Windows, PowerShell)

TensorFlow belum mendukung Python 3.14, dan Keras 3.13 (dipakai Colab) butuh Python ≥ 3.11,
jadi pakai **Python 3.12**. Pasang sekali bila belum ada:

```powershell
winget install Python.Python.3.12
```

Lalu:

```powershell
cd C:\Users\palmc\Downloads\tugas\TK2-W8-S23-R0\app
py -3.12 -m venv .venv
.venv\Scripts\Activate.ps1
pip install -r requirements.txt
streamlit run app.py
```

Browser terbuka di http://localhost:8501. Ambil screenshot SS-05 s.d. SS-09.

**Versi harus cocok.** Buka `app/model/model_info.json`, lihat `tensorflow_version` dan
`keras_version`, lalu samakan baris `tensorflow==` dan `keras==` di `requirements.txt` sebelum `pip install`.

Jika `Activate.ps1` ditolak PowerShell, jalankan sekali:
`Set-ExecutionPolicy -Scope CurrentUser RemoteSigned`.

## Catatan teknis

- Normalisasi piksel (dan resize ke 96×96 untuk MobileNetV2) ada **di dalam model**, jadi aplikasi
  cukup mengirim gambar 32×32 berpiksel 0–255.
- Gambar di-crop tengah menjadi persegi sebelum di-resize, sama di notebook dan aplikasi, agar
  hasil uji notebook dan aplikasi konsisten.
