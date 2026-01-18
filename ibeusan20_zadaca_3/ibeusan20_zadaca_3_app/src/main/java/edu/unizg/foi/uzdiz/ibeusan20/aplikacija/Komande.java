package edu.unizg.foi.uzdiz.ibeusan20.aplikacija;

import java.util.Scanner;
import edu.unizg.foi.uzdiz.ibeusan20.aplikacija.unos.LanacObradeUnosa;
import edu.unizg.foi.uzdiz.ibeusan20.aplikacija.unos.ObradeniUnos;
import edu.unizg.foi.uzdiz.ibeusan20.komande.Komanda;
import edu.unizg.foi.uzdiz.ibeusan20.komande.KomandaFactory;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljAranzmanima;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljRezervacijama;

/**
 * Klasa zadužena za interaktivni rad s korisnikom.
 * <p>
 * Čita korisničke komande, kreira odgovarajuće objekte pomoću {@link KomandaFactory} i izvršava ih
 * dok se ne unese {@code Q}.
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
    LanacObradeUnosa obradaUnosa = new LanacObradeUnosa();

    System.out.println("Sustav je spreman. Unesite komandu (Q za izlaz):");

    while (true) {
      System.out.print("> ");
      String unos = sc.nextLine(); // ne trimaj ovdje - lanac radi normalizaciju

      ObradeniUnos u = obradaUnosa.obradi(unos);

      if (u.jeIgnoriraj()) {
        continue;
      }
      if (u.getPoruka() != null && !u.getPoruka().isBlank()) {
        System.out.println(u.getPoruka());
        continue;
      }
      if (u.jeIzlaz()) {
        break;
      }

      // rekonstruiraj normalizirani unos (komanda + argumenti)
      StringBuilder sb = new StringBuilder();
      sb.append(u.getNaredba());
      for (String a : u.getArgumenti()) {
        sb.append(" ").append(a);
      }
      String normaliziraniUnos = sb.toString();

      Komanda komanda =
          KomandaFactory.kreiraj(normaliziraniUnos, upraviteljAranzmanima, upraviteljRezervacijama);

      boolean nastavi = komanda.izvrsi();
      if (!nastavi) {
        break;
      }
    }
    sc.close();
  }
}
