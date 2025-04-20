package edu.unizg.foi.nwtis.ibeusan20.vjezba_05.dnevnik.podaci;

public record KorisnikInfo(String korisnickoIme, String lozinka, long vrijemePrijavljivanja,
    long vrijemeRada) {

  public KorisnikInfo izracunajVrijemeRada(long vrijeme) {
    return new KorisnikInfo(korisnickoIme(), lozinka(), vrijemePrijavljivanja(),
        vrijeme - vrijemePrijavljivanja());
  }
}
