package komande;

import logika.UpraviteljRezervacijama;

/**
 * Komanda ORTA - Otkaz rezervacije turističkog aranžmana.
 */
public class KomandaOrta implements Komanda {

    private final UpraviteljRezervacijama upraviteljRezervacija;
    private final String[] argumenti;

    public KomandaOrta(UpraviteljRezervacijama upraviteljRezervacija, String... argumenti) {
        this.upraviteljRezervacija = upraviteljRezervacija;
        this.argumenti = argumenti;
    }

    @Override
    public boolean izvrsi() {
        if (argumenti.length < 3) {
            System.out.println("Sintaksa: ORTA <ime> <prezime> <oznakaAranzmana>");
            return true;
        }

        String ime = argumenti[0].trim();
        String prezime = argumenti[1].trim();
        String oznaka = argumenti[2].trim();

        boolean uspjeh = upraviteljRezervacija.otkaziRezervaciju(ime, prezime, oznaka);

        if (uspjeh) {
            System.out.println("Rezervacija za " + ime + " " + prezime +
                    " (aranžman " + oznaka + ") uspješno otkazana.");
        } else {
            System.out.println("Rezervacija nije pronađena ili je već otkazana.");
        }

        return true;
    }
}
