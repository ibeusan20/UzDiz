package ispisi;

public class TablicniFormat implements FormatIspisaBridge {

    private boolean zaglavljeIspisano = false;

    @Override
    public void ispisi(IspisAranzmanaAdapter a) {
        if (!zaglavljeIspisano) {
            ispisiZaglavlje();
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

    private void ispisiZaglavlje() {
        System.out.println("---------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-4s %-35s %-12s %-12s %-8s %-8s %10s %8s %8s%n",
                "OZN", "NAZIV", "POČETAK", "KRAJ", "KRET.", "POVR.", "CIJENA", "MIN", "MAKS");
        System.out.println("---------------------------------------------------------------------------------------------------------------");
    }

    private String skrati(String tekst, int max) {
        if (tekst == null) return "";
        if (tekst.length() <= max) return tekst;
        return tekst.substring(0, max - 3) + "...";
    }
}
