package edu.unizg.foi.uzdiz.ibeusan20.komande;

import edu.unizg.foi.uzdiz.ibeusan20.ispisi.FormatIspisaBridge;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.TablicniFormat;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljAranzmanima;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;

public class KomandaUptar implements Komanda {

  private final UpraviteljAranzmanima ua;
  private final String[] argumenti;
  private final FormatIspisaBridge ispis = new TablicniFormat();

  public KomandaUptar(UpraviteljAranzmanima ua, String[] argumenti) {
    this.ua = ua;
    this.argumenti = argumenti;
  }

  @Override
  public boolean izvrsi() {
    if (argumenti == null || argumenti.length < 1) {
      ispis.ispisi("Neispravna komanda. Sintaksa: UPTAR [ime prezime oznaka] | [oznaka]");
      return true;
    }

    if (argumenti.length == 1) {
      return ukloniSve(argumenti[0]);
    }

    if (argumenti.length == 3) {
      return ukloniJednu(argumenti[0], argumenti[1], argumenti[2]);
    }

    ispis.ispisi("Neispravna komanda. Sintaksa: UPTAR [ime prezime oznaka] | [oznaka]");
    return true;
  }

  private boolean ukloniSve(String oznaka) {
    Aranzman a = ua.pronadiPoOznaci(oznaka);
    ispis.ispisi("PTAR " + oznaka);
    if (a == null) {
      ispis.ispisi("Aranžman '" + oznaka + "' ne postoji.");
      return true;
    }

    int br = a.ukloniSvePretplatnike();
    ispis.ispisi("Ukinuto pretplata za aranžman '" + oznaka + "': " + br);
    return true;
  }

  private boolean ukloniJednu(String ime, String prezime, String oznaka) {
    Aranzman a = ua.pronadiPoOznaci(oznaka);
    ispis.ispisi("PTAR " + ime + " " + prezime + " " + oznaka);
    if (a == null) {
      ispis.ispisi("Aranžman '" + oznaka + "' ne postoji.");
      return true;
    }

    boolean ok = a.ukloniPretplatnika(ime, prezime);
    if (ok) {
      ispis.ispisi("Ukinuta pretplata: " + ime + " " + prezime + " -> " + oznaka);
    } else {
      ispis.ispisi("Pretplata ne postoji: " + ime + " " + prezime + " -> " + oznaka);
    }
    return true;
  }
}
