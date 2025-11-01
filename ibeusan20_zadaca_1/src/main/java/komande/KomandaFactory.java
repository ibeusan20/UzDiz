package komande;

import logika.*;

public final class KomandaFactory {

    private KomandaFactory() {}

    public static Komanda kreiraj(String unos,
                                  UpraviteljAranzmanima ua,
                                  UpraviteljRezervacijama ur) {

        String[] dijelovi = unos.trim().split("\\s+");
        String naredba = dijelovi[0].toUpperCase();

        String[] argumenti = new String[dijelovi.length - 1];
        if (dijelovi.length > 1) {
            System.arraycopy(dijelovi, 1, argumenti, 0, dijelovi.length - 1);
        }

        return switch (naredba) {
            case "ITAK" -> new KomandaItak(ua, argumenti);
            case "ITAP" -> new KomandaItap(ua, argumenti);
            case "IRTA" -> new KomandaIrta(ur, argumenti);
            case "Q" -> new KomandaQ();
            default -> null;
        };
    }
}
