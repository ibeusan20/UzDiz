package edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jf;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jpa.entiteti.Zapisi;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jpa.pomocnici.KorisniciFacade;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jpa.pomocnici.ZapisiFacade;
import edu.unizg.foi.nwtis.podaci.Jelovnik;
import edu.unizg.foi.nwtis.podaci.KartaPica;
import edu.unizg.foi.nwtis.podaci.Korisnik;
import edu.unizg.foi.nwtis.podaci.Partner;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.security.enterprise.SecurityContext;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Klasa PrijavaKorisnika za istoimenu .xhtml stranicu.
 */
@SessionScoped
@Named("prijavaKorisnika")
public class PrijavaKorisnika implements Serializable {
  
  /**  Constant serialVersionUID. */
  private static final long serialVersionUID = -1826447622277477398L;
  
  /**  korisnicko ime. */
  private String korisnickoIme;
  
  /**  lozinka. */
  private String lozinka;
  
  /**  korisnik. */
  private Korisnik korisnik;
  
  /**  prijavljen. */
  private boolean prijavljen = false;
  
  /**  poruka. */
  private String poruka = "";
  
  /**  odabrani partner. */
  private Partner odabraniPartner;
  
  /**  partner odabran. */
  private boolean partnerOdabran = false;

  /**  rest configuration. */
  @Inject
  RestConfiguration restConfiguration;

  /**  korisnici facade. */
  @Inject
  KorisniciFacade korisniciFacade;
  
  /**  zapisi facade. */
  @Inject
  private ZapisiFacade zapisiFacade;

  /**  security context. */
  @Inject
  private SecurityContext securityContext;
  
  /**  request. */
  @Inject
  private HttpServletRequest request;

  /**
   * Dohvaća korisnicko ime.
   *
   * @return the korisnicko ime
   */
  public String getKorisnickoIme() {
    return korisnickoIme;
  }

  /**
   * Postavlja korisnicko ime.
   *
   * @param korisnickoIme the new korisnicko ime
   */
  public void setKorisnickoIme(String korisnickoIme) {
    this.korisnickoIme = korisnickoIme;
  }

  /**
   * Dohvaća llozinku.
   *
   * @return the lozinka
   */
  public String getLozinka() {
    return lozinka;
  }

  /**
   * Postavlja lozinku.
   *
   * @param lozinka the new lozinka
   */
  public void setLozinka(String lozinka) {
    this.lozinka = lozinka;
  }

  /**
   * Dohvaća ime.
   *
   * @return the ime
   */
  public String getIme() {
    return this.korisnik.ime();
  }

  /**
   * Dohvaća prezime.
   *
   * @return the prezime
   */
  public String getPrezime() {
    return this.korisnik.prezime();
  }

  /**
   * Dohvaća email.
   *
   * @return the email
   */
  public String getEmail() {
    return this.korisnik.email();
  }

  /**
   * Provjera je li prijavlen.
   *
   * @return true, if is prijavljen
   */
  public boolean isPrijavljen() {
    if (!this.prijavljen) {
      provjeriPrijavuKorisnika();
    }
    return this.prijavljen;
  }

  /**
   * Dohvaćanje poruke.
   *
   * @return the poruka
   */
  public String getPoruka() {
    return poruka;
  }

  /**
   * Dohvaća se odabrani partner.
   *
   * @return the odabrani partner
   */
  public Partner getOdabraniPartner() {
    return odabraniPartner;
  }

  /**
   * Postavlja se odabrani partner.
   *
   * @param odabraniPartner the new odabrani partner
   */
  public void setOdabraniPartner(Partner odabraniPartner) {
    this.odabraniPartner = odabraniPartner;
  }

  /**
   * Provjera je li  partner odabran.
   *
   * @return true, if is partner odabran
   */
  public boolean isPartnerOdabran() {
    return partnerOdabran;
  }

  /**
   * Postavlja da je partner odabran.
   *
   * @param partnerOdabran the new partner odabran
   */
  public void setPartnerOdabran(boolean partnerOdabran) {
    this.partnerOdabran = partnerOdabran;
  }

  /**
   * Provjerava prijavu korisnika.
   */
  @PostConstruct
  private void provjeriPrijavuKorisnika() {
    if (this.securityContext.getCallerPrincipal() != null) {
      var korIme = this.securityContext.getCallerPrincipal().getName();
      this.korisnik = this.korisniciFacade.pretvori(this.korisniciFacade.find(korIme));
      if (this.korisnik != null) {
        this.prijavljen = true;
        this.korisnickoIme = korIme;
        this.lozinka = this.korisnik.lozinka();
        init();
      }
    }
  }

  /**
   * Odjava korisnika.
   *
   * @return the string
   */
  public String odjavaKorisnika() {
    if (this.prijavljen) {
      dodajZapis("odjava");
      request.getSession().invalidate();
      this.prijavljen = false;

      FacesContext facesContext = FacesContext.getCurrentInstance();
      facesContext.getExternalContext().invalidateSession();

      return "/index.xhtml?faces-redirect=true";
    }
    return "";
  }
 
  /**
   * Inicijalizacija.
   */
  public void init() {
    korisnickoIme = request.getRemoteUser();
    if (korisnickoIme != null) {
      dodajZapis("prijava");
    }
  }
  
  /**
   * Dodaj zapis.
   *
   * @param akcija the akcija
   */
  private void dodajZapis(String akcija) {
    Zapisi z = new Zapisi();
    z.setKorisnickoime(korisnickoIme);
    z.setVrijeme(Timestamp.valueOf(LocalDateTime.now()));
    z.setOpisrada(akcija);
    z.setIpadresaracunala(request.getRemoteAddr());
    zapisiFacade.create(z);
  }
  
  /**  ima aktivnu narudzbu. */
  private boolean imaAktivnuNarudzbu = false;

  /**
   * Provjerava ima li aktivnu narudzbu.
   *
   * @return true, if is ima aktivnu narudzbu
   */
  public boolean isImaAktivnuNarudzbu() {
    return imaAktivnuNarudzbu;
  }

  /**
   * Postavlja da ima aktivnu narudzbu.
   *
   * @param imaAktivnuNarudzbu the new ima aktivnu narudzbu
   */
  public void setImaAktivnuNarudzbu(boolean imaAktivnuNarudzbu) {
    this.imaAktivnuNarudzbu = imaAktivnuNarudzbu;
  }
  
  /**
   * Dohvaća jelovnik odabranog partnera.
   *
   * @return the jelovnik odabranog partnera
   */
  public List<Jelovnik> getJelovnikOdabranogPartnera() {
    if (!partnerOdabran || korisnik == null) return List.of();
    return restConfiguration.dajServisPartner()
        .dohvatiJelovnik(korisnik.korisnik(), korisnik.lozinka());
  }

  /**
   * Dohvaća karta pica odabranog partnera.
   *
   * @return the karta pica odabranog partnera
   */
  public List<KartaPica> getKartaPicaOdabranogPartnera() {
    if (!partnerOdabran || korisnik == null) return List.of();
    return restConfiguration.dajServisPartner()
        .dohvatiKartuPica(korisnik.korisnik(), korisnik.lozinka());
  }


}
