package edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jpa.pomocnici;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jpa.entiteti.Obracuni;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;


/**
 * The Class ObracuniFacade.
 */
@Stateless
public class ObracuniFacade extends EntityManagerProducer implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 6348758735L;

  /** The cb. */
  private CriteriaBuilder cb;

  /**
   * Inits the.
   */
  @PostConstruct
  private void init() {
    cb = getEntityManager().getCriteriaBuilder();
  }

  /**
   * Dohvati obracune za partnera i razdoblje.
   *
   * @param idPartnera the id partnera
   * @param od the od
   * @param do_ the do
   * @return the list
   */
  public List<Obracuni> dohvatiObracuneZaPartneraIRazdoblje(int idPartnera, Timestamp od, Timestamp do_) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<Obracuni> cq = cb.createQuery(Obracuni.class);
    Root<Obracuni> root = cq.from(Obracuni.class);
    List<Predicate> uvjeti = new ArrayList<>();

    uvjeti.add(cb.equal(root.get("partneri").get("id"), idPartnera));
    uvjeti.add(cb.greaterThanOrEqualTo(root.get("vrijeme"), od));
    uvjeti.add(cb.lessThanOrEqualTo(root.get("vrijeme"), do_));

    cq.select(root).where(cb.and(uvjeti.toArray(new Predicate[0])));
    return getEntityManager().createQuery(cq).getResultList();
  }

}
