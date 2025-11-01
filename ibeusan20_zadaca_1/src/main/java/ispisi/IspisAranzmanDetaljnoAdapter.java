package ispisi;

import model.Aranzman;
import model.FormatDatuma;
import java.util.stream.Collectors;

/**
 * Adapter za detaljan ispis jednog turističkog aranžmana.
 */
public class IspisAranzmanDetaljnoAdapter {

    private final Aranzman a;

    public IspisAranzmanDetaljnoAdapter(Aranzman a) {
        this.a = a;
    }

    public void ispisiDetalje() {
        System.out.println("Oznaka: " + a.getOznaka());
        System.out.println("Naziv: " + a.getNaziv());
        System.out.println("Program: " + a.getProgram());
        System.out.println("Početni datum: " + FormatDatuma.formatiraj(a.getPocetniDatum()));
        System.out.println("Završni datum: " + FormatDatuma.formatiraj(a.getZavrsniDatum()));
        System.out.println("Vrijeme kretanja: " + FormatDatuma.formatiraj(a.getVrijemeKretanja()));
        System.out.println("Vrijeme povratka: " + FormatDatuma.formatiraj(a.getVrijemePovratka()));
        System.out.println("Cijena: " + String.format("%.2f €", a.getCijena()));
        System.out.println("Min putnika: " + a.getMinPutnika());
        System.out.println("Max putnika: " + a.getMaxPutnika());
        System.out.println("Broj noćenja: " + a.getBrojNocenja());
        System.out.println("Doplata jednokrevetna: " + String.format("%.2f €", a.getDoplataJednokrevetna()));
        System.out.println("Prijevoz: " + (a.getPrijevoz() == null ? "" :
                a.getPrijevoz().stream().collect(Collectors.joining("; "))));
        System.out.println("Doručci: " + a.getBrojDorucaka());
        System.out.println("Ručkovi: " + a.getBrojRuckova());
        System.out.println("Večere: " + a.getBrojVecera());
    }
}
