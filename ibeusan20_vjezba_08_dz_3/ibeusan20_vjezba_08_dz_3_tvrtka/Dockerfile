FROM ubuntu:latest

COPY ./target/maven-jlink/default/ /usr/app/

WORKDIR /usr/app/podaci

EXPOSE 8000
EXPOSE 8001
EXPOSE 8002

CMD ["/usr/app/bin/java", "-Dfile.encoding=UTF-8", "-m", "ibeusan20_vjezba_08_dz_3_tvrtka/edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.PosluziteljTvrtka", "NWTiS_04_tvrtka.txt"]