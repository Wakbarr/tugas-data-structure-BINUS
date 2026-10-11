import os, random
from PIL import Image
import matplotlib.pyplot as plt

random.seed(42)
base = "/home/claude/cifar10imgs/train"
classes = sorted(os.listdir(base))

# 1) Grid contoh gambar: 1 baris per kelas, 5 sampel per kelas
fig, axes = plt.subplots(len(classes), 5, figsize=(8, 16))
for i, cls in enumerate(classes):
    folder = os.path.join(base, cls)
    files = os.listdir(folder)
    samples = random.sample(files, 5)
    for j, f in enumerate(samples):
        img = Image.open(os.path.join(folder, f))
        ax = axes[i, j]
        ax.imshow(img)
        ax.axis("off")
        if j == 0:
            ax.set_ylabel(cls, rotation=0, labelpad=40, fontsize=9)
    axes[i, 0].axis("on")
    axes[i, 0].set_xticks([]); axes[i, 0].set_yticks([])
    axes[i, 0].set_ylabel(cls, fontsize=9)
plt.suptitle("Contoh Gambar per Kelas — CIFAR-10 (train)", y=0.99)
plt.tight_layout()
plt.savefig("/home/claude/build/sample_grid.png", dpi=150, bbox_inches="tight")
plt.close()

# 2) Distribusi kelas (train & test)
counts_train = {cls: len(os.listdir(os.path.join(base, cls))) for cls in classes}
base_test = "/home/claude/cifar10imgs/test"
counts_test = {cls: len(os.listdir(os.path.join(base_test, cls))) for cls in classes}

fig, ax = plt.subplots(figsize=(9, 5))
x = range(len(classes))
w = 0.35
ax.bar([i - w/2 for i in x], [counts_train[c] for c in classes], width=w, label="Train")
ax.bar([i + w/2 for i in x], [counts_test[c] for c in classes], width=w, label="Test")
ax.set_xticks(list(x))
ax.set_xticklabels(classes, rotation=30, ha="right")
ax.set_ylabel("Jumlah Gambar")
ax.set_title("Distribusi Jumlah Gambar per Kelas — CIFAR-10")
ax.legend()
plt.tight_layout()
plt.savefig("/home/claude/build/class_distribution.png", dpi=150)
plt.close()

# 3) Cek ukuran & mode gambar (karakteristik dataset)
sizes = set()
modes = set()
for cls in classes[:3]:
    folder = os.path.join(base, cls)
    for f in os.listdir(folder)[:20]:
        img = Image.open(os.path.join(folder, f))
        sizes.add(img.size)
        modes.add(img.mode)

print("Ukuran gambar unik (sampel):", sizes)
print("Mode warna unik (sampel):", modes)
print("Total train:", sum(counts_train.values()))
print("Total test:", sum(counts_test.values()))
print("Jumlah kelas:", len(classes))
print(classes)
