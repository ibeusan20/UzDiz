package edu.unizg.foi.uzdiz.ibeusan20.aplikacija.unos;

final class ValidacijaQHandler extends ApstraktniHandlerUnosa {
  @Override
  protected void obradiInterno(UnosKontekst ctx) {
    if (ctx.naredba == null) return;

    // Ako netko upiše "Q nešto" ne smatra se isto kao "Q"
    if ("Q".equals(ctx.naredba) && !ctx.argumenti.isEmpty()) {
      ctx.stopGreska("Neispravna komanda. Sintaksa: Q");
    }
  }
}

