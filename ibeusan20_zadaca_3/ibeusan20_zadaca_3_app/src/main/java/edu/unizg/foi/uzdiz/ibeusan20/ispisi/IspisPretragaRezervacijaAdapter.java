package edu.unizg.foi.uzdiz.ibeusan20.ispisi;

import edu.unizg.foi.uzdiz.ibeusan20.datoteke.PomocnikDatum;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;
import edu.unizg.foi.uzdiz.ibeusan20.model.Rezervacija;

public class IspisPretragaRezervacijaAdapter implements IspisniRed {
  private final Rezervacija r;
  private final Aranzman a;

  public IspisPretragaRezervacijaAdapter(Rezervacija r, Aranzman a) {
    this.r = r;
    this.a = a;
  }

  @Override
  public String[] zaglavlje() {
    return new String[] {"Ime", "Prezime", "Oznaka aranžmana", "Naziv aranžmana", "Datum i vrijeme",
        "Stanje"};
  }

  @Override
  public String[] vrijednosti() {
    if (r == null) {
      return new String[] {"", "", "", "", "", ""};
    }
    String oznaka = r.getOznakaAranzmana();
    String naziv = (a == null) ? "" : a.getNaziv();
    String dv = PomocnikDatum.formatirajDatumVrijeme(r.getDatumVrijeme());

    return new String[] {r.getIme(), r.getPrezime(), oznaka, naziv, dv, r.nazivStanja()};
  }
}
