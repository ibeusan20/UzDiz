#!/usr/bin/env bash
set -euo pipefail

echo "zbog sigurnosti kopirajte direktorij nad kojim želite izvršiti program te priložite putanje kopiranog direktorija (npr. /home/User/Desktop)"
echo

read -r -p "Unesi putanju direktorija (npr. /home/user/Desktop): " ROOT

# Uredi unos (navodnici + trailing razmaci)
ROOT="${ROOT%\"}"; ROOT="${ROOT#\"}"
ROOT="$(echo "$ROOT" | sed 's/[[:space:]]*$//')"

if [[ -z "${ROOT}" ]]; then
  echo "[ERROR] Niste unijeli putanju."
  exit 1
fi

if [[ ! -d "${ROOT}" ]]; then
  echo "[ERROR] Direktorij ne postoji: ${ROOT}"
  exit 1
fi

ROOT="$(cd "$ROOT" && pwd)"

if [[ "${ROOT}" == "/" ]]; then
  echo "[ERROR] Odbijam raditi nad root direktorijem (/)."
  exit 1
fi

echo
echo "Odabrani direktorij: ${ROOT}"
echo "Tražim (.settings, target, .classpath, .project, .gitignore) u svim poddirektorijima..."
echo

# Pronađi mete
mapfile -d '' FOUND < <(
  find "$ROOT" \
    \( -type d -name ".settings" -o -type d -name "target" -o -type f -name ".classpath" -o -type f -name ".project" -type f -name ".gitignore" \) \
    -print0
)

if (( ${#FOUND[@]} == 0 )); then
  echo "Nema ničega za obrisati. Gotovo."
  exit 0
fi

# Dedupliciraj: ako je direktorij već u listi, preskoči sve što je unutar njega
# (sort po duljini putanje, pa filtriranje)
mapfile -t SORTED < <(printf '%s\n' "${FOUND[@]}" | awk '{ print length($0) "\t" $0 }' | sort -n | cut -f2-)

KEEP=()
KEEP_DIRS=()

is_under_kept_dir() {
  local p="$1"
  for d in "${KEEP_DIRS[@]}"; do
    [[ "$p" == "$d"/* ]] && return 0
  done
  return 1
}

for p in "${SORTED[@]}"; do
  if is_under_kept_dir "$p"; then
    continue
  fi
  KEEP+=("$p")
  if [[ -d "$p" ]]; then
    KEEP_DIRS+=("$p")
  fi
done

echo "Pronađeno za brisanje (filtrirano): ${#KEEP[@]} stavki"
echo "----------------------------------------"
for t in "${KEEP[@]}"; do
  echo "$t"
done
echo "----------------------------------------"
echo

read -r -p "Želiš li napraviti backup pa obrisati ove stavke? (DA/ne): " CONFIRM
CONFIRM="${CONFIRM:-ne}"

if [[ "$CONFIRM" != "DA" ]]; then
  echo "Prekidam. Ništa nije obrisano."
  exit 0
fi

# Pripremi relativne putanje za arhivu
REL=()
for t in "${KEEP[@]}"; do
  REL+=("${t#"$ROOT"/}")
done

TS="$(date +%Y%m%d_%H%M%S)"
BASE="$(basename "$ROOT")"
OUTDIR="$(pwd)"
ZIPFILE="${OUTDIR}/backup_cleanup_${BASE}_${TS}.zip"
TARFILE="${OUTDIR}/backup_cleanup_${BASE}_${TS}.tar.gz"

echo
echo "Radim backup odabranih stavki u: ${OUTDIR}"

if command -v zip >/dev/null 2>&1; then
  (
    cd "$ROOT"
    # -r: rekurzivno (za direktorije), -q: tiše
    zip -rq "$ZIPFILE" "${REL[@]}"
  )
  echo "Backup gotov: $ZIPFILE"
else
  (
    cd "$ROOT"
    tar -czf "$TARFILE" "${REL[@]}"
  )
  echo "ZIP nije dostupan, napravljen TAR.GZ: $TARFILE"
fi

echo
echo "Brišem..."
for t in "${KEEP[@]}"; do
  rm -rf -- "$t"
done

echo "Gotovo. Obrisano: ${#KEEP[@]} stavki."

