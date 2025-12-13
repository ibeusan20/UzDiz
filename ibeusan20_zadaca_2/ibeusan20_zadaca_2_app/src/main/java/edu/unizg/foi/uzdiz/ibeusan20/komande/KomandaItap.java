package edu.unizg.foi.uzdiz.ibeusan20.komande;

import java.time.format.DateTimeFormatter;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.FormatIspisaBridge;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.IspisAranzmanDetaljnoAdapter;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.IspisTekstAdapter;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.IspisniRed;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.TablicniFormat;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljAranzmanima;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;

public class KomandaItap implements Komanda {

  private static final DateTimeFormatter FORMAT_DATUM = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
  private static final DateTimeFormatter FORMAT_VRIJEME = DateTimeFormatter.ofPattern("HH:mm");

  private final UpraviteljAranzmanima upravitelj;
  private final String[] argumenti;
  private final FormatIspisaBridge ispis = new TablicniFormat();

  public KomandaItap(UpraviteljAranzmanima upravitelj, String... argumenti) {
    this.upravitelj = upravitelj;
    this.argumenti = argumenti;
  }

  @Override
  public boolean izvrsi() {
    if (argumenti.length < 1) {
      ispis.ispisi(new IspisTekstAdapter("Nedostaje oznaka aranžmana (ITAP oznaka)."));
      return true;
    }

    String oznaka = argumenti[0].trim();
    Aranzman a = upravitelj.pronadiPoOznaci(oznaka);
    
    ispis.ispisi(new IspisTekstAdapter("ITAP " + oznaka));

    if (a == null) {
      ispis.ispisi(new IspisTekstAdapter("Aranžman s oznakom '" + oznaka + "' nije pronađen."));
      return true;
    }

    ispis.ispisi(new IspisTekstAdapter("Pregled turističkog aranžmana"));
    ispis.ispisi(IspisTekstAdapter(""));
    new IspisAranzmanDetaljnoAdapter(a).ispisiDetalje();
    ispis.ispisi(IspisTekstAdapter(""));

    return true;
  }

  private IspisniRed IspisTekstAdapter(String string) {
    // TODO Auto-generated method stub
    return null;
  }
}
