#!/bin/bash
echo "[INFO] Restart svih kontejnera (tvrtka, partner, servis)..."
./restart_tvrtka.sh
./restart_partner.sh
./restart_servis.sh

