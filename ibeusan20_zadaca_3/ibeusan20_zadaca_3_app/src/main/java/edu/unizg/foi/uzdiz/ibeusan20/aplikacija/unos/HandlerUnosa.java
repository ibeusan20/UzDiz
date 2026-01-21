package edu.unizg.foi.uzdiz.ibeusan20.aplikacija.unos;

interface HandlerUnosa {
  void postaviSljedeci(HandlerUnosa sljedeci);

  void obradi(UnosKontekst ctx);
}
