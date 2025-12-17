package edu.unizg.foi.uzdiz.ibeusan20.model;

import edu.unizg.foi.uzdiz.ibeusan20.datoteke.api.AranzmanPodaci;

/**
 * Director za izgradnju {@link Aranzman} objekata iz ulaznih podataka.
 * <p>
 * Dio je uzorka <b>Builder</b>: preuzima {@link AranzmanPodaci}, priprema ih 
 * i koristi {@link AranzmanBuilder} za kreiranje aranžmana.
 * </p>
 */
public class AranzmanDirector {

  /**
   * Konstruira {@link Aranzman} iz zadanih podataka.
   *
   * @param p podaci aranžmana (npr. iz datoteke)
   * @return kreirani aranžman
   * @throws IllegalArgumentException ako su podaci {@code null}
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
