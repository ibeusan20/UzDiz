package edu.unizg.foi.uzdiz.ibeusan20.memento;

import java.time.LocalDateTime;
import edu.unizg.foi.uzdiz.ibeusan20.model.Rezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeAktivnaRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeNaCekanjuRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeNovaRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeOdgodenaRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeOtkazanaRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjePrimljenaRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeRezervacije;

public record RezervacijaMemento(String ime, String prezime, String oznakaAranzmana,
    LocalDateTime datumVrijeme, String stanjeNaziv, LocalDateTime datumVrijemeOtkaza) {

  public static RezervacijaMemento from(Rezervacija r) {
    if (r == null)
      return null;
    return new RezervacijaMemento(r.getIme(), r.getPrezime(), r.getOznakaAranzmana(),
        r.getDatumVrijeme(), r.nazivStanja(), r.getDatumVrijemeOtkaza());
  }

  public Rezervacija restore() {
    StanjeRezervacije s = mapirajStanje(stanjeNaziv);
    return new Rezervacija(ime, prezime, oznakaAranzmana, datumVrijeme, s, datumVrijemeOtkaza);
  }

  private static StanjeRezervacije mapirajStanje(String naziv) {
    String n = normaliziraj(naziv);

    if (n.contains("NOVA"))
      return StanjeNovaRezervacija.instanca();
    if (n.contains("PRIMLJ"))
      return StanjePrimljenaRezervacija.instanca();
    if (n.contains("AKTIV"))
      return StanjeAktivnaRezervacija.instanca();
    if (n.contains("CEKANJ") || n.contains("CEKANJU"))
      return StanjeNaCekanjuRezervacija.instanca();
    if (n.contains("ODGOD"))
      return StanjeOdgodenaRezervacija.instanca();
    if (n.contains("OTKAZ"))
      return StanjeOtkazanaRezervacija.instanca();

    // fallback: ako dođe nešto neočekivano
    return StanjePrimljenaRezervacija.instanca();
  }

  private static String normaliziraj(String s) {
    if (s == null)
      return "";
    return s.trim().toUpperCase().replace('Č', 'C').replace('Ć', 'C').replace('Đ', 'D')
        .replace('Š', 'S').replace('Ž', 'Z');
  }
}
