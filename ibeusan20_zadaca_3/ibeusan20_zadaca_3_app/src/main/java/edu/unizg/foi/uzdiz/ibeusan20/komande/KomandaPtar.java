package edu.unizg.foi.uzdiz.ibeusan20.komande;

import edu.unizg.foi.uzdiz.ibeusan20.ispisi.FormatIspisaBridge;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.TablicniFormat;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljAranzmanima;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;
import edu.unizg.foi.uzdiz.ibeusan20.pretplate.OsobaPretplatnik;

public class KomandaPtar implements Komanda {

  private final UpraviteljAranzmanima ua;
  private final String[] argumenti;
  private final FormatIspisaBridge ispis = new TablicniFormat();

  public KomandaPtar(UpraviteljAranzmanima ua, String[] argumenti) {
    this.ua = ua;
    this.argumenti = argumenti;
  }

  @Override
  public boolean izvrsi() {
    if (argumenti == null || argumenti.length < 3) {
      ispis.ispisi("Neispravna komanda. Sintaksa: PTAR ime prezime oznaka");
      return true;
    }

    String ime = argumenti[0];
    String prezime = argumenti[1];
    String oznaka = argumenti[2];

    Aranzman a = ua.pronadiPoOznaci(oznaka);
    if (a == null) {
      ispis.ispisi("Aranžman '" + oznaka + "' ne postoji.");
      return true;
    }

    boolean dodano = a.dodajPretplatnika(new OsobaPretplatnik(ime, prezime));
    if (dodano) {
      ispis.ispisi("Pretplata dodana: " + ime + " " + prezime + " -> " + oznaka);
    } else {
      ispis.ispisi("Pretplata već postoji ili podaci nisu ispravni.");
    }

    return true;
  }
}
