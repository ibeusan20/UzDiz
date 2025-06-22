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

@Named("pregledKorisnika")
@RequestScoped
public class PregledKorisnika implements Serializable {
  private static final long serialVersionUID = 189537565L;

  @Inject
  KorisniciFacade korisniciFacade;
  
  @Inject
  RestConfiguration restConfiguration;

  private String ime;
  private String prezime;
  private List<Korisnici> korisnici;
  private List<Korisnik> korisnicii;
  private Korisnik odabraniKorisnik;

  public String getIme() {
    return ime;
  }

  public void setIme(String ime) {
    this.ime = ime;
  }

  public String getPrezime() {
    return prezime;
  }

  public void setPrezime(String prezime) {
    this.prezime = prezime;
  }

  public List<Korisnici> getKorisnici() {
    return korisnici;
  }
  
  public List<Korisnik> getKorisnicii() {
    return korisnicii;
  }

  public String pretrazi() {
    korisnici = korisniciFacade.pretraziPoImenuIPrezimenu(ime, prezime);
    return null;
  }
  
  @PostConstruct
  public void init() {
    var servis = restConfiguration.dajServisPartner();
    korisnicii = servis.dohvatiSveKorisnike();
  }
  
  public Korisnik getOdabraniKorisnik() {
    return odabraniKorisnik;
  }

  public void setOdabraniKorisnik(Korisnik odabraniKorisnik) {
    this.odabraniKorisnik = odabraniKorisnik;
  }
  
  public String prikaziDetalje(String korisnickoIme) {
    var servis = restConfiguration.dajServisPartner();
    this.odabraniKorisnik = servis.dohvatiKorisnika(korisnickoIme);
    return "/admin/detaljiKorisnika.xhtml?faces-redirect=true";
  }
}
