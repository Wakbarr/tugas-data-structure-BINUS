"""Aplikasi Streamlit klasifikasi gambar CIFAR-10 dengan model CNN.

Jalankan:  streamlit run app.py
"""
import html
import json
import time
from pathlib import Path

import altair as alt
import numpy as np
import pandas as pd
import streamlit as st
from PIL import Image

BASE_DIR = Path(__file__).parent
MODEL_PATH = BASE_DIR / "model" / "cifar10_best.keras"
INFO_PATH = BASE_DIR / "model" / "model_info.json"
SAMPLES_DIR = BASE_DIR / "samples"
IMAGE_TYPES = ["jpg", "jpeg", "png", "webp"]
LOW_CONFIDENCE = 0.50
HIGH_CONFIDENCE = 0.80

# Ikon dan nama Indonesia untuk setiap kelas CIFAR-10
CLASS_META = {
    "airplane": ("✈️", "Pesawat"), "automobile": ("🚗", "Mobil"), "bird": ("🐦", "Burung"),
    "cat": ("🐱", "Kucing"), "deer": ("🦌", "Rusa"), "dog": ("🐶", "Anjing"),
    "frog": ("🐸", "Katak"), "horse": ("🐴", "Kuda"), "ship": ("🚢", "Kapal"),
    "truck": ("🚚", "Truk"),
}
PRIMARY = "#4F46E5"

st.set_page_config(page_title="Klasifikasi CIFAR-10", page_icon="🧠", layout="wide")

st.markdown(f"""
<style>
.block-container {{ padding-top: 2rem; max-width: 1200px; }}
.hero {{
  background: linear-gradient(135deg, {PRIMARY} 0%, #7C3AED 55%, #DB2777 100%);
  border-radius: 20px; padding: 28px 32px; color: white; margin-bottom: 1.5rem;
  box-shadow: 0 10px 30px rgba(79, 70, 229, .25);
}}
.hero h1 {{ color: white; font-size: 2rem; margin: 0 0 .35rem 0; padding: 0; }}
.hero p {{ color: rgba(255,255,255,.88); margin: 0; font-size: 1.02rem; }}
.hero .chips {{ margin-top: 14px; display: flex; flex-wrap: wrap; gap: 8px; }}
.hero .chip {{
  background: rgba(255,255,255,.16); border: 1px solid rgba(255,255,255,.28);
  padding: 4px 12px; border-radius: 999px; font-size: .85rem;
}}
.pred-label {{ font-size: .8rem; letter-spacing: .08em; text-transform: uppercase; color: #6B7280; }}
.pred-main {{ display: flex; align-items: center; gap: 14px; margin: 4px 0 10px; }}
.pred-emoji {{ font-size: 2.6rem; line-height: 1; }}
.pred-name {{ font-size: 1.7rem; font-weight: 700; color: #111827; line-height: 1.1; }}
.pred-sub {{ color: #6B7280; font-size: .95rem; }}
.badge {{ display: inline-block; padding: 3px 10px; border-radius: 999px; font-size: .8rem; font-weight: 600; }}
.badge.high {{ background: #DCFCE7; color: #166534; }}
.badge.mid {{ background: #FEF3C7; color: #92400E; }}
.badge.low {{ background: #FEE2E2; color: #991B1B; }}
.meter {{ height: 12px; background: #E5E7EB; border-radius: 999px; overflow: hidden; margin: 8px 0 4px; }}
.meter > div {{ height: 100%; border-radius: 999px; }}
.meter-row {{ display: flex; justify-content: space-between; font-size: .85rem; color: #4B5563; }}
.top3 {{ margin-top: 14px; }}
.top3-row {{ display: flex; align-items: center; gap: 10px; padding: 6px 0; border-bottom: 1px dashed #E5E7EB; }}
.top3-row:last-child {{ border-bottom: none; }}
.top3-rank {{ width: 22px; height: 22px; border-radius: 50%; background: #EEF2FF; color: {PRIMARY};
  font-size: .75rem; font-weight: 700; display: flex; align-items: center; justify-content: center; }}
.top3-name {{ flex: 1; }}
.top3-pct {{ font-variant-numeric: tabular-nums; font-weight: 600; color: #374151; }}
.file-title {{ font-weight: 600; color: #374151; margin-bottom: 6px; word-break: break-all; }}
.empty {{
  text-align: center; padding: 48px 24px; border: 2px dashed #C7D2FE; border-radius: 18px;
  background: #F8FAFF; color: #4B5563;
}}
.empty .big {{ font-size: 3rem; }}
.empty h3 {{ margin: .4rem 0; color: #1F2937; }}
.steps {{ display: flex; justify-content: center; gap: 28px; flex-wrap: wrap; margin-top: 18px; }}
.step {{ max-width: 180px; font-size: .9rem; }}
.step b {{ display: block; color: {PRIMARY}; font-size: 1.4rem; }}
.class-grid {{ display: grid; grid-template-columns: 1fr 1fr; gap: 6px; }}
.class-item {{ background: #F3F4F6; border-radius: 10px; padding: 6px 8px; font-size: .85rem; }}
.footer {{ text-align: center; color: #9CA3AF; font-size: .8rem; margin-top: 2rem; }}
</style>
""", unsafe_allow_html=True)


