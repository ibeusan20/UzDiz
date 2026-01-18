package edu.unizg.foi.uzdiz.ibeusan20.aplikacija.unos;

final class KanonizirajKomanduHandler extends ApstraktniHandlerUnosa {
  @Override
  protected void obradiInterno(UnosKontekst ctx) {
    if (ctx.naredba == null) {
      ctx.stopIgnoriraj();
      return;
    }
    ctx.naredba = ctx.naredba.trim().toUpperCase();
  }
}
