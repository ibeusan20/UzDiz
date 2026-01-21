package edu.unizg.foi.uzdiz.ibeusan20.aplikacija.unos;

final class TokenizacijaHandler extends ApstraktniHandlerUnosa {
  @Override
  protected void obradiInterno(UnosKontekst ctx) {
    String s = ctx.linija;
    if (s == null || s.isBlank()) {
      ctx.stopIgnoriraj();
      return;
    }

    String[] tok = s.split(" ");
    if (tok.length == 0) {
      ctx.stopIgnoriraj();
      return;
    }

    ctx.naredba = tok[0];
    ctx.argumenti.clear();
    for (int i = 1; i < tok.length; i++) {
      if (!tok[i].isBlank()) {
        ctx.argumenti.add(tok[i]);
      }
    }
  }
}