@st.cache_resource(show_spinner="Memuat model...")
def load_model():
    """Model dimuat sekali saja, lalu dipakai ulang untuk setiap prediksi."""
    import keras  # impor di sini agar halaman tetap tampil cepat

    with open(INFO_PATH, encoding="utf-8") as f:
        info = json.load(f)
    return keras.models.load_model(MODEL_PATH), info


def to_model_input(img, size):
    """Crop tengah ke persegi lalu resize, sama persis dengan notebook training."""
    img = img.convert("RGB")
    w, h = img.size
    s = min(w, h)
    left, top = (w - s) // 2, (h - s) // 2
    img = img.crop((left, top, left + s, top + s))
    return img.resize((size, size), Image.BILINEAR)


def predict(model, small_img):
    # Normalisasi ada di dalam model, jadi cukup kirim piksel 0-255
    x = np.asarray(small_img, dtype="float32")[None, ...]
    t0 = time.perf_counter()
    probs = model.predict(x, verbose=0)[0]
    return probs, (time.perf_counter() - t0) * 1000


def label(cls):
    emoji, indo = CLASS_META.get(cls, ("🔹", cls))
    return f"{emoji} {indo}"


def confidence_level(conf):
    if conf >= HIGH_CONFIDENCE:
        return "high", "Sangat yakin", "#16A34A"
    if conf >= LOW_CONFIDENCE:
        return "mid", "Cukup yakin", "#D97706"
    return "low", "Tidak yakin", "#DC2626"


def probability_chart(classes, probs, top):
    df = pd.DataFrame({
        "kelas": [label(c) for c in classes],
        "prob": [float(p) * 100 for p in probs],
        "teratas": [c == top for c in classes],
    }).sort_values("prob", ascending=False)
    base = alt.Chart(df).encode(
        y=alt.Y("kelas:N", sort="-x", title=None, axis=alt.Axis(labelFontSize=12, labelLimit=160)),
        x=alt.X("prob:Q", title="Probabilitas (%)", scale=alt.Scale(domain=[0, 100])),
    )
    bars = base.mark_bar(cornerRadiusEnd=6, height=18).encode(
        color=alt.condition("datum.teratas", alt.value(PRIMARY), alt.value("#CBD5E1")),
        tooltip=[alt.Tooltip("kelas:N", title="Kelas"), alt.Tooltip("prob:Q", title="Probabilitas (%)", format=".2f")],
    )
    text = base.mark_text(align="left", dx=4, fontSize=11, color="#374151").encode(
        text=alt.Text("prob:Q", format=".1f"))
    return (bars + text).properties(height=310)


