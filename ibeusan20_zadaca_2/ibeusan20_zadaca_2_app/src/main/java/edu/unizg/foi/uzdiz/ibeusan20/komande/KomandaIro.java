package edu.unizg.foi.uzdiz.ibeusan20.komande;

import java.util.List;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.FormatIspisaBridge;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.IspisRezervacijaOsobeAdapter;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.IspisTekstAdapter;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.TablicniFormat;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljAranzmanima;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljRezervacijama;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;
import edu.unizg.foi.uzdiz.ibeusan20.model.Rezervacija;

/**
 * Komanda IRO - ispis svih rezervacija određene osobe.
 */
public class KomandaIro implements Komanda {
  private final UpraviteljRezervacijama upraviteljRezervacija;
  private final UpraviteljAranzmanima upraviteljAranzmani;
  private final FormatIspisaBridge formatIspisa = new TablicniFormat();
  private final String[] argumenti;
  private final FormatIspisaBridge ispis = new TablicniFormat();


  /**
   * Instancira novu komandu iro.
   *
   * @param upraviteljRezervacija the upravitelj rezervacija
   * @param upraviteljAranzmani the upravitelj aranzmani
   * @param argumenti the argumenti
   */
  public KomandaIro(UpraviteljRezervacijama upraviteljRezervacija,
      UpraviteljAranzmanima upraviteljAranzmani, String... argumenti) {
    this.upraviteljRezervacija = upraviteljRezervacija;
    this.upraviteljAranzmani = upraviteljAranzmani;
    this.argumenti = argumenti;
  }

  /**
   * Izvrsi.
   *
   * @return true, if successful
   */
  @Override
  public boolean izvrsi() {
    if (argumenti.length < 2) {
      ispis.ispisi(new IspisTekstAdapter("Sintaksa: IRO <ime> <prezime>"));
      return true;
    }

    String ime = argumenti[0].trim();
    String prezime = argumenti[1].trim();

    List<Rezervacija> lista = upraviteljRezervacija.dohvatiZaOsobu(ime, prezime);

    ispis.ispisi(new IspisTekstAdapter(""));
    ispis.ispisi(new IspisTekstAdapter("Pregled rezervacija za osobu " + ime + " " + prezime + ":"));

    if (lista.isEmpty()) {
      ispis.ispisi(new IspisTekstAdapter("Nema rezervacija za navedenu osobu."));
      return true;
    }

    for (Rezervacija r : lista) {
      Aranzman a = upraviteljAranzmani.pronadiPoOznaci(r.getOznakaAranzmana());
      IspisRezervacijaOsobeAdapter adapter = new IspisRezervacijaOsobeAdapter(r, a);
      formatIspisa.ispisi(adapter);
    }
    return true;
  }
}
