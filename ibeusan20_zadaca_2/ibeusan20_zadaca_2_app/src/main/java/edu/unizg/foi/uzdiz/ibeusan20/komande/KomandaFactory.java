package edu.unizg.foi.uzdiz.ibeusan20.komande;

import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljAranzmanima;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljRezervacijama;

public final class KomandaFactory {

  private KomandaFactory() {}

  public static Komanda kreiraj(String unos, UpraviteljAranzmanima ua, UpraviteljRezervacijama ur) {
    if (unos == null || unos.isBlank()) return null;

    String[] dijelovi = unos.trim().split("\\s+");
    String naredba = dijelovi[0].toUpperCase();

    String[] argumenti = new String[dijelovi.length - 1];
    if (dijelovi.length > 1) {
      System.arraycopy(dijelovi, 1, argumenti, 0, dijelovi.length - 1);
    }

    return switch (naredba) {
      case "ITAK" -> new KomandaItak(ua, argumenti);
      case "ITAP" -> new KomandaItap(ua, argumenti);
      case "IRTA" -> new KomandaIrta(ur, argumenti);
      case "IRO"  -> new KomandaIro(ur, ua, argumenti);
      case "ORTA" -> new KomandaOrta(ur, ua, argumenti);
      case "DRTA" -> new KomandaDrta(ur, ua, argumenti);
      case "OTA"  -> new KomandaOta(ua, ur, argumenti);

      case "IS"   -> new KomandaIp(argumenti); //opis zadaće potencijalno kriv?....
      case "IP"   -> new KomandaIp(argumenti);

      case "BP"   -> new KomandaBp(ua, ur, argumenti);
      case "UP"   -> new KomandaUp(ua, ur, argumenti);
      case "ITAS" -> new KomandaItas(ua, argumenti);

      case "Q"    -> new KomandaQ();
      default     -> null;
    };
  }
}
