package edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jf;

import java.io.Serializable;
import java.util.List;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jpa.entiteti.Korisnici;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jpa.pomocnici.KorisniciFacade;
import edu.unizg.foi.nwtis.podaci.Korisnik;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Klasa PregledKorisnika za istoimenu .xhtml stranicu.
 */
@Named("pregledKorisnika")
@RequestScoped
public class PregledKorisnika implements Serializable {
  
  /**  Constant serialVersionUID. */
  private static final long serialVersionUID = 189537565L;

  /**  korisnici facade. */
  @Inject
  KorisniciFacade korisniciFacade;
  
  /**  rest configuration. */
  @Inject
  RestConfiguration restConfiguration;

  /**  ime. */
  private String ime;
  
  /**  prezime. */
  private String prezime;
  
  /**  korisnici entiteti. */
  private List<Korisnici> korisnici;
  
  /**  korisnicii objekti. */
  private List<Korisnik> korisnicii;
  
  /** odabrani korisnik. */
  private Korisnik odabraniKorisnik;

  /**
   * Dohvaćanje imena.
   *
   * @return the ime
   */
  public String getIme() {
    return ime;
  }

  /**
   * Postavljanje imena.
   *
   * @param ime the new ime
   */
  public void setIme(String ime) {
    this.ime = ime;
  }

  /**
   * Dohvaćanej prezimena.
   *
   * @return the prezime
   */
  public String getPrezime() {
    return prezime;
  }

  /**
   * Postavljanje prezimena.
   *
   * @param prezime the new prezime
   */
  public void setPrezime(String prezime) {
    this.prezime = prezime;
  }

  /**
   * Dohvaćanje korinsika tj. objekata
   *
   * @return the korisnici
   */
  public List<Korisnici> getKorisnici() {
    return korisnici;
  }
  
  /**
   * Dohvaćanje korisnika tj. entiteta.
   *
   * @return the korisnicii
   */
  public List<Korisnik> getKorisnicii() {
    return korisnicii;
  }

  /**
   * Pretrazivanje korisnika.
   *
   * @return the string
   */
  public String pretrazi() {
    korisnici = korisniciFacade.pretraziPoImenuIPrezimenu(ime, prezime);
    return null;
  }
  
  /**
   * Inicijalizacija.
   */
  @PostConstruct
  public void init() {
    var servis = restConfiguration.dajServisPartner();
    korisnicii = servis.dohvatiSveKorisnike();
  }
  
  /**
   * Dohvaćanej odabranog korisnika..
   *
   * @return the odabrani korisnik
   */
  public Korisnik getOdabraniKorisnik() {
    return odabraniKorisnik;
  }

  /**
   * Postavljanje odabranog korisnika.
   *
   * @param odabraniKorisnik the new odabrani korisnik
   */
  public void setOdabraniKorisnik(Korisnik odabraniKorisnik) {
    this.odabraniKorisnik = odabraniKorisnik;
  }
  
  /**
   * Prikaz detalja za korisnika po korisnickom imenu.
   *
   * @param korisnickoIme the korisnicko ime
   * @return the string
   */
  public String prikaziDetalje(String korisnickoIme) {
    var servis = restConfiguration.dajServisPartner();
    this.odabraniKorisnik = servis.dohvatiKorisnika(korisnickoIme);
    return "/admin/detaljiKorisnika.xhtml?faces-redirect=true";
  }
}
