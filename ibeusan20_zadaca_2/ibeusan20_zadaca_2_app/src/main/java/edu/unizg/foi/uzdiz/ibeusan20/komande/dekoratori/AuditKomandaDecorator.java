package edu.unizg.foi.uzdiz.ibeusan20.komande.dekoratori;

import java.time.LocalDateTime;
import edu.unizg.foi.uzdiz.ibeusan20.audit.AuditDnevnik;
import edu.unizg.foi.uzdiz.ibeusan20.komande.Komanda;

// TODO: Auto-generated Javadoc
/**
 * The Class AuditKomandaDecorator.
 */
public class AuditKomandaDecorator extends KomandaDecorator {

  /** The izvorni unos. */
  private final String izvorniUnos;

  /**
   * Instantiates a new audit komanda decorator.
   *
   * @param omotana the omotana
   * @param izvorniUnos the izvorni unos
   */
  public AuditKomandaDecorator(Komanda omotana, String izvorniUnos) {
    super(omotana);
    this.izvorniUnos = izvorniUnos == null ? "" : izvorniUnos.trim();
  }

  /**
   * Izvrsi.
   *
   * @return true, if successful
   */
  @Override
  public boolean izvrsi() {
    LocalDateTime vrijeme = LocalDateTime.now();
    long start = System.nanoTime();

    String status = "OK";
    String poruka = "";
    boolean nastavi = true;

    try {
      nastavi = omotana.izvrsi();
      status = nastavi ? "OK" : "KRAJ";
      return nastavi;
    } catch (Exception e) {
      status = "ERROR";
      poruka = (e.getMessage() == null) ? e.getClass().getSimpleName() : e.getMessage();
      // ne ruši aplikaciju na neočekivanoj grešci komande
      System.err.println("Greška pri izvršavanju komande: " + poruka);
      return true;
    } finally {
      long trajanjeMs = Math.max(0, (System.nanoTime() - start) / 1_000_000);
      String naziv = omotana.getClass().getSimpleName();
      AuditDnevnik.instanca().dodaj(vrijeme, izvorniUnos, naziv, status, trajanjeMs, poruka);
    }
  }
}