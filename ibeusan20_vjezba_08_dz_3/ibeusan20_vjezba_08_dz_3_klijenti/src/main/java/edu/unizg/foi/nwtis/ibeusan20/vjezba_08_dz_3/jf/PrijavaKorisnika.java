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

@SessionScoped
@Named("prijavaKorisnika")
public class PrijavaKorisnika implements Serializable {
  private static final long serialVersionUID = -1826447622277477398L;
  private String korisnickoIme;
  private String lozinka;
  private Korisnik korisnik;
  private boolean prijavljen = false;
  private String poruka = "";
  private Partner odabraniPartner;
  private boolean partnerOdabran = false;

  @Inject
  RestConfiguration restConfiguration;

  @Inject
  KorisniciFacade korisniciFacade;
  
  @Inject
  private ZapisiFacade zapisiFacade;

  @Inject
  private SecurityContext securityContext;
  
  @Inject
  private HttpServletRequest request;

  public String getKorisnickoIme() {
    return korisnickoIme;
  }

  public void setKorisnickoIme(String korisnickoIme) {
    this.korisnickoIme = korisnickoIme;
  }

  public String getLozinka() {
    return lozinka;
  }

  public void setLozinka(String lozinka) {
    this.lozinka = lozinka;
  }

  public String getIme() {
    return this.korisnik.ime();
  }

  public String getPrezime() {
    return this.korisnik.prezime();
  }

  public String getEmail() {
    return this.korisnik.email();
  }

  public boolean isPrijavljen() {
    if (!this.prijavljen) {
      provjeriPrijavuKorisnika();
    }
    return this.prijavljen;
  }

  public String getPoruka() {
    return poruka;
  }

  public Partner getOdabraniPartner() {
    return odabraniPartner;
  }

  public void setOdabraniPartner(Partner odabraniPartner) {
    this.odabraniPartner = odabraniPartner;
  }

  public boolean isPartnerOdabran() {
    return partnerOdabran;
  }

  public void setPartnerOdabran(boolean partnerOdabran) {
    this.partnerOdabran = partnerOdabran;
  }

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
 
  public void init() {
    korisnickoIme = request.getRemoteUser();
    if (korisnickoIme != null) {
      dodajZapis("prijava");
    }
  }
  
  private void dodajZapis(String akcija) {
    Zapisi z = new Zapisi();
    z.setKorisnickoime(korisnickoIme);
    z.setVrijeme(Timestamp.valueOf(LocalDateTime.now()));
    z.setOpisrada(akcija);
    z.setIpadresaracunala(request.getRemoteAddr());
    zapisiFacade.create(z);
  }
  
  private boolean imaAktivnuNarudzbu = false;

  public boolean isImaAktivnuNarudzbu() {
    return imaAktivnuNarudzbu;
  }

  public void setImaAktivnuNarudzbu(boolean imaAktivnuNarudzbu) {
    this.imaAktivnuNarudzbu = imaAktivnuNarudzbu;
  }
  
  public List<Jelovnik> getJelovnikOdabranogPartnera() {
    if (!partnerOdabran || korisnik == null) return List.of();
    return restConfiguration.dajServisPartner()
        .dohvatiJelovnik(korisnik.korisnik(), korisnik.lozinka());
  }

  public List<KartaPica> getKartaPicaOdabranogPartnera() {
    if (!partnerOdabran || korisnik == null) return List.of();
    return restConfiguration.dajServisPartner()
        .dohvatiKartuPica(korisnik.korisnik(), korisnik.lozinka());
  }


}
