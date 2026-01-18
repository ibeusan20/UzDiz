package edu.unizg.foi.uzdiz.ibeusan20.pretplate;

public class OsobaPretplatnik implements Pretplatnik {

  private final String ime;
  private final String prezime;

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
    System.out.println("Osoba: " + ime + " " + prezime);
    System.out.println("Aranžman: " + (oznakaAranzmana == null ? "" : oznakaAranzmana));
    System.out.println("Promjena: " + (opisPromjene == null ? "" : opisPromjene));
    System.out.println();
  }
}
