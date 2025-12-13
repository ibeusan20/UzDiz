package edu.unizg.foi.uzdiz.ibeusan20.komande;

import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.FormatIspisaBridge;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.IspisParAdapter;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.IspisTekstAdapter;
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

    if (a == null) {
      ispis.ispisi(new IspisTekstAdapter("Aranžman s oznakom '" + oznaka + "' nije pronađen."));
      return true;
    }

    ispis.ispisi(new IspisTekstAdapter("--- Detalji aranžmana ---"));

    String od = a.getPocetniDatum() == null ? "" : a.getPocetniDatum().format(FORMAT_DATUM);
    String d0 = a.getZavrsniDatum() == null ? "" : a.getZavrsniDatum().format(FORMAT_DATUM);
    String vk = a.getVrijemeKretanja() == null ? "" : a.getVrijemeKretanja().format(FORMAT_VRIJEME);
    String vp = a.getVrijemePovratka() == null ? "" : a.getVrijemePovratka().format(FORMAT_VRIJEME);

    String prijevoz = "";
    if (a.getPrijevoz() != null && !a.getPrijevoz().isEmpty()) {
      StringJoiner sj = new StringJoiner(", ");
      a.getPrijevoz().forEach(sj::add);
      prijevoz = sj.toString();
    }

    ispis.ispisi(new IspisParAdapter("Oznaka", a.getOznaka()));
    ispis.ispisi(new IspisParAdapter("Naziv", a.getNaziv()));
    ispis.ispisi(new IspisParAdapter("Program", a.getProgram()));
    ispis.ispisi(new IspisParAdapter("Početni datum", od));
    ispis.ispisi(new IspisParAdapter("Završni datum", d0));
    ispis.ispisi(new IspisParAdapter("Vrijeme kretanja", vk));
    ispis.ispisi(new IspisParAdapter("Vrijeme povratka", vp));
    ispis.ispisi(new IspisParAdapter("Cijena", String.format("%.2f", a.getCijena())));
    ispis.ispisi(new IspisParAdapter("Min putnika", String.valueOf(a.getMinPutnika())));
    ispis.ispisi(new IspisParAdapter("Max putnika", String.valueOf(a.getMaxPutnika())));
    ispis.ispisi(new IspisParAdapter("Broj noćenja", String.valueOf(a.getBrojNocenja())));
    ispis.ispisi(new IspisParAdapter("Doplata jednokrevetna", String.format("%.2f", a.getDoplataJednokrevetna())));
    ispis.ispisi(new IspisParAdapter("Prijevoz", prijevoz));
    ispis.ispisi(new IspisParAdapter("Doručaka", String.valueOf(a.getBrojDorucaka())));
    ispis.ispisi(new IspisParAdapter("Ručkova", String.valueOf(a.getBrojRuckova())));
    ispis.ispisi(new IspisParAdapter("Večera", String.valueOf(a.getBrojVecera())));
    ispis.ispisi(new IspisParAdapter("Stanje aranžmana", a.nazivStanja()));

    return true;
  }
}
