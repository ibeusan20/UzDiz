package komande;

import logika.UpraviteljAranzmanima;
import logika.UpraviteljRezervacijama;
import model.Aranzman;
import model.PomocnikDatum;
import model.Rezervacija;
import java.time.LocalDateTime;

/**
 * Komanda DRTA - Dodavanje rezervacije za turistički aranžman.
 */
public class KomandaDrta implements Komanda {

  private final UpraviteljRezervacijama upraviteljRezervacija;
  private final UpraviteljAranzmanima upraviteljAranzmani;
  private final String[] argumenti;

  public KomandaDrta(UpraviteljRezervacijama ur, UpraviteljAranzmanima ua, String... argumenti) {
    this.upraviteljRezervacija = ur;
    this.upraviteljAranzmani = ua;
    this.argumenti = argumenti;
  }

  @Override
  public boolean izvrsi() {
    if (argumenti.length < 5) {
      System.out.println("Sintaksa: DRTA <ime> <prezime> <oznaka> <datum> <vrijeme>");
      return true;
    }

    String ime = argumenti[0].trim();
    String prezime = argumenti[1].trim();
    String oznaka = argumenti[2].trim();
    String datum = argumenti[3].trim();
    String vrijeme = argumenti[4].trim();

    // provjera postojanja aranžmana
    Aranzman a = upraviteljAranzmani.pronadiPoOznaci(oznaka);
    if (a == null) {
      System.out.println("Ne postoji aranžman s oznakom: " + oznaka);
      return true;
    }

    // provjera ispravnosti datuma i vremena
    LocalDateTime datumVrijeme = PomocnikDatum.procitajDatumIVrijeme(datum + " " + vrijeme);
    if (datumVrijeme == null) {
      System.out.println("Neispravan format datuma/vremena. Koristi dd.MM.yyyy. HH:mm:ss");
      return true;
    }

    // provjera postoji li već AKTIVNA rezervacija za tu osobu i aranžman
    if (upraviteljRezervacija.imaAktivnuZa(ime, prezime, oznaka)) {
      System.out.println("Osoba već ima AKTIVNU rezervaciju za aranžman " + oznaka + ".");
      return true;
    }

    // dodaj novu rezervaciju
    Rezervacija r = new Rezervacija(ime, prezime, oznaka, datumVrijeme);
    upraviteljRezervacija.dodaj(r);
    upraviteljRezervacija.rekalkulirajZaAranzman(oznaka, a.getMinPutnika(), a.getMaxPutnika());

    System.out.println("Dodana rezervacija za " + ime + " " + prezime + " za turistički aranžman s oznakom " + oznaka
        + " u " + PomocnikDatum.formatirajDatumVrijeme(datumVrijeme));
    return true;
  }
}