def show_result(model, info, img, title):
    size = info["input_size"]
    classes = info["classes"]
    small = to_model_input(img, size)
    probs, ms = predict(model, small)
    order = np.argsort(probs)[::-1]
    top, conf = classes[order[0]], float(probs[order[0]])
    level, level_text, color = confidence_level(conf)
    emoji, indo = CLASS_META.get(top, ("🔹", top))

    with st.container(border=True):
        st.markdown(f'<div class="file-title">🖼️ {html.escape(title)}</div>', unsafe_allow_html=True)
        col_img, col_pred, col_chart = st.columns([1.05, 1, 1.35], gap="large")
        with col_img:
            st.image(img, width="stretch")
            with st.expander(f"Yang dilihat model ({size}×{size} piksel)"):
                st.image(small.resize((160, 160), Image.NEAREST), width=160)
                st.caption("Gambar di-crop tengah lalu diperkecil. Detail halus hilang, "
                           "karena itu objek kecil sulit dikenali.")
        with col_pred:
            top3 = "".join(
                f'<div class="top3-row"><div class="top3-rank">{r + 1}</div>'
                f'<div class="top3-name">{label(classes[i])}</div>'
                f'<div class="top3-pct">{probs[i] * 100:.1f}%</div></div>'
                for r, i in enumerate(order[:3]))
            st.markdown(f"""
<div class="pred-label">Prediksi</div>
<div class="pred-main"><div class="pred-emoji">{emoji}</div>
  <div><div class="pred-name">{indo}</div><div class="pred-sub">{top}</div></div></div>
<span class="badge {level}">{level_text}</span>
<div class="meter"><div style="width:{conf * 100:.1f}%; background:{color};"></div></div>
<div class="meter-row"><span>Confidence</span><b>{conf * 100:.1f}%</b></div>
<div class="top3"><div class="pred-label">3 kemungkinan teratas</div>{top3}</div>
""", unsafe_allow_html=True)
            if conf < LOW_CONFIDENCE:
                st.warning("Model tidak yakin (confidence < 50%). Gambar mungkin bukan salah satu "
                           "dari 10 kelas CIFAR-10, atau objeknya kurang jelas.", icon="⚠️")
            st.caption(f"⏱️ Waktu inferensi: {ms:.0f} ms")
        with col_chart:
            st.markdown('<div class="pred-label">Probabilitas semua kelas</div>', unsafe_allow_html=True)
            st.altair_chart(probability_chart(classes, probs, top), width="stretch")

    return {"gambar": title, "prediksi": top, "label": indo,
            "confidence (%)": round(conf * 100, 2), "keyakinan": level_text}


# ---------- halaman ----------
if not MODEL_PATH.exists() or not INFO_PATH.exists():
    st.error("Model belum ada. Jalankan notebook `training/TK2_training.ipynb`, lalu salin "
             "`cifar10_best.keras` dan `model_info.json` dari `results.zip` ke folder `app/model/`.")
    st.stop()

model, info = load_model()
acc = info["test_accuracy"] * 100

st.markdown(f"""
<div class="hero">
  <h1>🧠 Klasifikasi Gambar CIFAR-10</h1>
  <p>Unggah foto, lalu model CNN menebak objeknya dari 10 kelas: hewan atau kendaraan.</p>
  <div class="chips">
    <span class="chip">🎯 Akurasi uji {acc:.2f}%</span>
    <span class="chip">🧩 {html.escape(info['model_name'])}</span>
    <span class="chip">📐 Input {info['input_size']}×{info['input_size']} px</span>
    <span class="chip">🏷️ {len(info['classes'])} kelas</span>
  </div>
</div>
""", unsafe_allow_html=True)

