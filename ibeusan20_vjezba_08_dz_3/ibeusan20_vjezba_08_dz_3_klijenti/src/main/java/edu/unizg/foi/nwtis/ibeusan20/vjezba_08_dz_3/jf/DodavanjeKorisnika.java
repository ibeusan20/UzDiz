package edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jf;

import java.io.Serializable;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.ServisPartnerKlijent;
import edu.unizg.foi.nwtis.podaci.Korisnik;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.core.Response;

@Named("dodavanjeKorisnika")
@RequestScoped
public class DodavanjeKorisnika implements Serializable {
  private static final long serialVersionUID = 98237478924897234L;

  @Inject
  RestConfiguration restConfiguration;
  
  @Inject
  @RestClient
  ServisPartnerKlijent servisPartnerKlijent;

  private String korisnickoIme;
  private String lozinka;
  private String ime;
  private String prezime;
  private String email;

  private String poruka;

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

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPoruka() {
    return poruka;
  }

  public void dodajKorisnika() {
    Korisnik korisnik = new Korisnik(korisnickoIme, lozinka, prezime, ime, email);
    ServisPartnerKlijent klijent = restConfiguration.dajServisPartner();
    System.out.println("KLJENT1: " + klijent + "\n\n\n-------------\n\n\n");
    System.out.println(klijent);
    Response odgovor = klijent.dodajKorisnika(korisnik);
    System.out.println("ODGOVOR " + odgovor);
    System.out.println("KLJENT2: " + odgovor);
    if (odgovor.getStatus() == 200 || odgovor.getStatus() == 201) {
      poruka = "Korisnik uspješno dodan!";
    } else {
      poruka = "Greška pri dodavanju korisnika! Status: " + odgovor.getStatus();
    }
  }
}
