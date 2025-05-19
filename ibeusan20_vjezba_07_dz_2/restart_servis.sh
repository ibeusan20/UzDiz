#!/bin/bash

cd ibeusan20_vjezba_07_dz_2_servisi

echo "[INFO] Gasi i briše stari kontejner servisa..."
docker stop servis_ibeusan20 2>/dev/null
docker rm servis_ibeusan20 2>/dev/null
docker rmi servis_ibeusan20:latest 2>/dev/null

echo "[INFO] Gradi novu sliku servisa..."
docker build -t servis_ibeusan20 -f Dockerfile .

echo "[INFO] Pokreće novi kontejner servisa..."
docker run -it -d --network=mreza_ibeusan20 --ip 20.24.5.20 \
  --name=servis_ibeusan20 --hostname=servis_ibeusan20 \
  servis_ibeusan20:latest

echo "[INFO] Logovi kontejnera servisa:"
docker logs servis_ibeusan20

cd ..