with st.sidebar:
    st.markdown("### 📊 Tentang Model")
    c1, c2 = st.columns(2)
    c1.metric("Akurasi uji", f"{acc:.1f}%")
    c2.metric("Input", f"{info['input_size']} px")
    st.caption(f"Model **{info['model_name']}**: CNN dengan batch normalization, dropout, "
               "data augmentation, dan hyperparameter tuning.")
    st.markdown("### 🏷️ Kelas yang dikenali")
    st.markdown('<div class="class-grid">' + "".join(
        f'<div class="class-item">{label(c)}</div>' for c in info["classes"]) + "</div>",
        unsafe_allow_html=True)
    st.markdown("### 💡 Tips")
    st.markdown("- Pakai foto dengan objek **besar dan di tengah**.\n"
                "- Model hanya mengenal 10 kelas di atas. Objek lain tetap akan ditebak "
                "sebagai salah satunya.\n"
                "- Perhatikan **confidence**: di bawah 50% berarti model ragu.")

tab_upload, tab_camera, tab_sample = st.tabs(["📁 Unggah gambar", "📷 Kamera", "🖼️ Coba contoh"])

with tab_upload:
    uploads = st.file_uploader("Pilih satu atau beberapa gambar (JPG, PNG, WEBP)", type=IMAGE_TYPES,
                               accept_multiple_files=True)

with tab_camera:
    camera = None
    if st.toggle("Aktifkan kamera"):
        camera = st.camera_input("Arahkan objek ke kamera lalu ambil foto")

with tab_sample:
    samples = sorted(p for p in SAMPLES_DIR.glob("*") if p.suffix.lower().lstrip(".") in IMAGE_TYPES)
    chosen = []
    if samples:
        names = st.pills("Klik untuk mencoba (boleh lebih dari satu)", [p.name for p in samples],
                         selection_mode="multi")
        chosen = [SAMPLES_DIR / n for n in names or []]
        cols = st.columns(min(len(samples), 6))
        for i, p in enumerate(samples):
            with cols[i % len(cols)]:
                st.image(str(p), caption=p.name, width="stretch")
    else:
        st.info("Belum ada gambar contoh. Taruh file gambar di folder `app/samples/`.")

images = [(Image.open(u), u.name) for u in uploads or []]
if camera is not None:
    images.append((Image.open(camera), "Foto kamera"))
images += [(Image.open(p), f"Contoh: {p.name}") for p in chosen]

st.write("")
if not images:
    st.markdown("""
<div class="empty">
  <div class="big">📸</div>
  <h3>Belum ada gambar</h3>
  <div>Pilih salah satu tab di atas untuk memulai.</div>
  <div class="steps">
    <div class="step"><b>1</b>Unggah foto, ambil dari kamera, atau pilih contoh</div>
    <div class="step"><b>2</b>Model CNN menganalisis gambar</div>
    <div class="step"><b>3</b>Lihat prediksi, confidence, dan probabilitas tiap kelas</div>
  </div>
</div>
""", unsafe_allow_html=True)
else:
    st.markdown(f"### 🔍 Hasil Prediksi ({len(images)} gambar)")
    rows = [show_result(model, info, img, title) for img, title in images]
    if len(rows) > 1:
        st.markdown("### 📋 Ringkasan")
        summary = pd.DataFrame(rows)
        st.dataframe(summary, hide_index=True, width="stretch", column_config={
            "confidence (%)": st.column_config.ProgressColumn(
                "confidence (%)", min_value=0, max_value=100, format="%.1f%%"),
        })
        st.download_button("⬇️ Unduh ringkasan (CSV)", summary.to_csv(index=False).encode("utf-8"),
                           file_name="hasil_prediksi.csv", mime="text/csv")

st.markdown('<div class="footer">Tugas Kelompok 2 · Artificial Intelligence · '
            'Model CNN dilatih pada dataset CIFAR-10</div>', unsafe_allow_html=True)
