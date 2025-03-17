package edu.unizg.foi.nwtis.ibeusan20.vjezba_02;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;

/**
 * Klasa Vjezba_02 koja sluŽi kao primjer za korištenje biblioteke klasa
 */

public class Vjezba_02_2 {
  /**
   * Konstruktor klase vjezba_02_2
   */
  public Vjezba_02_2() {}

  /**
   * Glavna metoda koja prima argumente u rasponu 1-4
   * 
   * @param args
   */
  public static void main(String[] args) {
    if (args.length < 2 || args.length > 4) {
      System.out.println("Broj argumenata nije u rasponu 1 - 4.");
      return;
    }
    String komanda = args[0];
    switch (komanda) {
      case "L":
        izvrsiKomanduL(args);
        break;

      case "P":
        izvrsiKomanduP(args);
        break;

      case "S":
        izvrsiKomanduS(args);
        break;

      case "K":
        izvrsiKomanduK(args);
        break;

      case "D":
        izvrsiKomanduD(args);
        break;

      case "E":
        izvrsiKomanduE(args);
        break;
    }
  }

  /**
   * Izvrši komandu E.
   *
   * @param args argumenti
   */
  private static void izvrsiKomanduE(String[] args) {
    try {
      Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(args[1]);
      String k = args[2];
      var status = konfig.obrisiSvePostavke();
      System.out.println("Status: " + status);
      konfig.spremiKonfiguraciju();
    } catch (NeispravnaKonfiguracija ex) {
      Logger.getLogger(Vjezba_02_2.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  /**
   * IzvrŠi komandu D.
   *
   * @param args argumenti
   */
  private static void izvrsiKomanduD(String[] args) {
    try {
      Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(args[1]);
      String k = args[2];
      var status = konfig.obrisiPostavku(k);
      System.out.println("Status: " + status);
      konfig.spremiKonfiguraciju();
    } catch (NeispravnaKonfiguracija ex) {
      Logger.getLogger(Vjezba_02_2.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  /**
   * Izvrši komandu K.
   *
   * @param args argumenti
   */
  private static void izvrsiKomanduK(String[] args) {
    try {
      Konfiguracija konfigU = KonfiguracijaApstraktna.preuzmiKonfiguraciju(args[1]);
      Konfiguracija konfigI = KonfiguracijaApstraktna.kreirajKonfiguraciju(args[2]);
      konfigU.dajSvePostavke().keySet().stream()
          .forEach(k -> konfigI.spremiPostavku((String) k, konfigU.dajPostavku((String) k)));
      konfigI.spremiKonfiguraciju();
    } catch (NeispravnaKonfiguracija ex) {
      Logger.getLogger(Vjezba_02_2.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  /**
   * Izvrši komandu S.
   *
   * @param args argumenti
   */
  private static void izvrsiKomanduS(String[] args) {
    try {

      Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(args[1]);
      String k = args[2];
      String v = args[3];
      konfig.spremiPostavku(k, v);
      konfig.spremiKonfiguraciju();
    } catch (NeispravnaKonfiguracija ex) {
      Logger.getLogger(Vjezba_02_2.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  /**
   * Izvrši komandu P.
   *
   * @param args argumenti
   */
  private static void izvrsiKomanduP(String[] args) {
    try {
      Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(args[1]);
      String k = args[2];
      String v = konfig.dajPostavku(k);
      System.out.println("k: " + k + " v: " + v);
    } catch (NeispravnaKonfiguracija ex) {
      Logger.getLogger(Vjezba_02_2.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  /**
   * Izvrši komandu L.
   *
   * @param args argumenti
   */
  private static void izvrsiKomanduL(String[] args) {
    try {
      Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKreirajKonfiguraciju(args[1]);
      Properties postavke = konfig.dajSvePostavke();
      System.out.println("Sve postavke");
      for (Object o : postavke.keySet()) {
        String k = (String) o;
        String v = postavke.getProperty(k);
        System.out.println("k: " + k + " v: " + v);
      }
      System.out.println();
    } catch (NeispravnaKonfiguracija ex) {
      Logger.getLogger(Vjezba_02_2.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

}
