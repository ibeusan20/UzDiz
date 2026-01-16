import os
import re

# Regex za prepoznavanje početka metode
method_start = re.compile(r'^\s*(public|private|protected)?\s*(static\s+)?[\w<>\[\]]+\s+\w+\s*\([^)]*\)\s*\{?\s*$')

def provjeri_datoteku(putanja):
    with open(putanja, encoding='utf-8', errors='ignore') as f:
        linije = f.readlines()

    preduge_linije = []
    viselinijske_instrukcije = []
    metode = []
    u_metodi = False
    metoda_brojac = 0
    stvarne_linije = 0
    ime_metode = ""

    for i, linija in enumerate(linije, start=1):
        s = linija.strip()

        # provjera duljine linije
        if len(linija.rstrip('\n')) > 120:
            preduge_linije.append((i, len(linija.rstrip('\n'))))

        # više od jedne instrukcije u liniji
        if s.count(';') > 1:
            viselinijske_instrukcije.append(i)

        # detekcija početka metode
        if not u_metodi and method_start.match(s):
            u_metodi = True
            ime_metode = s
            metoda_brojac = i
            stvarne_linije = 0
            continue

        # praćenje tijela metode
        if u_metodi:
            if s == "" or s == "{" or s == "}" or s.startswith("//") or s.startswith("/*") or s.startswith("*"):
                pass
            else:
                stvarne_linije += 1

            if s.endswith("}"):
                # kraj metode
                if stvarne_linije > 30:
                    metode.append((metoda_brojac, ime_metode, stvarne_linije))
                u_metodi = False

    return preduge_linije, viselinijske_instrukcije, metode


def provjeri_direktorij(putanja):
    for root, _, files in os.walk(putanja):
        for file in files:
            if file.endswith(".java"):
                put = os.path.join(root, file)
                preduge, vise_instr, metode = provjeri_datoteku(put)

                if not preduge and not vise_instr and not metode:
                    continue

                print(f"\n📄 {put}")
                if preduge:
                    for linija, duljina in preduge:
                        print(f"  ⚠️ Linija {linija} ima {duljina} znakova (>120)")
                if vise_instr:
                    for linija in vise_instr:
                        print(f"  ⚠️ Linija {linija} sadrži više od jedne instrukcije")
                if metode:
                    for linija, naziv, broj in metode:
                        print(f"  ⚠️ Metoda (linija {linija}) ima {broj} linija koda (>30)")


if __name__ == "__main__":
    direktorij = input("Unesi put do direktorija s kodom: ").strip()
    if not os.path.isdir(direktorij):
        print("❌ Nije pronađen direktorij.")
    else:
        provjeri_direktorij(direktorij)

