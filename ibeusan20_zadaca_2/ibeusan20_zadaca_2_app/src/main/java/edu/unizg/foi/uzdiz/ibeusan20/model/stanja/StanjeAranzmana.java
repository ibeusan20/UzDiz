package edu.unizg.foi.uzdiz.ibeusan20.model.stanja;

public interface StanjeAranzmana {

  /**
   * Tekstualni naziv stanja aranžmana.
   */
  String naziv();

  /**
   * Smije li se na aranžman i dalje primati nove rezervacije.
   */
  boolean mozePrimatiRezervacije();
}
