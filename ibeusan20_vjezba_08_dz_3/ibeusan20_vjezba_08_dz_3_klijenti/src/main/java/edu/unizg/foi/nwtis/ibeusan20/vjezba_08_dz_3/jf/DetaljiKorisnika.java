package edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jf;

import java.io.Serializable;
import edu.unizg.foi.nwtis.podaci.Korisnik;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Klasa DetaljiKorisnika za istoimenu .xhtml stranicu.
 */
@Named("detaljiKorisnika")
@RequestScoped
public class DetaljiKorisnika implements Serializable {

  /** Constant serialVersionUID. */
  private static final long serialVersionUID = 145645645L;

  /** rest configuration. */
  @Inject
  RestConfiguration restConfiguration;

  /** korisnik. */
  private Korisnik korisnik;

  /**
   * Inicijalizacija.
   */
  @PostConstruct
  public void init() {
    FacesContext facesContext = FacesContext.getCurrentInstance();
    String korisnickoIme = facesContext.getExternalContext()
        .getRequestParameterMap()
        .get("korisnik");

    if (korisnickoIme != null && !korisnickoIme.isBlank()) {
      try {
        korisnik = restConfiguration.dajServisPartner().dohvatiKorisnika(korisnickoIme);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Dohvaćanje korisnika.
   *
   * @return the korisnik
   */
  public Korisnik getKorisnik() {
    return korisnik;
  }
}
