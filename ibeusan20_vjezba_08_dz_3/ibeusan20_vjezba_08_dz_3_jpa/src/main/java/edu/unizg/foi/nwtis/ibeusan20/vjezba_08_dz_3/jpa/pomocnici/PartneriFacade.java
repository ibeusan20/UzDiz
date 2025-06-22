package edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jpa.pomocnici;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jpa.entiteti.Partneri;
import edu.unizg.foi.nwtis.podaci.Partner;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

/**
 * The Class PartneriFacade.
 */
@Stateless
public class PartneriFacade extends EntityManagerProducer implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 193874598375988685L;

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
   * Stvaranje partnera.
   *
   * @param partner the partner
   */
  public void create(Partneri partner) {
    getEntityManager().persist(partner);
  }

  /**
   * Uređivanje partnera.
   *
   * @param partner the partner
   */
  public void edit(Partneri partner) {
    getEntityManager().merge(partner);
  }

  /**
   * Micanje partnera.
   *
   * @param partner the partner
   */
  public void remove(Partneri partner) {
    getEntityManager().remove(getEntityManager().merge(partner));
  }

  /**
   * Traženje partnera po idju.
   *
   * @param id the id
   * @return the partneri
   */
  public Partneri find(Object id) {
    return getEntityManager().find(Partneri.class, id);
  }

  /**
   * Traženje svih partnera.
   *
   * @return the list
   */
  public List<Partneri> findAll() {
    CriteriaQuery<Partneri> cq = cb.createQuery(Partneri.class);
    cq.select(cq.from(Partneri.class));
    return getEntityManager().createQuery(cq).getResultList();
  }

  /**
   * Traženje svih partnera.
   *
   * @return the list
   */
  public List<Partner> findAllPartners() {
    CriteriaQuery<Partneri> cq = cb.createQuery(Partneri.class);
    Root<Partneri> partneriRoot = cq.from(Partneri.class);
    cq.select(partneriRoot);
    TypedQuery<Partneri> q = getEntityManager().createQuery(cq);
    return q.getResultList().stream().map(this::pretvori).toList();
  }

  /**
   * Vraća broj partnera.
   *
   * @return the int
   */
  public int count() {
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    cq.select(cb.count(cq.from(Partneri.class)));
    return ((Long) getEntityManager().createQuery(cq).getSingleResult()).intValue();
  }

  /**
   * Pretvorba partnera (entitet vs objekt)
   *
   * @param p the p
   * @return the partner
   */
  public Partner pretvori(Partneri p) {
    if (p == null) {
      return null;
    }
    return new Partner(
        p.getId(),
        p.getNaziv(),
        p.getVrstakuhinje(),
        p.getAdresa(),
        p.getMreznavrata(),
        p.getMreznavratakraj(),
        (float) p.getGpssirina(),
        (float) p.getGpsduzina(),
        p.getSigurnosnikod(),
        p.getAdminkod()
    );
  }

  /**
   * Pretvorba partnera (entitet vs objekt)
   *
   * @param p the p
   * @return the partneri
   */
  public Partneri pretvori(Partner p) {
    if (p == null) {
      return null;
    }
    Partneri entitet = new Partneri();
    entitet.setId(p.id());
    entitet.setNaziv(p.naziv());
    entitet.setVrstakuhinje(p.vrstaKuhinje());
    entitet.setAdresa(p.adresa());
    entitet.setMreznavrata(p.mreznaVrata());
    entitet.setMreznavratakraj(p.mreznaVrataKraj());
    entitet.setGpssirina(p.gpsSirina());
    entitet.setGpsduzina(p.gpsDuzina());
    entitet.setSigurnosnikod(p.sigurnosniKod());
    entitet.setAdminkod(p.adminKod());
    return entitet;
  }

  /**
   * Pretvorba partnera (entitet vs objekt)
   *
   * @param partneriEntiteti the partneri entiteti
   * @return the list
   */
  public List<Partner> pretvori(List<Partneri> partneriEntiteti) {
    List<Partner> partneri = new ArrayList<>();
    for (Partneri entitet : partneriEntiteti) {
      partneri.add(pretvori(entitet));
    }
    return partneri;
  }
}
