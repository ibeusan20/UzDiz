package edu.unizg.foi.uzdiz.ibeusan20.pretplate;

import edu.unizg.foi.uzdiz.ibeusan20.ispisi.FormatIspisaBridge;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.TablicniFormat;

public class OsobaPretplatnik implements Pretplatnik {

  private final String ime;
  private final String prezime;
  private final FormatIspisaBridge ispis = new TablicniFormat();

  public OsobaPretplatnik(String ime, String prezime) {
    this.ime = ime == null ? "" : ime.trim();
    this.prezime = prezime == null ? "" : prezime.trim();
  }

  @Override
  public String getIme() {
    return ime;
  }

  @Override
  public String getPrezime() {
    return prezime;
  }

  @Override
  public void obavijesti(String oznakaAranzmana, String opisPromjene) {
    String osoba = ("Osoba: " + ime + " " + prezime);
    String aranzman = ("Aranžman: " + (oznakaAranzmana == null ? "" : oznakaAranzmana));
    String promjena = ("Promjena: " + (opisPromjene == null ? "" : opisPromjene));
    String nazivTablice = "OBAVIJEST";

    ispis.ispisi("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    ispis.ispisi(nazivTablice);
    ispis.ispisi("---------------------------------------------------------");
    ispis.ispisi(osoba);
    ispis.ispisi(aranzman);
    ispis.ispisi(promjena);
    ispis.ispisi("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
  }
}
