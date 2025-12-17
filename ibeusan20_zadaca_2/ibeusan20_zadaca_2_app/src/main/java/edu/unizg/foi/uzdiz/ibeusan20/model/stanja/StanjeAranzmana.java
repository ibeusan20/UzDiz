package edu.unizg.foi.uzdiz.ibeusan20.model.stanja;

// TODO: Auto-generated Javadoc
/**
 * The Interface StanjeAranzmana.
 */
public interface StanjeAranzmana {

  /**
   * Tekstualni naziv stanja aranžmana.
   *
   * @return the string
   */
  String naziv();

  /**
   * Smije li se na aranžman i dalje primati nove rezervacije.
   *
   * @return true, if successful
   */
  boolean mozePrimatiRezervacije();
}
