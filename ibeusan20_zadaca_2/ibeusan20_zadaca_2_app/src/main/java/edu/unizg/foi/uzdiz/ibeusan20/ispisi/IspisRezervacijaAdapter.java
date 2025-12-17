package edu.unizg.foi.uzdiz.ibeusan20.ispisi;

import edu.unizg.foi.uzdiz.ibeusan20.datoteke.PomocnikDatum;
import edu.unizg.foi.uzdiz.ibeusan20.model.Rezervacija;

// TODO: Auto-generated Javadoc
/**
 * Adapter za ispis rezervacija jednog aranžmana (IRTA).
 */
public class IspisRezervacijaAdapter implements IspisniRed {

  /** The rezervacija. */
  private final Rezervacija rezervacija;

  /**
   * Instantiates a new ispis rezervacija adapter.
   *
   * @param rezervacija the rezervacija
   */
  public IspisRezervacijaAdapter(Rezervacija rezervacija) {
    this.rezervacija = rezervacija;
  }

  /**
   * Zaglavlje.
   *
   * @return the string[]
   */
  @Override
  public String[] zaglavlje() {
    return new String[] {"Ime", "Prezime", "Datum i vrijeme", "Stanje", "Datum/vrijeme otkaza"};
  }

  /**
   * Vrijednosti.
   *
   * @return the string[]
   */
  @Override
  public String[] vrijednosti() {
    if (rezervacija == null) {
      return new String[] {"", "", "", "", ""};
    }

    String dv = PomocnikDatum.formatirajDatumVrijeme(rezervacija.getDatumVrijeme());
    String dvo = rezervacija.getDatumVrijemeOtkaza() == null
        ? ""
        : PomocnikDatum.formatirajDatumVrijeme(rezervacija.getDatumVrijemeOtkaza());

    return new String[] {
        rezervacija.getIme(),
        rezervacija.getPrezime(),
        dv,
        rezervacija.nazivStanja(),
        dvo
    };
  }
}
