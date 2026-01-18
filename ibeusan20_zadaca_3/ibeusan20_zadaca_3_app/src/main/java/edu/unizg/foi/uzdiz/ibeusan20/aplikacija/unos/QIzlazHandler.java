package edu.unizg.foi.uzdiz.ibeusan20.aplikacija.unos;

final class QIzlazHandler extends ApstraktniHandlerUnosa {
  @Override
  protected void obradiInterno(UnosKontekst ctx) {
    if (ctx.linija == null) return;
    // izlaz samo ako je baš "Q" ili "q" (bez argumenata)
    if (ctx.linija.equalsIgnoreCase("q")) {
      ctx.stopIzlaz();
    }
  }
}
