package edu.unizg.foi.uzdiz.ibeusan20.model;

import edu.unizg.foi.uzdiz.ibeusan20.datoteke.api.RezervacijaPodaci;


/**
 * Director za izgradnju {@link Rezervacija} objekata iz ulaznih podataka.
 * <p>
 * Služi kao centralno mjesto validacije ulaznih podataka pri kreiranju rezervacije iz izvora
 * (npr. datoteka), te kreira objekt u inicijalnom stanju (prema konstruktoru {@link Rezervacija}).
 * </p>
 */
public class RezervacijaDirector {

  public Rezervacija konstruiraj(RezervacijaPodaci p) {
    if (p == null) throw new IllegalArgumentException("Podaci rezervacije nisu definirani.");
    if (p.getIme() == null || p.getIme().isBlank())
      throw new IllegalArgumentException("Ime nije definirano.");
    if (p.getPrezime() == null || p.getPrezime().isBlank())
      throw new IllegalArgumentException("Prezime nije definirano.");
    if (p.getOznakaAranzmana() == null || p.getOznakaAranzmana().isBlank())
      throw new IllegalArgumentException("Oznaka aranžmana nije definirana.");
    if (p.getDatumVrijeme() == null)
      throw new IllegalArgumentException("Datum/vrijeme nije definirano.");

    return new Rezervacija(p.getIme(), p.getPrezime(), p.getOznakaAranzmana(), p.getDatumVrijeme());
  }
}
