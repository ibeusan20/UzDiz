package komande;

import java.util.List;
import ispisi.IspisRezervacijaAdapter;
import ispisi.TablicniFormat;
import logika.UpraviteljRezervacijama;
import model.Rezervacija;

/**
 * Komanda IRTA - ispis rezervacija za aranžman (s mogućim filtriranjem po statusu).
 */
public class KomandaIrta implements Komanda {
  private final UpraviteljRezervacijama upraviteljRezervacija;
  private final TablicniFormat formatIspisa = new TablicniFormat();
  private final String[] argumenti;

  /**
   * Instancira novu koamndu irta.
   *
   * @param upraviteljRezervacija the upravitelj rezervacija
   * @param argumenti the argumenti
   */
  public KomandaIrta(UpraviteljRezervacijama upraviteljRezervacija, String... argumenti) {
    this.upraviteljRezervacija = upraviteljRezervacija;
    this.argumenti = argumenti;
  }

  /**
   * Izvrsi.
   *
   * @return true, if successful
   */
  @Override
  public boolean izvrsi() {
    if (argumenti.length < 2) {
      System.out.println("Sintaksa: IRTA <oznakaAranzmana> [PA|Č|O]");
      return true;
    }

    String oznaka = argumenti[0].trim();
    String vrste = (argumenti.length >= 2) ? argumenti[1].toUpperCase() : "";

    List<Rezervacija> lista = upraviteljRezervacija.dohvatiZaAranzmanIVrste(oznaka, vrste);

    System.out.println();
    System.out.println("Pregled rezervacija za aranžman " + oznaka + ":");

    if (lista.isEmpty()) {
      System.out.println("Nema rezervacija za tražene kriterije.");
      return true;
    }

    boolean imaOtkazane = vrste.contains("O");
    formatIspisa.setIspisujeOtkazane(imaOtkazane);

    for (Rezervacija r : lista) {
      IspisRezervacijaAdapter adapter = new IspisRezervacijaAdapter(r);
      formatIspisa.ispisi(adapter);
    }
    return true;
  }
}
