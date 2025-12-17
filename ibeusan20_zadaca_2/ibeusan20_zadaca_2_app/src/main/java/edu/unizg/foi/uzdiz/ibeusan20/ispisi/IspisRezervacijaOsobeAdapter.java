package edu.unizg.foi.uzdiz.ibeusan20.ispisi;

import edu.unizg.foi.uzdiz.ibeusan20.datoteke.PomocnikDatum;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;
import edu.unizg.foi.uzdiz.ibeusan20.model.Rezervacija;

// TODO: Auto-generated Javadoc
/**
 * Adapter za ispis rezervacija određene osobe (IRO).
 */
public class IspisRezervacijaOsobeAdapter implements IspisniRed {

  /** The rezervacija. */
  private final Rezervacija rezervacija;
  
  /** The aranzman. */
  private final Aranzman aranzman;

  /**
   * Instantiates a new ispis rezervacija osobe adapter.
   *
   * @param rezervacija the rezervacija
   * @param aranzman the aranzman
   */
  public IspisRezervacijaOsobeAdapter(Rezervacija rezervacija, Aranzman aranzman) {
    this.rezervacija = rezervacija;
    this.aranzman = aranzman;
  }

  /**
   * Zaglavlje.
   *
   * @return the string[]
   */
  @Override
  public String[] zaglavlje() {
    return new String[] {"Datum i vrijeme", "Oznaka", "Naziv aranžmana", "Stanje"};
  }

  /**
   * Vrijednosti.
   *
   * @return the string[]
   */
  @Override
  public String[] vrijednosti() {
    if (rezervacija == null) {
      return new String[] {"", "", "", ""};
    }

    String dv = PomocnikDatum.formatirajDatumVrijeme(rezervacija.getDatumVrijeme());
    String oznaka = rezervacija.getOznakaAranzmana();

    String naziv = (aranzman == null) ? "" : aranzman.getNaziv();

    return new String[] {
        dv,
        oznaka,
        naziv,
        rezervacija.nazivStanja()
    };
  }
}
