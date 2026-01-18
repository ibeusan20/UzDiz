package edu.unizg.foi.uzdiz.ibeusan20.aplikacija.unos;

final class QIzlazHandler extends ApstraktniHandlerUnosa {
  @Override
  protected void obradiInterno(UnosKontekst ctx) {
    if (ctx.linija == null)
      return;
    if (ctx.linija.equalsIgnoreCase("q") || ctx.linija.equalsIgnoreCase("quit")
        || ctx.linija.equalsIgnoreCase("exit")) {
      ctx.stopIzlaz();
    }
  }
}
