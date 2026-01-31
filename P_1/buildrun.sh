#!/usr/bin/env bash
set -euo pipefail

# 1) Build projekta
#cd /home/NWTiS_2/ibeusan20/procjene/dblazevic21_zadaca_3
#cd /home/NWTiS_2/ibeusan20/ibeusan20_zadaca_3
cd /home/NWTiS_2/ibeusan20/procjene/ibeusan20_zadaca_3
mvn clean install

# 2) Vraćanje u mapu gdje je run_file.sh
cd /home/NWTiS_2/ibeusan20/P_1

#JAR="/home/NWTiS_2/ibeusan20/ibeusan20_zadaca_3/ibeusan20_zadaca_3_app/target/ibeusan20_zadaca_3.jar"
#JAR="/home/NWTiS_2/ibeusan20/procjene/dblazevic21_zadaca_3/dblazevic21_zadaca_3_app/target/dblazevic21_zadaca_3_app.jar"
#JAR="/opt/m2/edu/unizg/uzdiz/svinko21/svinko21_zadaca_3_app/2.0.0/svinko21_zadaca_3_app-2.0.0.jar"
#JAR="/home/NWTiS_2/ibeusan20/procjene/djosipovi21_zadaca_3/djosipovi21_zadaca_3_app/target/djosipovi21_zadaca_3_app.jar"
#JAR="/opt/m2/edu/unizg/foi/uzdiz/mkir21/mkir21_zadaca_3_app/2.0.0/mkir21_zadaca_3_app-2.0.0.jar"
#JAR="/opt/m2/edu/unizg/foi/uzdiz/mritosa20/mritosa20_zadaca_3_app/2.0.0/mritosa20_zadaca_3_app-2.0.0.jar"
JAR="/home/NWTiS_2/ibeusan20/procjene/ibeusan20_zadaca_3/ibeusan20_zadaca_3_app/target/ibeusan20_zadaca_3.jar"


#ARGS=(--ta DZ_1_aranzmani.csv --rta DZ_1_rezervacije_1.csv)
#ARGS=(--ta DZ_1_aranzmani.csv --rta DZ_1_rezervacije_1.csv --jdr)
#ARGS=(--ta DZ_1_aranzmani.csv --rta DZ_1_rezervacije_1.csv --vdr)
#ARGS=(--ta DZ_1_aranzmani.csv --rta DZ_1_rezervacije_1.csv --jdr --vdr)
ARGS=(--ta DZ_3_aranzmani_3.csv --rta DZ_3_rezervacije_3.csv --jdr)

INTERVAL="0.2"

#FILE="komande_dz2.txt"
#FILE="kom.txt"
FILE="kom3.txt"

{
  while IFS= read -r cmd; do
    [[ -z "${cmd// /}" ]] && continue
    echo "$cmd"
    sleep "$INTERVAL"
  done < "$FILE"
} | java -jar "$JAR" "${ARGS[@]}"

