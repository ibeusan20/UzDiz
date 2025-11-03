package komande;

import java.time.LocalDate;
import java.util.List;
import ispisi.FormatIspisaBridge;
import ispisi.IspisAranzmanaAdapter;
import ispisi.TablicniFormat;
import logika.UpraviteljAranzmanima;
import model.Aranzman;
import model.PomocnikDatum;

public class KomandaItak implements Komanda {

  private final UpraviteljAranzmanima upravitelj;
  private final FormatIspisaBridge formatIspisa = new TablicniFormat();

  private final String[] argumenti;

  public KomandaItak(UpraviteljAranzmanima upravitelj, String... argumenti) {
    this.upravitelj = upravitelj;
    this.argumenti = argumenti;
  }

  @Override
  public boolean izvrsi() {
    LocalDate datumOd = null;
    LocalDate datumDo = null;

    try {
      if (argumenti.length >= 2) {
        datumOd = PomocnikDatum.procitajDatum(argumenti[0]);
        datumDo = PomocnikDatum.procitajDatum(argumenti[1]);
      }
    } catch (Exception e) {
      System.out.println("Neispravan format datuma. Koristi dd.MM.yyyy.");
      return true;
    }

    List<Aranzman> lista =
        (datumOd == null) ? upravitelj.svi() : upravitelj.filtrirajPoRasponu(datumOd, datumDo);

    System.out.println();
    System.out.println("Pregled turističkih aranzmana:");

    if (lista.isEmpty()) {
      System.out.println("Nema aranzmana u zadanom razdoblju.");
    } else {
      for (Aranzman a : lista) {
        IspisAranzmanaAdapter adapter = new IspisAranzmanaAdapter(a);
        formatIspisa.ispisi(adapter);
      }
    }
    return true;
  }
}
