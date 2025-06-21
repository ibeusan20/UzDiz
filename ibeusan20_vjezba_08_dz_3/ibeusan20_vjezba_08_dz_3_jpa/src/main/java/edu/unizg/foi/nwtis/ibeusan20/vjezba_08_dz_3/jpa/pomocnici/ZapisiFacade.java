package edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jpa.pomocnici;

import edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jpa.entiteti.Zapisi;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;

import java.io.Serializable;
import java.util.List;

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
}
