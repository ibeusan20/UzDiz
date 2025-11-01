package datoteke;

import model.Aranzman;
import model.Rezervacija;
/**
 * Factory Method uzorak za stvaranje čitača datoteka.
 * Kreira tip-sigurne instance čitača za različite tipove podataka.
 */
public final class UcitavacFactory {

    private UcitavacFactory() {
    }

    /**
     * Kreira čitač za turističke aranžmane.
     *
     * @return instanca čitača aranžmana
     */
    public static UcitavacPodataka<Aranzman> createAranzmanReader() {
        return new CitacAranzmana();
    }

    /**
     * Kreira čitač za rezervacije aranžmana.
     *
     * @return instanca čitača rezervacija
     */
    public static UcitavacPodataka<Rezervacija> createRezervacijaReader() {
        return new CitacRezervacija();
    }
}

