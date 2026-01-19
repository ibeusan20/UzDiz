package edu.unizg.foi.uzdiz.ibeusan20.aplikacija.unos;

final class KanonizirajArgumenteHandler extends ApstraktniHandlerUnosa {

  @Override
  protected void obradiInterno(UnosKontekst ctx) {
    if (ctx.naredba == null) return;

    String cmd = ctx.naredba;

    // Argument 0 je argument (A/R) za ove komande:
    if ((cmd.equals("BP") || cmd.equals("UP") || cmd.equals("PPTAR")) && ctx.argumenti.size() >= 1) {
      ctx.argumenti.set(0, ctx.argumenti.get(0).trim().toUpperCase());
    }

    // IRTA: drugi argument npr PAČO u uppercase
    if (cmd.equals("IRTA") && ctx.argumenti.size() >= 2) {
      ctx.argumenti.set(1, ctx.argumenti.get(1).trim().toUpperCase());
    }

    // ITAP / ITAK: IP N/S u uppercase
    if ((cmd.equals("ITAP") || cmd.equals("ITAK")) && ctx.argumenti.size() >= 1) {
      ctx.argumenti.set(0, ctx.argumenti.get(0).trim().toUpperCase());
    }
  }
}
