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

  public static String skratiImenaOdDrugogRetka(String text) {
    String[] lines = text.split("\\R", -1); // hvata new line
    if (lines.length <= 1)
      return text;

    for (int i = 1; i < lines.length; i++) {
      String line = lines[i].trim();
      if (line.isEmpty())
        continue;

      // prva i druga riječ + ostatak retka
      String[] parts = line.split("\\s+", 3);
      if (parts.length < 2)
        continue;

      String initials = Character.toUpperCase(parts[0].charAt(0)) + ". "
          + Character.toUpperCase(parts[1].charAt(0)) + ".";
      String rest = (parts.length == 3) ? " " + parts[2] : "";

      // sačuva eventualni leading whitespace iz originalnog retka
      int leadingSpaces = lines[i].length() - lines[i].stripLeading().length();
      String indent = " ".repeat(Math.max(0, leadingSpaces));

      lines[i] = indent + initials + rest;
    }
    return String.join(System.lineSeparator(), lines);
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
    ispis.ispisi(skratiImenaOdDrugogRetka(promjena));
    ispis.ispisi("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
  }
}
