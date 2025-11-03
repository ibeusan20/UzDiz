package aplikacija;

import java.util.Scanner;
import komande.Komanda;
import komande.KomandaFactory;
import logika.UpraviteljAranzmanima;
import logika.UpraviteljRezervacijama;

public class Komande {

    private final UpraviteljAranzmanima upraviteljAranzmanima;
    private final UpraviteljRezervacijama upraviteljRezervacijama;

    public Komande(UpraviteljAranzmanima ua, UpraviteljRezervacijama ur) {
        this.upraviteljAranzmanima = ua;
        this.upraviteljRezervacijama = ur;
    }

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
