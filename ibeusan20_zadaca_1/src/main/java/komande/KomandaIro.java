package komande;

import java.util.List;
import ispisi.FormatIspisaBridge;
import ispisi.IspisRezervacijaOsobeAdapter;
import ispisi.TablicniFormat;
import logika.UpraviteljAranzmanima;
import logika.UpraviteljRezervacijama;
import model.Aranzman;
import model.Rezervacija;

/**
 * Komanda IRO - ispis svih rezervacija određene osobe.
 */
public class KomandaIro implements Komanda {
  private final UpraviteljRezervacijama upraviteljRezervacija;
  private final UpraviteljAranzmanima upraviteljAranzmani;
  private final FormatIspisaBridge formatIspisa = new TablicniFormat();
  private final String[] argumenti;

  /**
   * Instancira novu komandu iro.
   *
   * @param upraviteljRezervacija the upravitelj rezervacija
   * @param upraviteljAranzmani the upravitelj aranzmani
   * @param argumenti the argumenti
   */
  public KomandaIro(UpraviteljRezervacijama upraviteljRezervacija,
      UpraviteljAranzmanima upraviteljAranzmani, String... argumenti) {
    this.upraviteljRezervacija = upraviteljRezervacija;
    this.upraviteljAranzmani = upraviteljAranzmani;
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
      System.out.println("Sintaksa: IRO <ime> <prezime>");
      return true;
    }

    String ime = argumenti[0].trim();
    String prezime = argumenti[1].trim();

    List<Rezervacija> lista = upraviteljRezervacija.dohvatiZaOsobu(ime, prezime);

    System.out.println();
    System.out.println("Pregled rezervacija za osobu " + ime + " " + prezime + ":");

    if (lista.isEmpty()) {
      System.out.println("Nema rezervacija za navedenu osobu.");
      return true;
    }

    for (Rezervacija r : lista) {
      Aranzman a = upraviteljAranzmani.pronadiPoOznaci(r.getOznakaAranzmana());
      IspisRezervacijaOsobeAdapter adapter = new IspisRezervacijaOsobeAdapter(r, a);
      formatIspisa.ispisi(adapter);
    }
    return true;
  }
}
