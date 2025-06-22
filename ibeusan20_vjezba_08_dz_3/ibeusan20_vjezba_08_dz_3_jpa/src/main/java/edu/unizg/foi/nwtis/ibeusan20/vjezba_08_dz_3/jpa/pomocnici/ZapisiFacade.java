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

@Stateless
public class ZapisiFacade extends EntityManagerProducer implements Serializable {

  private static final long serialVersionUID = 938759334550L;

  private CriteriaBuilder cb;

  @PostConstruct
  private void init() {
    cb = getEntityManager().getCriteriaBuilder();
  }

  public void create(Zapisi zapis) {
    getEntityManager().persist(zapis);
  }

  public void edit(Zapisi zapis) {
    getEntityManager().merge(zapis);
  }

  public void remove(Zapisi zapis) {
    getEntityManager().remove(getEntityManager().merge(zapis));
  }

  public Zapisi find(Object id) {
    return getEntityManager().find(Zapisi.class, id);
  }

  public List<Zapisi> findAll() {
    CriteriaQuery<Zapisi> cq = cb.createQuery(Zapisi.class);
    cq.select(cq.from(Zapisi.class));
    return getEntityManager().createQuery(cq).getResultList();
  }

  public int count() {
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    cq.select(cb.count(cq.from(Zapisi.class)));
    return ((Long) getEntityManager().createQuery(cq).getSingleResult()).intValue();
  }
  
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
