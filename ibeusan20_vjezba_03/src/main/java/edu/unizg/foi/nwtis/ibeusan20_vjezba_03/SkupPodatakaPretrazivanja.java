package edu.unizg.foi.nwtis.ibeusan20_vjezba_03;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SkupPodatakaPretrazivanja<T> {
  private final Queue<T> podaci;

  public SkupPodatakaPretrazivanja() {
    this.podaci = new ConcurrentLinkedQueue<>();
  }

  public Queue<T> getPodaci() {
    return podaci;
  }

  public boolean dodajPodatak(T podatak) {
    return podaci.offer(podatak);
  }
}
