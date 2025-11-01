package komande;

import java.util.List;
import ispisi.FormatIspisaBridge;
import ispisi.IspisRezervacijaAdapter;
import ispisi.TablicniFormat;
import logika.UpraviteljRezervacijama;
import model.Rezervacija;

/**
 * Komanda IRTA - Ispis rezervacija za određeni aranžman.
 */
public class KomandaIrta implements Komanda {

    private final UpraviteljRezervacijama upraviteljRezervacija;
    private final FormatIspisaBridge formatIspisa = new TablicniFormat();
    private final String[] argumenti;

    public KomandaIrta(UpraviteljRezervacijama upraviteljRezervacija, String... argumenti) {
        this.upraviteljRezervacija = upraviteljRezervacija;
        this.argumenti = argumenti;
    }

    @Override
    public boolean izvrsi() {
        if (argumenti.length < 1) {
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

        for (Rezervacija r : lista) {
            IspisRezervacijaAdapter adapter = new IspisRezervacijaAdapter(r);
            formatIspisa.ispisi(adapter);
        }

        System.out.println("------------------------------------------------------------");
        return true;
    }
}
