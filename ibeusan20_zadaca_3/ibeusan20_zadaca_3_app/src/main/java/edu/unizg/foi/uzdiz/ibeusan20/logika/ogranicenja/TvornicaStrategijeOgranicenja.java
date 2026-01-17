package edu.unizg.foi.uzdiz.ibeusan20.logika.ogranicenja;

public class TvornicaStrategijeOgranicenja {

  public StrategijaOgranicenjaRezervacija kreiraj(boolean jdr, boolean vdr) {
    if (jdr) {
      return new StrategijaJdr();
    }
    if (vdr) {
      return new StrategijaVdr();
    }
    return new StrategijaBezOgranicenja();
  }
}
