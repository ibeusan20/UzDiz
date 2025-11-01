package ispisi;

import model.Rezervacija;
import model.FormatDatuma;

/**
 * Adapter između modela Rezervacija i ispisa u tablici.
 * 
 * Ovaj adapter omogućuje da format ispisa (Bridge uzorak)
 * ostane neovisan o strukturi modela Rezervacija.
 */
public class IspisRezervacijaAdapter {

    private final Rezervacija r;

    public IspisRezervacijaAdapter(Rezervacija r) {
        this.r = r;
    }

    public String getIme() {
        return r.getIme();
    }

    public String getPrezime() {
        return r.getPrezime();
    }

    public String getOznakaAranzmana() {
        return r.getOznakaAranzmana();
    }

    public String getDatumVrijemeRezervacije() {
        return FormatDatuma.formatiraj(r.getDatumVrijeme());
    }
}
