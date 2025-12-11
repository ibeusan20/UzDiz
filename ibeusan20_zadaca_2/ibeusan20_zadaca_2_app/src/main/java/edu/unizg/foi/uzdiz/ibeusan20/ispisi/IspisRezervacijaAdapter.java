package edu.unizg.foi.uzdiz.ibeusan20.ispisi;

import edu.unizg.foi.uzdiz.ibeusan20.datoteke.PomocnikDatum;
import edu.unizg.foi.uzdiz.ibeusan20.model.Rezervacija;

/**
 * Adapter za ispis rezervacija jednog aranžmana (IRTA).
 */
public class IspisRezervacijaAdapter implements IspisniRed {

  private final Rezervacija rezervacija;

  public IspisRezervacijaAdapter(Rezervacija rezervacija) {
    this.rezervacija = rezervacija;
  }

  @Override
  public String[] zaglavlje() {
    return new String[] {"Ime", "Prezime", "Oznaka", "Datum/vrijeme", "Stanje"};
  }

  @Override
  public String[] vrijednosti() {
    String dv = PomocnikDatum.formatirajDatumVrijeme(rezervacija.getDatumVrijeme());

    return new String[] {
        rezervacija.getIme(),
        rezervacija.getPrezime(),
        rezervacija.getOznakaAranzmana(),
        dv,
        rezervacija.nazivStanja() // STATE, tekstualno
    };
  }
}
