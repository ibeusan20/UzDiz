package edu.unizg.foi.uzdiz.ibeusan20.ispisi;

import java.time.format.DateTimeFormatter;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;
import edu.unizg.foi.uzdiz.ibeusan20.model.Rezervacija;

/**
 * Adapter za ispis rezervacija određene osobe (IRO).
 */
public class IspisRezervacijaOsobeAdapter implements IspisniRed {

  private static final DateTimeFormatter FORMAT_DATUM =
      DateTimeFormatter.ofPattern("dd.MM.yyyy.");

  private final Rezervacija rezervacija;
  private final Aranzman aranzman;

  public IspisRezervacijaOsobeAdapter(Rezervacija rezervacija, Aranzman aranzman) {
    this.rezervacija = rezervacija;
    this.aranzman = aranzman;
  }

  @Override
  public String[] zaglavlje() {
    return new String[] {
        "Oznaka", "Naziv aranžmana", "Polazak", "Povratak", "Stanje"
    };
  }

  @Override
  public String[] vrijednosti() {
    String polazak = aranzman.getPocetniDatum() == null ? ""
        : aranzman.getPocetniDatum().format(FORMAT_DATUM);
    String povratak = aranzman.getZavrsniDatum() == null ? ""
        : aranzman.getZavrsniDatum().format(FORMAT_DATUM);

    return new String[] {
        aranzman.getOznaka(),
        aranzman.getNaziv(),
        polazak,
        povratak,
        rezervacija.nazivStanja() // STATE, tekstualno
    };
  }
}
