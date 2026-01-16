#!/usr/bin/env bash
set -euo pipefail

#JAR="/home/NWTiS_2/ibeusan20/ibeusan20_zadaca_2/ibeusan20_zadaca_2_app/target/ibeusan20_zadaca_2.jar"
JAR="/home/NWTiS_2/ibeusan20/ibeusan20_zadaca_3/ibeusan20_zadaca_3_app/target/ibeusan20_zadaca_3.jar"

#ARGS=(--ta DZ_1_aranzmani.csv --rta DZ_1_rezervacije.csv)
#ARGS=(--ta DZ_1_aranzmani.csv --rta DZ_1_rezervacije.csv --jdr)
#ARGS=(--ta DZ_1_aranzmani.csv --rta DZ_1_rezervacije.csv --vdr)
#ARGS=(--ta DZ_1_aranzmani.csv --rta DZ_1_rezervacije.csv --jdr --vdr)
ARGS=(--ta DZ_1_aranzmani.csv --rta DZ_1_rezervacije.csv)

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

