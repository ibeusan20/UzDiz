package edu.unizg.foi.uzdiz.ibeusan20.komande;

import java.time.LocalDateTime;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.PomocnikDatum;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.FormatIspisaBridge;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.TablicniFormat;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljAranzmanima;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljRezervacijama;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;
import edu.unizg.foi.uzdiz.ibeusan20.model.Rezervacija;

/**
 * Komanda DRTA - dodavanje rezervacije za turistički aranžman.
 */
public class KomandaDrta implements Komanda {
  private final UpraviteljRezervacijama upraviteljRezervacija;
  private final UpraviteljAranzmanima upraviteljAranzmani;
  private final String[] argumenti;
  private final FormatIspisaBridge ispis = new TablicniFormat();


  /**
   * Instancira novu komandu drta.
   *
   * @param ur the ur
   * @param ua the ua
   * @param argumenti the argumenti
   */
  public KomandaDrta(UpraviteljRezervacijama ur, UpraviteljAranzmanima ua, String... argumenti) {
    this.upraviteljRezervacija = ur;
    this.upraviteljAranzmani = ua;
    this.argumenti = argumenti;
  }

  /**
   * Izvrsi.
   *
   * @return true, if successful
   */
  @Override
  public boolean izvrsi() {
    if (argumenti.length < 5) {
      ispis.ispisi("Sintaksa: DRTA <ime> <prezime> <oznaka> <datum> <vrijeme>");
      return true;
    }

    String ime = argumenti[0].trim();
    String prezime = argumenti[1].trim();
    String oznaka = argumenti[2].trim();
    String datum = argumenti[3].trim();
    String vrijeme = argumenti[4].trim();
    
    ispis.ispisi("DRTA " + ime + " " + prezime + " " + oznaka + " " + datum + " " + vrijeme);

    // provjera postojanja aranžmana
    Aranzman a = upraviteljAranzmani.pronadiPoOznaci(oznaka);
    if (a == null) {
      ispis.ispisi("Ne postoji aranžman s oznakom: " + oznaka);
      ispis.ispisi("");
      return true;
    }

    // provjera ispravnosti datuma i vremena
    LocalDateTime datumVrijeme = PomocnikDatum.procitajDatumIVrijeme(datum + " " + vrijeme);
    if (datumVrijeme == null) {
      ispis.ispisi("Neispravan format datuma/vremena. Koristi dd.MM.yyyy. HH:mm:ss");
      ispis.ispisi("");
      return true;
    }

    // provjera postoji li već aktivna rezervacija za tu osobu i aranžman
    if (upraviteljRezervacija.imaAktivnuZa(ime, prezime, oznaka)) {
      ispis.ispisi("Osoba već ima AKTIVNU rezervaciju za aranžman " + oznaka + ".");
      ispis.ispisi("");
      return true;
    }

    if (upraviteljRezervacija.imaAktivnuUPeriodu(ime, prezime, oznaka, upraviteljAranzmani)) {
      ispis.ispisi(
          "Postoji aktivna rezervacija za korisnika u tom vremenskom periodu. Rezervacija nije unesena.");
      ispis.ispisi("");
      return true;
    }

    // dodaj novu rezervaciju
    Rezervacija r = new Rezervacija(ime, prezime, oznaka, datumVrijeme);
    upraviteljRezervacija.dodaj(r);
    // upraviteljRezervacija.rekalkulirajZaAranzman(oznaka, a.getMinPutnika(), a.getMaxPutnika());
    upraviteljRezervacija.rekalkulirajSve();
    
    ispis.ispisi(
        "Dodana rezervacija za " + ime + " " + prezime + " za turistički aranžman s oznakom "
            + oznaka + " u " + PomocnikDatum.formatirajDatumVrijeme(datumVrijeme) + "\n");
    return true;
  }
}
