package komande;

import ispisi.*;
import logika.*;
import model.*;
import java.util.List;

/**
 * Komanda IRO - Ispis rezervacija osobe.
 */
public class KomandaIro implements Komanda {

    private final UpraviteljRezervacijama upraviteljRezervacija;
    private final UpraviteljAranzmanima upraviteljAranzmani;
    private final FormatIspisaBridge formatIspisa = new TablicniFormat();
    private final String[] argumenti;

    public KomandaIro(UpraviteljRezervacijama upraviteljRezervacija,
                      UpraviteljAranzmanima upraviteljAranzmani,
                      String... argumenti) {
        this.upraviteljRezervacija = upraviteljRezervacija;
        this.upraviteljAranzmani = upraviteljAranzmani;
        this.argumenti = argumenti;
    }

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

        //System.out.println("------------------------------------------------------------------------------------------");
        return true;
    }
}
