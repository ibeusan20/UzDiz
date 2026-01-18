package edu.unizg.foi.uzdiz.ibeusan20.aplikacija.unos;

final class PrazanUnosHandler extends ApstraktniHandlerUnosa {
  @Override
  protected void obradiInterno(UnosKontekst ctx) {
    if (ctx.linija == null || ctx.linija.isBlank()) {
      ctx.stopIgnoriraj();
    }
  }
}
