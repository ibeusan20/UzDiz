package edu.unizg.foi.uzdiz.ibeusan20.logika.ogranicenja;

import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljAranzmanima;

public class StrategijaBezOgranicenja implements StrategijaOgranicenjaRezervacija {

  @Override
  public void primijeni(UpraviteljAranzmanima upraviteljAranzmanima) {
    // Null Object: nema ograničenja, samo već postojeći min i max 
  }
}
