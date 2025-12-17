package edu.unizg.foi.uzdiz.ibeusan20.ispisi;

import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;
import edu.unizg.foi.uzdiz.ibeusan20.model.Rezervacija;

public class IspisStatistikaAranzmanaAdapter implements IspisniRed {

  private final Aranzman a;

  public IspisStatistikaAranzmanaAdapter(Aranzman a) {
    this.a = a;
  }

  @Override
  public String[] zaglavlje() {
    return new String[] {
        "Oznaka", "Ukupan broj", "Aktivne", "Na čekanju", "Odgođene", "Otkazane", "Ukupan prihod"
    };
  }

  @Override
  public String[] vrijednosti() {
    int ukupno = 0, aktivne = 0, cekanje = 0, odgodene = 0, otkazane = 0;

    for (Rezervacija r : a.getRezervacije()) {
      ukupno++;
      String s = (r.nazivStanja() == null) ? "" : r.nazivStanja().toUpperCase();

      if (s.contains("AKTIV")) aktivne++;
      else if (s.contains("ČEKAN") || s.contains("CEKAN")) cekanje++;
      else if (s.contains("ODGOD") || s.contains("ODGO")) odgodene++;
      else if (s.contains("OTKAZ")) otkazane++;
    }

    double prihod = aktivne * a.getCijena();

    return new String[] {
        a.getOznaka(),
        String.valueOf(ukupno),
        String.valueOf(aktivne),
        String.valueOf(cekanje),
        String.valueOf(odgodene),
        String.valueOf(otkazane),
        String.valueOf(prihod)
    };
  }
}
