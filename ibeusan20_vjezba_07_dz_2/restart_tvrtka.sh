#!/bin/bash
cd ibeusan20_vjezba_07_dz_2_tvrtka

echo "[INFO] Gasi i briše stari kontejner tvrtke..."
docker stop tvrtka_ibeusan20 2>/dev/null
docker rm tvrtka_ibeusan20 2>/dev/null
docker rmi tvrtka_ibeusan20:latest 2>/dev/null

echo "[INFO] Gradi novu sliku tvrtke..."
docker build -t tvrtka_ibeusan20 -f Dockerfile .

echo "[INFO] Pokreće novi kontejner tvrtke..."
docker run -it -d --network=mreza_ibeusan20 --ip 20.24.5.2 \
  --name=tvrtka_ibeusan20 --hostname=tvrtka_ibeusan20 \
  --mount source=svezak_ibeusan20,target=/usr/app/podaci \
  tvrtka_ibeusan20:latest

echo "[INFO] Logovi kontejnera tvrtke:"
docker logs tvrtka_ibeusan20

cd ..

