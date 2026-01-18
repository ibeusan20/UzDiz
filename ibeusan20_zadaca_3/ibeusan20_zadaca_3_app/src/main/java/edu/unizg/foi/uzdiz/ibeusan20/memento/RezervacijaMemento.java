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
    String st = r == null ? "" : r.nazivStanja();
    LocalDateTime otkaz = r == null ? null : r.getDatumVrijemeOtkaza();
    return new RezervacijaMemento(r.getIme(), r.getPrezime(), r.getOznakaAranzmana(),
        r.getDatumVrijeme(), st, otkaz);
  }

  public Rezervacija restore() {
    StanjeRezervacije st = stanjeZaNaziv(stanjeNaziv);
    return new Rezervacija(ime, prezime, oznakaAranzmana, datumVrijeme, st, datumVrijemeOtkaza);
  }

  private StanjeRezervacije stanjeZaNaziv(String naziv) {
    if (naziv == null) {
      return StanjePrimljenaRezervacija.instanca();
    }

    String u = naziv.trim().toUpperCase();

    if (u.contains("NOVA")) {
      return StanjeNovaRezervacija.instanca();
    }
    if (u.contains("AKTIV")) {
      return StanjeAktivnaRezervacija.instanca();
    }
    if (u.contains("ČEKAN") || u.contains("CEKAN")) {
      return StanjeNaCekanjuRezervacija.instanca();
    }
    if (u.contains("ODGOĐ") || u.contains("ODGOD")) {
      return StanjeOdgodenaRezervacija.instanca();
    }
    if (u.contains("OTKAZ")) {
      return StanjeOtkazanaRezervacija.instanca();
    }

    return StanjePrimljenaRezervacija.instanca();
  }
}
