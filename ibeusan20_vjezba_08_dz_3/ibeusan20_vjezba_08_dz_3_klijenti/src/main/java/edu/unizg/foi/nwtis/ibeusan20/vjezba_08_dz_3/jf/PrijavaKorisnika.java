package edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jf;

import java.io.Serializable;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.dao.KorisnikDAO;
import edu.unizg.foi.nwtis.podaci.Korisnik;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@SessionScoped
@Named("prijavaKorisnika")
public class PrijavaKorisnika implements Serializable {
  private static final long serialVersionUID = -1826447622277477398L;
  private Korisnik korisnik;
  private String korisnickoIme;
  private String lozinka;
  private String ime;
  private String prezime;
  private String email;
  private boolean prijavljen = false;
  private String poruka = "";

  @Inject
  RestConfiguration restConfiguration;  
  
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

  public void setIme(String ime) {
    this.ime = ime;
  }

  public String getPrezime() {
    return this.korisnik.prezime();
  }

  public void setPrezime(String prezime) {
    this.prezime = prezime;
  }

  public String getEmail() {
    return this.korisnik.email();
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public boolean isPrijavljen() {
    return prijavljen;
  }

  public String getPoruka() {
    return poruka;
  }

  public String prijavaKorisnika() {
    if (this.korisnickoIme != null && this.korisnickoIme.trim().length() > 3 && this.lozinka != null
        && this.lozinka.trim().length() > 5) {
      try (var vezaBP = this.restConfiguration.dajVezu()) {
        var korisnikDAO = new KorisnikDAO(vezaBP);
        this.korisnik = korisnikDAO.dohvati(korisnickoIme, lozinka, true);
        if (this.korisnik != null) {
          this.prijavljen = true;
          this.poruka = "";
          return "index.xhtml";
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    this.prijavljen = false;
    this.poruka = "Neuspješna prijava korisnika.";
    return "prijavaKorisnika.xhtml";
  }

}
