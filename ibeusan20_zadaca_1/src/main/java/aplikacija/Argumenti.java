package aplikacija;

import java.util.HashMap;
import java.util.Map;

public class Argumenti {

    private final Map<String, String> argumenti = new HashMap<>();

    public Argumenti(String[] args) {
        ucitajArgumente(args);
        provjeriObavezne();
    }

    private void ucitajArgumente(String[] args) {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].startsWith("--")) {
                argumenti.put(args[i], args[i + 1]);
            }
        }
    }

    private void provjeriObavezne() {
        if (!argumenti.containsKey("--ta")) {
            throw new IllegalArgumentException("Nije zadana datoteka aranžmana (--ta)");
        }
        if (!argumenti.containsKey("--rta")) {
            throw new IllegalArgumentException("Nije zadana datoteka rezervacija (--rta)");
        }
    }

    public String dohvatiPutanjuAranzmana() {
        return argumenti.get("--ta");
    }

    public String dohvatiPutanjuRezervacija() {
        return argumenti.get("--rta");
    }
}
