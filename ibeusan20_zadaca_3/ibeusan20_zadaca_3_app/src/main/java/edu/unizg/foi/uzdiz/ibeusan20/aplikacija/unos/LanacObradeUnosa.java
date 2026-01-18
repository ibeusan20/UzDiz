package edu.unizg.foi.uzdiz.ibeusan20.aplikacija.unos;

public final class LanacObradeUnosa {

  private final HandlerUnosa prvi;

  public LanacObradeUnosa() {
    HandlerUnosa h1 = new TrimIWhitespaceHandler();
    HandlerUnosa h2 = new PrazanUnosHandler();
    HandlerUnosa h3 = new QIzlazHandler();
    HandlerUnosa h4 = new TokenizacijaHandler();
    HandlerUnosa h5 = new KanonizirajKomanduHandler();
    HandlerUnosa h6 = new ValidacijaQHandler();
    HandlerUnosa h7 = new KanonizirajArgumenteHandler();

    h1.postaviSljedeci(h2);
    h2.postaviSljedeci(h3);
    h3.postaviSljedeci(h4);
    h4.postaviSljedeci(h5);
    h5.postaviSljedeci(h6);
    h6.postaviSljedeci(h7);

    this.prvi = h1;
  }

  public ObradeniUnos obradi(String linija) {
    UnosKontekst ctx = new UnosKontekst(linija);
    prvi.obradi(ctx);
    return ctx.toRezultat();
  }
}
