package edu.unizg.foi.uzdiz.ibeusan20.ispisi;

import edu.unizg.foi.uzdiz.ibeusan20.datoteke.PomocnikDatum;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;
import edu.unizg.foi.uzdiz.ibeusan20.model.Rezervacija;

/**
 * Adapter za ispis rezervacija određene osobe (IRO).
 */
public class IspisRezervacijaOsobeAdapter implements IspisniRed {

  private final Rezervacija rezervacija;
  private final Aranzman aranzman;

  public IspisRezervacijaOsobeAdapter(Rezervacija rezervacija, Aranzman aranzman) {
    this.rezervacija = rezervacija;
    this.aranzman = aranzman;
  }

  @Override
  public String[] zaglavlje() {
    return new String[] {"Datum i vrijeme", "Oznaka", "Naziv aranžmana", "Stanje"};
  }

  @Override
  public String[] vrijednosti() {
    if (rezervacija == null) {
      return new String[] {"", "", "", ""};
    }

    String dv = PomocnikDatum.formatirajDatumVrijeme(rezervacija.getDatumVrijeme());
    String oznaka = rezervacija.getOznakaAranzmana();

    String naziv = (aranzman == null) ? "" : aranzman.getNaziv();

    return new String[] {dv, oznaka, naziv, rezervacija.nazivStanja()};
  }
}
