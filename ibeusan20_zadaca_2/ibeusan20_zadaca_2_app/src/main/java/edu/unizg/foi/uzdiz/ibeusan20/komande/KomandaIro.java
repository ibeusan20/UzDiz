package edu.unizg.foi.uzdiz.ibeusan20.komande;

import java.util.ArrayList;
import java.util.List;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.FormatIspisaBridge;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.IspisRezervacijaOsobeAdapter;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.IspisniRed;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.TablicniFormat;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljAranzmanima;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljRezervacijama;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;
import edu.unizg.foi.uzdiz.ibeusan20.model.Rezervacija;

// TODO: Auto-generated Javadoc
/**
 * Komanda IRO - ispis svih rezervacija određene osobe.
 */
public class KomandaIro implements Komanda {
  
  /** The upravitelj rezervacija. */
  private final UpraviteljRezervacijama upraviteljRezervacija;
  
  /** The upravitelj aranzmani. */
  private final UpraviteljAranzmanima upraviteljAranzmani;
  
  /** The argumenti. */
  private final String[] argumenti;
  
  /** The ispis. */
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
      ispis.ispisi("Sintaksa: IRO <ime> <prezime>");
      return true;
    }

    String ime = argumenti[0].trim();
    String prezime = argumenti[1].trim();

    String komandaTekst = "IRO " + ime + " " + prezime;
    String nazivTablice = "Rezervacije za osobu " + ime + " " + prezime;

    List<Rezervacija> lista = upraviteljRezervacija.dohvatiZaOsobu(ime, prezime);

    List<IspisniRed> redovi = new ArrayList<>();
    for (Rezervacija r : lista) {
      Aranzman a = upraviteljAranzmani.pronadiPoOznaci(r.getOznakaAranzmana());
      redovi.add(new IspisRezervacijaOsobeAdapter(r, a));
    }

    TablicniFormat tab = new TablicniFormat();
    tab.ispisiTablicu(komandaTekst, nazivTablice, redovi);

    if (lista.isEmpty()) {
      ispis.ispisi("Nema rezervacija za navedenu osobu.");
      ispis.ispisi("");
    }

    return true;
  }
}
