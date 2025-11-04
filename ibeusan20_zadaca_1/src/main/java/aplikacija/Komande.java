package aplikacija;

import java.util.Scanner;
import komande.Komanda;
import komande.KomandaFactory;
import logika.UpraviteljAranzmanima;
import logika.UpraviteljRezervacijama;

/**
 * Klasa zadužena za interaktivni rad s korisnikom.
 * <p>
 * Čita korisničke komande, kreira odgovarajuće objekte pomoću
 * {@link KomandaFactory} i izvršava ih dok se ne unese {@code Q}.
 * </p>
 */
public class Komande {

  private final UpraviteljAranzmanima upraviteljAranzmanima;
  private final UpraviteljRezervacijama upraviteljRezervacijama;

  /**
   * Inicijalizira objekt s upraviteljima aranžmana i rezervacija..
   *
   * @param ua UpraviteljAranzmanima
   * @param ur UpraviteljRezervacijama
   */
  public Komande(UpraviteljAranzmanima ua, UpraviteljRezervacijama ur) {
    this.upraviteljAranzmanima = ua;
    this.upraviteljRezervacijama = ur;
  }

  /**
   * Pokreće čitanje i izvršavanje komandi.
   */
  public void pokreni() {
    Scanner sc = new Scanner(System.in);
    System.out.println("Sustav je spreman. Unesite komandu (Q za izlaz):");

    while (true) {
      System.out.print("> ");
      String unos = sc.nextLine().trim();

      if (unos.isEmpty()) {
        continue;
      }

      Komanda komanda =
          KomandaFactory.kreiraj(unos, upraviteljAranzmanima, upraviteljRezervacijama);

      if (komanda == null) {
        System.out.println("Nepoznata komanda: " + unos);
        continue;
      }

      boolean nastavi = komanda.izvrsi();
      if (!nastavi) {
        break;
      }
    }
    sc.close();
  }
}
