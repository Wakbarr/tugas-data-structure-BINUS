"""
Constraint Satisfaction Problem (CSP): Penjadwalan 4 Meeting
==============================================================
Variables   : Meeting A, Meeting B, Meeting C, Meeting D
Domain      : {09.00, 10.00, 11.00}  untuk setiap meeting
Constraints :
    1. Meeting A != Meeting B
    2. Meeting A != Meeting C
    3. Meeting B != Meeting D
    4. Meeting C harus sebelum Meeting D  (waktu C < waktu D)

Pendekatan: Backtracking Search (algoritma CSP klasik), lengkap
dengan constraint checking di setiap langkah penugasan nilai.
"""

# ---------------------------------------------------------
# 1. Definisi variabel dan domain
# ---------------------------------------------------------
VARIABLES = ["A", "B", "C", "D"]
DOMAIN = [9, 10, 11]  # jam dalam format 24 jam: 09.00, 10.00, 11.00


# ---------------------------------------------------------
# 2. Definisi constraint
#    Setiap constraint berupa fungsi yang menerima assignment
#    (dict sebagian/lengkap) dan mengembalikan True/False.
# ---------------------------------------------------------
def constraints_ok(assignment):
    a = assignment.get("A")
    b = assignment.get("B")
    c = assignment.get("C")
    d = assignment.get("D")

    # 1. Meeting A != Meeting B
    if a is not None and b is not None and a == b:
        return False

    # 2. Meeting A != Meeting C
    if a is not None and c is not None and a == c:
        return False

    # 3. Meeting B != Meeting D
    if b is not None and d is not None and b == d:
        return False

    # 4. Meeting C harus sebelum Meeting D  -> waktu C < waktu D
    if c is not None and d is not None and not (c < d):
        return False

    return True


# ---------------------------------------------------------
# 3. Algoritma Backtracking Search
# ---------------------------------------------------------
def backtracking_search(assignment, variables, domain, solutions):
    # Basis: semua variabel sudah terisi -> assignment lengkap ditemukan
    if len(assignment) == len(variables):
        solutions.append(assignment.copy())
        return

    # Pilih variabel berikutnya yang belum diberi nilai
    unassigned = [v for v in variables if v not in assignment]
    var = unassigned[0]

    # Coba setiap nilai pada domain (waktu 09.00 / 10.00 / 11.00)
    for value in domain:
        assignment[var] = value

        # Cek apakah assignment sejauh ini masih konsisten dengan constraint
        if constraints_ok(assignment):
            backtracking_search(assignment, variables, domain, solutions)

        # Backtrack: hapus nilai jika tidak mengarah ke solusi (atau sudah dieksplor)
        del assignment[var]


# ---------------------------------------------------------
# 4. Menjalankan pencarian & menampilkan hasil
# ---------------------------------------------------------
def format_jam(jam):
    return f"{jam:02d}.00"


def main():
    solutions = []
    backtracking_search({}, VARIABLES, DOMAIN, solutions)

    print(f"Total solusi valid yang ditemukan: {len(solutions)}\n")

    for i, sol in enumerate(solutions, start=1):
        print(f"Solusi {i}:")
        for meeting in VARIABLES:
            print(f"  Meeting {meeting} -> {format_jam(sol[meeting])}")
        print()

    if solutions:
        print("Contoh jadwal yang bisa dipakai (solusi pertama):")
        contoh = solutions[0]
        for meeting in VARIABLES:
            print(f"  Meeting {meeting}: {format_jam(contoh[meeting])}")
    else:
        print("Tidak ada solusi yang memenuhi semua constraint.")


if __name__ == "__main__":
    main()
