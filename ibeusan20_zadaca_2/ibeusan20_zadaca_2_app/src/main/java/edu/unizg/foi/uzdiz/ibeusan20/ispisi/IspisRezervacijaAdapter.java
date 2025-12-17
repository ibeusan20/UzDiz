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
    return new String[] {"Ime", "Prezime", "Datum i vrijeme", "Stanje", "Datum/vrijeme otkaza"};
  }

  @Override
  public String[] vrijednosti() {
    if (rezervacija == null) {
      return new String[] {"", "", "", "", ""};
    }

    String dv = PomocnikDatum.formatirajDatumVrijeme(rezervacija.getDatumVrijeme());
    String dvo = rezervacija.getDatumVrijemeOtkaza() == null ? ""
        : PomocnikDatum.formatirajDatumVrijeme(rezervacija.getDatumVrijemeOtkaza());

    return new String[] {rezervacija.getIme(), rezervacija.getPrezime(), dv,
        rezervacija.nazivStanja(), dvo};
  }
}
