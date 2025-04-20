package edu.unizg.foi.nwtis.ibeusan20.vjezba_05.dnevnik;

import java.util.List;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_05.dnevnik.podaci.DnevnikRada;

public interface ServisDnevnikRada {
  boolean koristiDatoteku();

  boolean koristiBazuPodataka();

  boolean pripremiResurs() throws Exception;

  boolean otpustiResurs() throws Exception;

  boolean upisiDnevnik(DnevnikRada dnevnikRada) throws Exception;

  List<DnevnikRada> dohvatiDnevnik(long vrijemeOd, long vrijemeDo, String korisnickoIme)
      throws Exception;
}
