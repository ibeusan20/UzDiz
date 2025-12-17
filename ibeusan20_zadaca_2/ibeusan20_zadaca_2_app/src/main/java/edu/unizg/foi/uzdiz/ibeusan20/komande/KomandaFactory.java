package edu.unizg.foi.uzdiz.ibeusan20.komande;

import edu.unizg.foi.uzdiz.ibeusan20.komande.dekoratori.AuditKomandaDecorator;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljAranzmanima;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljRezervacijama;

/**
 * Tvornica komandi
 * <p>
 * Prima tekst unosa, parsira naziv komande i argumente te vraća odgovarajuću implementaciju
 * {@link Komanda}.
 * </p>
 * <p>
 * Sve komande osim {@link KomandaQ} se dodatno omataju u {@link AuditKomandaDecorator} kako bi se
 * izvršavanje bilježilo u audit dnevnik.
 * </p>
 */
public final class KomandaFactory {

  private KomandaFactory() {}

  public static Komanda kreiraj(String unos, UpraviteljAranzmanima ua, UpraviteljRezervacijama ur) {
    if (unos == null || unos.isBlank()) return new KomandaNepoznata(unos);

    String[] dijelovi = unos.trim().split("\\s+");
    String naredba = dijelovi[0].toUpperCase();

    String[] argumenti = new String[dijelovi.length - 1];
    if (dijelovi.length > 1) {
      System.arraycopy(dijelovi, 1, argumenti, 0, dijelovi.length - 1);
    }

    Komanda baza = switch (naredba) {
      case "ITAK" -> new KomandaItak(ua, argumenti);
      case "ITAP" -> new KomandaItap(ua, argumenti);
      case "IRTA" -> new KomandaIrta(ur, argumenti);
      case "IRO"  -> new KomandaIro(ur, ua, argumenti);
      case "ORTA" -> new KomandaOrta(ur, ua, argumenti);
      case "DRTA" -> new KomandaDrta(ur, ua, argumenti);
      case "OTA"  -> new KomandaOta(ua, ur, argumenti);
      case "IS"   -> new KomandaIp(argumenti);
      case "IP"   -> new KomandaIp(argumenti);
      case "BP"   -> new KomandaBp(ua, ur, argumenti);
      case "UP"   -> new KomandaUp(ua, ur, argumenti);
      case "ITAS" -> new KomandaItas(ua, argumenti);
      case "AUDIT" -> new KomandaAudit(argumenti);
      case "Q"    -> new KomandaQ();
      default     -> null;
    };
    if (baza == null) return new KomandaNepoznata(unos);
    if (baza instanceof KomandaQ) return baza;

    return new AuditKomandaDecorator(baza, unos);
  }
}
