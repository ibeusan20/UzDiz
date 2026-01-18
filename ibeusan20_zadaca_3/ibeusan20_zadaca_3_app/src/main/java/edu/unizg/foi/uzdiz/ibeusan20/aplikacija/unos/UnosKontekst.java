package edu.unizg.foi.uzdiz.ibeusan20.aplikacija.unos;

import java.util.ArrayList;
import java.util.List;

final class UnosKontekst {
  String original;
  String linija;

  String naredba;
  List<String> argumenti = new ArrayList<>();

  boolean izlaz = false;
  boolean ignoriraj = false;
  String greska = null;

  UnosKontekst(String linija) {
    this.original = linija;
    this.linija = linija;
  }

  ObradeniUnos toRezultat() {
    if (greska != null) {
      return ObradeniUnos.greska(greska);
    }
    if (ignoriraj) {
      return ObradeniUnos.ignoriraj();
    }
    if (izlaz) {
      return ObradeniUnos.izlaz();
    }
    if (naredba == null || naredba.isBlank()) {
      return ObradeniUnos.ignoriraj();
    }
    return ObradeniUnos.ok(naredba, argumenti.toArray(new String[0]));
  }

  void stopGreska(String msg) {
    this.greska = msg;
  }

  void stopIgnoriraj() {
    this.ignoriraj = true;
  }

  void stopIzlaz() {
    this.izlaz = true;
  }
}
