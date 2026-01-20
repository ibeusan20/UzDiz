#!/usr/bin/env bash
set -euo pipefail

# 1) Build projekta
cd /home/NWTiS_2/ibeusan20/ibeusan20_zadaca_3
mvn clean install

# 2) Vraćanje u mapu gdje je run_file.sh
cd /home/NWTiS_2/Desktop/DZX_pokretanje

#JAR="/home/NWTiS_2/ibeusan20/ibeusan20_zadaca_2/ibeusan20_zadaca_2_app/target/ibeusan20_zadaca_2.jar"
JAR="/home/NWTiS_2/ibeusan20/ibeusan20_zadaca_3/ibeusan20_zadaca_3_app/target/ibeusan20_zadaca_3.jar"

#ARGS=(--ta DZ_1_aranzmani.csv --rta DZ_1_rezervacije_1.csv)
#ARGS=(--ta DZ_1_aranzmani.csv --rta DZ_1_rezervacije_1.csv --jdr)
#ARGS=(--ta DZ_1_aranzmani.csv --rta DZ_1_rezervacije_1.csv --vdr)
#ARGS=(--ta DZ_1_aranzmani.csv --rta DZ_1_rezervacije_1.csv --jdr --vdr)

ARGS=(--ta DZ_1_aranzmani.csv --rta DZ_1_rezervacije.csv --jdr)

INTERVAL="0.2"

#FILE="komande_dz2.txt"
FILE="komande_dz3.txt"

{
  while IFS= read -r cmd; do
    [[ -z "${cmd// /}" ]] && continue
    echo "$cmd"
    sleep "$INTERVAL"
  done < "$FILE"
} | java -jar "$JAR" "${ARGS[@]}"

