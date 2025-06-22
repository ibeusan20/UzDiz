package edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jf;

import java.io.Serializable;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.ServisPartnerKlijent;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.core.Response;


/**
 * Klasa SpavanjePartnera  za istoimenu .xhtml stranicu.
 */
@Named("spavanjePartnera")
@RequestScoped
public class SpavanjePartnera implements Serializable {
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 98734895L;
  
  /** The vrijeme. */
  private int vrijeme;
  
  /** The poruka. */
  private String poruka;

  /** The servis partner klijent. */
  @Inject
  private ServisPartnerKlijent servisPartnerKlijent;

  /**
   * Gets the vrijeme.
   *
   * @return the vrijeme
   */
  public int getVrijeme() { return vrijeme; }
  
  /**
   * Sets the vrijeme.
   *
   * @param vrijeme the new vrijeme
   */
  public void setVrijeme(int vrijeme) { this.vrijeme = vrijeme; }

  /**
   * Aktiviraj spavanje.
   */
  public void aktivirajSpavanje() {
    try {
      Response r = servisPartnerKlijent.spava(vrijeme);
      if (r.getStatus() == 200) {
        poruka = "Spavanje aktivirano!";
      } else {
        poruka = "Greška: " + r.getStatus();
      }
    } catch (Exception e) {
      poruka = "Došlo je do greške: " + e.getMessage();
    }
    FacesContext.getCurrentInstance().addMessage(null,
        new FacesMessage(FacesMessage.SEVERITY_INFO, poruka, null));
  }
}
