package edu.unizg.foi.uzdiz.ibeusan20.aplikacija.unos;

abstract class ApstraktniHandlerUnosa implements HandlerUnosa {
  private HandlerUnosa sljedeci;

  @Override
  public void postaviSljedeci(HandlerUnosa sljedeci) {
    this.sljedeci = sljedeci;
  }

  @Override
  public final void obradi(UnosKontekst ctx) {
    if (ctx == null)
      return;

    if (ctx.greska != null || ctx.ignoriraj || ctx.izlaz)
      return;

    obradiInterno(ctx);

    if (ctx.greska != null || ctx.ignoriraj || ctx.izlaz)
      return;

    if (sljedeci != null) {
      sljedeci.obradi(ctx);
    }
  }

  protected abstract void obradiInterno(UnosKontekst ctx);
}
