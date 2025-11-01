package ispisi;

/**
 * Implementacija Bridge uzorka za tablični ispis.
 * Može ispisivati i aranžmane i rezervacije.
 */
public class TablicniFormat implements FormatIspisaBridge {

    private boolean zaglavljeIspisano = false;

    @Override
    public void ispisi(Object adapter) {
        if (adapter instanceof IspisAranzmanaAdapter a) {
            ispisiAranzman(a);
        } else if (adapter instanceof IspisRezervacijaAdapter r) {
            ispisiRezervaciju(r);
        }
    }

    private void ispisiAranzman(IspisAranzmanaAdapter a) {
        if (!zaglavljeIspisano) {
            ispisiZaglavljeAranzmana();
            zaglavljeIspisano = true;
        }

        System.out.printf("%-4s %-35s %-12s %-12s %-8s %-8s %10s € %6d %6d%n",
                a.getOznaka(),
                skrati(a.getNaziv(), 35),
                a.getDatumOd(),
                a.getDatumDo(),
                a.getVrijemeKretanja(),
                a.getVrijemePovratka(),
                a.getCijena(),
                a.getMinPutnika(),
                a.getMaxPutnika());
    }

    private void ispisiZaglavljeAranzmana() {
        System.out.println("---------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-4s %-35s %-12s %-12s %-8s %-8s %10s %8s %8s%n",
                "OZN", "NAZIV", "POČETAK", "KRAJ", "KRET.", "POVR.", "CIJENA", "MIN", "MAKS");
        System.out.println("---------------------------------------------------------------------------------------------------------------");
    }

    private void ispisiRezervaciju(IspisRezervacijaAdapter r) {
        if (!zaglavljeIspisano) {
            ispisiZaglavljeRezervacija();
            zaglavljeIspisano = true;
        }

        System.out.printf("%-12s %-12s %-20s %-5s%n",
                r.getIme(),
                r.getPrezime(),
                r.getDatumVrijeme(),
                r.getVrsta());
    }

    private void ispisiZaglavljeRezervacija() {
        System.out.println("------------------------------------------------------------");
        System.out.printf("%-12s %-12s %-20s %-5s%n", "IME", "PREZIME", "DATUM I VRIJEME", "VRSTA");
        System.out.println("------------------------------------------------------------");
    }

    private String skrati(String tekst, int max) {
        if (tekst == null) return "";
        if (tekst.length() <= max) return tekst;
        return tekst.substring(0, max - 3) + "...";
    }
}
