#!/bin/bash

cd ibeusan20_vjezba_07_dz_2_partner

echo "[INFO] Gasi i briše stari kontejner partnera..."
docker stop partner_ibeusan20 2>/dev/null
docker rm partner_ibeusan20 2>/dev/null
docker rmi partner_ibeusan20:latest 2>/dev/null

echo "[INFO] Gradi novu sliku partnera..."
docker build -t partner_ibeusan20 -f Dockerfile .

echo "[INFO] Pokreće novi kontejner partnera..."
docker run -it -d --network=mreza_ibeusan20 --ip 20.24.5.3 \
  --name=partner_ibeusan20 --hostname=partner_ibeusan20 \
  --mount source=svezak_ibeusan20,target=/usr/app/podaci \
  partner_ibeusan20:latest

echo "[INFO] Logovi kontejnera partnera:"
docker logs partner_ibeusan20

cd ..
