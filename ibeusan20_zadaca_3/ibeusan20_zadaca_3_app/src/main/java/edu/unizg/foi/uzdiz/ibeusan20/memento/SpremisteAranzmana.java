package edu.unizg.foi.uzdiz.ibeusan20.memento;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public final class SpremisteAranzmana {

  private static final SpremisteAranzmana INSTANCA = new SpremisteAranzmana();

  private final Map<String, Deque<AranzmanMemento>> poOznaci = new HashMap<>();

  private SpremisteAranzmana() {}

  public static SpremisteAranzmana instanca() {
    return INSTANCA;
  }

  public synchronized void spremi(String oznaka, AranzmanMemento m) {
    if (oznaka == null || oznaka.isBlank() || m == null)
      return;
    String key = oznaka.trim().toUpperCase();
    poOznaci.computeIfAbsent(key, k -> new ArrayDeque<>()).push(m); // zadnje na vrh
  }

  public synchronized AranzmanMemento zadnji(String oznaka) {
    if (oznaka == null || oznaka.isBlank())
      return null;
    Deque<AranzmanMemento> stog = poOznaci.get(oznaka.trim().toUpperCase());
    return (stog == null || stog.isEmpty()) ? null : stog.peek();
  }
}
