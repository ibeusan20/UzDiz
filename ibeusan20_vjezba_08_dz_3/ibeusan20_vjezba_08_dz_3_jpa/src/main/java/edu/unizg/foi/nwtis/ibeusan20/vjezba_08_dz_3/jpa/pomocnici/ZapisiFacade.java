package edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jpa.pomocnici;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jpa.entiteti.Zapisi;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * The Class ZapisiFacade.
 */
@Stateless
public class ZapisiFacade extends EntityManagerProducer implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 938759334550L;

  /** The cb. */
  private CriteriaBuilder cb;

  /**
   * Inicijalizacija.
   */
  @PostConstruct
  private void init() {
    cb = getEntityManager().getCriteriaBuilder();
  }

  /**
   * Kreiranje zapisa.
   *
   * @param zapis the zapis
   */
  public void create(Zapisi zapis) {
    getEntityManager().persist(zapis);
  }

  /**
   * Uređivanej zapisa.
   *
   * @param zapis the zapis
   */
  public void edit(Zapisi zapis) {
    getEntityManager().merge(zapis);
  }

  /**
   * Micanje zapisa.
   *
   * @param zapis the zapis
   */
  public void remove(Zapisi zapis) {
    getEntityManager().remove(getEntityManager().merge(zapis));
  }

  /**
   * Traženje zapisa po idju.
   *
   * @param id the id
   * @return the zapisi
   */
  public Zapisi find(Object id) {
    return getEntityManager().find(Zapisi.class, id);
  }

  /**
   * Traženje svih zapisa.
   *
   * @return the list
   */
  public List<Zapisi> findAll() {
    CriteriaQuery<Zapisi> cq = cb.createQuery(Zapisi.class);
    cq.select(cq.from(Zapisi.class));
    return getEntityManager().createQuery(cq).getResultList();
  }

  /**
   * vraća broj zapisa.
   *
   * @return the int
   */
  public int count() {
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    cq.select(cb.count(cq.from(Zapisi.class)));
    return ((Long) getEntityManager().createQuery(cq).getSingleResult()).intValue();
  }
  
  /**
   * Dohvati zapise za korisnika i razdoblje.
   *
   * @param korisnickoIme the korisnicko ime
   * @param od the od
   * @param do_ the do
   * @return the list
   */
  public List<Zapisi> dohvatiZapiseZaKorisnikaIRazdoblje(String korisnickoIme, Timestamp od, Timestamp do_) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<Zapisi> cq = cb.createQuery(Zapisi.class);
    Root<Zapisi> root = cq.from(Zapisi.class);

    Predicate pIme = cb.equal(root.get("korisnickoime"), korisnickoIme);
    Predicate pOd = cb.greaterThanOrEqualTo(root.get("vrijeme"), od);
    Predicate pDo = cb.lessThanOrEqualTo(root.get("vrijeme"), do_);
    cq.select(root).where(cb.and(pIme, pOd, pDo));

    return getEntityManager().createQuery(cq).getResultList();
  }

}
