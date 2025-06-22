package edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jpa.pomocnici;

import java.io.Serializable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Producer for injectable EntityManager.
 */
public abstract class EntityManagerProducer implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -8963852717889659294L;

  /** The em. */
  @PersistenceContext(unitName = "vjezba_08_dz_3_jpa")
  private EntityManager em;

  /**
   * Gets the entity manager.
   *
   * @return the entity manager
   */
  public EntityManager getEntityManager() {
    return em;
  }
}
