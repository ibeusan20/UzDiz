package edu.unizg.foi.uzdiz.ibeusan20.model;

import edu.unizg.foi.uzdiz.ibeusan20.datoteke.api.AranzmanPodaci;

// TODO: Auto-generated Javadoc
/**
 * The Class AranzmanDirector.
 */
public class AranzmanDirector {

  /**
   * Konstruiraj.
   *
   * @param p the p
   * @return the aranzman
   */
  public Aranzman konstruiraj(AranzmanPodaci p) {
    if (p == null) throw new IllegalArgumentException("Podaci aranžmana nisu definirani.");

    String prijevozTekst =
        (p.getPrijevoz() == null || p.getPrijevoz().isEmpty())
            ? null
            : String.join(";", p.getPrijevoz());

    return new AranzmanBuilder()
        .postaviOznaku(p.getOznaka())
        .postaviNaziv(p.getNaziv())
        .postaviProgram(p.getProgram())
        .postaviPocetniDatum(p.getPocetniDatum())
        .postaviZavrsniDatum(p.getZavrsniDatum())
        .postaviVrijemeKretanja(p.getVrijemeKretanja())
        .postaviVrijemePovratka(p.getVrijemePovratka())
        .postaviCijenu(p.getCijena())
        .postaviMinPutnika(p.getMinPutnika())
        .postaviMaxPutnika(p.getMaxPutnika())
        .postaviBrojNocenja(p.getBrojNocenja())
        .postaviDoplatuJednokrevetna(p.getDoplataJednokrevetna())
        .postaviPrijevoz(prijevozTekst)
        .postaviBrojDorucaka(p.getBrojDorucaka())
        .postaviBrojRuckova(p.getBrojRuckova())
        .postaviBrojVecera(p.getBrojVecera())
        .izgradi();
  }
}
