package edu.unizg.foi.uzdiz.ibeusan20.aplikacija.unos;

final class TrimIWhitespaceHandler extends ApstraktniHandlerUnosa {
  @Override
  protected void obradiInterno(UnosKontekst ctx) {
    if (ctx.linija == null) {
      ctx.stopIgnoriraj();
      return;
    }
    String s = ctx.linija.trim();
    s = s.replaceAll("\\s+", " "); // tabovi i/ili više razmaka -> jedan razmak
    ctx.linija = s;
  }
}
