package edu.unizg.foi.nwtis.ibeusan20.vjezba_03;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SkupPodatakaPretrazivanja {
  private Queue<PodaciPretrazivanja> podaci = new ConcurrentLinkedQueue<>();

  public Queue<PodaciPretrazivanja> getPodaci() {
    return this.podaci;
  }

  public boolean dodajPodatak(PodaciPretrazivanja noviPodatak) {
    return this.podaci.add(noviPodatak);
  }
}
