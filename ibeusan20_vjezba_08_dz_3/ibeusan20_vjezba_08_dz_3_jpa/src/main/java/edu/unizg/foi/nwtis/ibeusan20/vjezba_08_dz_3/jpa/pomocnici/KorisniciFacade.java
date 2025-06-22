/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jpa.pomocnici;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jpa.entiteti.Korisnici;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jpa.entiteti.Korisnici_;
import edu.unizg.foi.nwtis.podaci.Korisnik;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * The Class KorisniciFacade.
 *
 * @author Ivan Beusan
 */
@Stateless
public class KorisniciFacade extends EntityManagerProducer implements Serializable {
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 3595041786540495885L;

  /** Criteria API builder. */
  private CriteriaBuilder cb;

  /**
   * Inicijalizacija.
   */
  @PostConstruct
  private void init() {
    cb = getEntityManager().getCriteriaBuilder();
  }

  /**
   * Stvaranje korisnika.
   *
   * @param  korisnici
   */
  public void create(Korisnici korisnici) {
    getEntityManager().persist(korisnici);
  }

  /**
   * Uređivanje korisnika.
   *
   * @param  korisnici
   */
  public void edit(Korisnici korisnici) {
    getEntityManager().merge(korisnici);
  }

  /**
   * Micanje korisnika.
   *
   * @param  korisnici
   */
  public void remove(Korisnici korisnici) {
    getEntityManager().remove(getEntityManager().merge(korisnici));
  }

  /**
   * Traženje korisnika.
   *
   * @param  id
   * @return  korisnici
   */
  public Korisnici find(Object id) {
    return getEntityManager().find(Korisnici.class, id);
  }

  /**
   * Traženje svih korisnika.
   *
   * @return lista korisnika
   */
  public List<Korisnici> findAll() {
    CriteriaQuery<Korisnici> cq = cb.createQuery(Korisnici.class);
    cq.select(cq.from(Korisnici.class));
    return getEntityManager().createQuery(cq).getResultList();
  }

  /**
   * Traži skup korisika.
   *
   * @param  range
   * @return  list
   */
  public List<Korisnici> findRange(int[] range) {
    CriteriaQuery<Korisnici> cq = cb.createQuery(Korisnici.class);
    cq.select(cq.from(Korisnici.class));
    TypedQuery<Korisnici> q = getEntityManager().createQuery(cq);
    q.setMaxResults(range[1] - range[0]);
    q.setFirstResult(range[0]);
    return q.getResultList();
  }

  /**
   * Traži korisnika.
   *
   * @param korisnickoIme korisnicko ime
   * @param lozinka lozinka
   * @return korisnici
   */
  public Korisnici find(String korisnickoIme, String lozinka) {
    CriteriaQuery<Korisnici> cq = cb.createQuery(Korisnici.class);
    Root<Korisnici> korisnici = cq.from(Korisnici.class);
    Expression<String> zaKorisnik = korisnici.get(Korisnici_.korisnik);
    Expression<String> zaLozinku = korisnici.get(Korisnici_.lozinka);
    cq.where(cb.and(cb.equal(zaKorisnik, korisnickoIme), cb.equal(zaLozinku, lozinka)));
    TypedQuery<Korisnici> q = getEntityManager().createQuery(cq);
    return q.getResultList().getFirst();
  }

  /**
   * Traži sve korisnike.
   *
   * @param prezime prezime
   * @param ime ime
   * @return list
   */
  public List<Korisnici> findAll(String prezime, String ime) {
    CriteriaQuery<Korisnici> cq = cb.createQuery(Korisnici.class);
    Root<Korisnici> korisnici = cq.from(Korisnici.class);
    Expression<String> zaPrezime = korisnici.get(Korisnici_.prezime);
    Expression<String> zaIme = korisnici.get(Korisnici_.ime);
    cq.where(cb.and(cb.like(zaPrezime, prezime), cb.like(zaIme, ime)));
    TypedQuery<Korisnici> q = getEntityManager().createQuery(cq);
    return q.getResultList();
  }
  
  /**
   * Pretraži po imenu i prezimenu.
   *
   * @param ime ime
   * @param prezime prezime
   * @return list
   */
  public List<Korisnici> pretraziPoImenuIPrezimenu(String ime, String prezime) {
    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    CriteriaQuery<Korisnici> cq = cb.createQuery(Korisnici.class);
    Root<Korisnici> root = cq.from(Korisnici.class);

    List<Predicate> uvjeti = new ArrayList<>();
    if (ime != null && !ime.isBlank()) {
      uvjeti.add(cb.like(cb.lower(root.get("ime")), "%" + ime.toLowerCase() + "%"));
    }
    if (prezime != null && !prezime.isBlank()) {
      uvjeti.add(cb.like(cb.lower(root.get("prezime")), "%" + prezime.toLowerCase() + "%"));
    }

    cq.where(cb.and(uvjeti.toArray(new Predicate[0])));
    cq.orderBy(cb.asc(root.get("prezime")), cb.asc(root.get("ime")));

    return getEntityManager().createQuery(cq).getResultList();
  }


  /**
   * Vraća broj korinsika.
   *
   * @return the int
   */
  public int count() {
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    cq.select(cb.count(cq.from(Korisnici.class)));
    return ((Long) getEntityManager().createQuery(cq).getSingleResult()).intValue();
  }

  /**
   * Pretvorba korisnika (objekt vs entitet)
   *
   * @param k the k
   * @return the korisnik
   */
  public Korisnik pretvori(Korisnici k) {
    if (k == null) {
      return null;
    }
    var kObjekt =
        new Korisnik(k.getKorisnik(), k.getLozinka(), k.getPrezime(), k.getIme(), k.getEmail());

    return kObjekt;
  }

  /**
   * Pretvorba korisnika (objekt vs entitet)
   *
   * @param k the k
   * @return the korisnici
   */
  public Korisnici pretvori(Korisnik k) {
    if (k == null) {
      return null;
    }
    var kE = new Korisnici();
    kE.setKorisnik(k.korisnik());
    kE.setLozinka(k.lozinka());
    kE.setPrezime(k.prezime());
    kE.setIme(k.ime());
    kE.setEmail(k.email());

    return kE;
  }

  /**
   * Pretvorba korisnika (objekt vs entitet)
   *
   * @param korisniciE the korisnici E
   * @return the list
   */
  public List<Korisnik> pretvori(List<Korisnici> korisniciE) {
    List<Korisnik> korisnici = new ArrayList<>();
    for (Korisnici kEntitet : korisniciE) {
      var kObjekt = pretvori(kEntitet);

      korisnici.add(kObjekt);
    }

    return korisnici;
  }

}
