package logika;

import model.Rezervacija;
import java.util.ArrayList;
import java.util.List;

public class UpraviteljRezervacijama {

    private final List<Rezervacija> rezervacije = new ArrayList<>();

    public UpraviteljRezervacijama(List<Rezervacija> pocetne) {
        if (pocetne != null) {
            rezervacije.addAll(pocetne);
        }
    }

    public int brojRezervacija() {
        return rezervacije.size();
    }

    public List<Rezervacija> sve() {
        return new ArrayList<>(rezervacije);
    }

    public void dodaj(Rezervacija r) {
        if (r != null) {
            rezervacije.add(r);
        }
    }
}
