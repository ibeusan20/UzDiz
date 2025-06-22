package edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jf;

import java.io.Serializable;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.ServisPartnerKlijent;
import edu.unizg.foi.nwtis.podaci.Korisnik;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.core.Response;

/**
 * Klasa DodavanjeKorisnika za istoimenu .xhtml stranicu.
 */
@Named("dodavanjeKorisnika")
@RequestScoped
public class DodavanjeKorisnika implements Serializable {
  
  /**  Constant serialVersionUID. */
  private static final long serialVersionUID = 98237478924897234L;

  /**  rest configuration. */
  @Inject
  RestConfiguration restConfiguration;
  
  /**  servis partner klijent. */
  @Inject
  @RestClient
  ServisPartnerKlijent servisPartnerKlijent;

  /**  korisnicko ime */
  private String korisnickoIme;
  
  /**  lozinka */
  private String lozinka;
  
  /**  ime */
  private String ime;
  
  /**  prezime. */
  private String prezime;
  
  /**  email */
  private String email;

  /**  poruka */
  private String poruka;

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
   * Dohvaća lozinku.
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
    return ime;
  }

  /**
   * Postavlja ime.
   *
   * @param ime the new ime
   */
  public void setIme(String ime) {
    this.ime = ime;
  }

  /**
   * Dohvaća prezime.
   *
   * @return the prezime
   */
  public String getPrezime() {
    return prezime;
  }

  /**
   * Postavlja prezime.
   *
   * @param prezime the new prezime
   */
  public void setPrezime(String prezime) {
    this.prezime = prezime;
  }

  /**
   * Dohvaća email.
   *
   * @return the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * Postavlja email.
   *
   * @param email the new email
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Dohvaća poruku.
   *
   * @return the poruka
   */
  public String getPoruka() {
    return poruka;
  }

  /**
   * Dodavanje korisnika.
   */
  public void dodajKorisnika() {
    Korisnik korisnik = new Korisnik(korisnickoIme, lozinka, prezime, ime, email);
    ServisPartnerKlijent klijent = restConfiguration.dajServisPartner();
    try {
      Response odgovor = klijent.dodajKorisnika(korisnik);

      int status = odgovor.getStatus();
      System.out.println("Status dodavanja korisnika: " + status);

      if (status == 200 || status == 201) {
        poruka = "Korisnik uspješno dodan!";
      } else if (status == 409) {
        poruka = "Korisnik već postoji!";
      } else {
        poruka = "Neočekivana greška! Status: " + status;
      }

    } catch (jakarta.ws.rs.WebApplicationException e) {
      int status = e.getResponse().getStatus();
      if (status == 409) {
        poruka = "Korisnik već postoji! (409)";
      } else {
        poruka = "Greška pri dodavanju korisnika! Status: " + status;
      }
      e.printStackTrace();
    } catch (Exception e) {
      poruka = "Došlo je do greške: " + e.getMessage();
      e.printStackTrace();
    }
  }
}
