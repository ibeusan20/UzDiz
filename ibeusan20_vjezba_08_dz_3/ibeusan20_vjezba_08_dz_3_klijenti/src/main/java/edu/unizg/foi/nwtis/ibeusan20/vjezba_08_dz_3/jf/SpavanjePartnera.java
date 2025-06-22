package edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jf;

import java.io.Serializable;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.ServisPartnerKlijent;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.core.Response;

@Named("spavanjePartnera")
@RequestScoped
public class SpavanjePartnera implements Serializable {
  private static final long serialVersionUID = 98734895L;
  
  private int vrijeme;
  private String poruka;

  @Inject
  private ServisPartnerKlijent servisPartnerKlijent;

  public int getVrijeme() { return vrijeme; }
  public void setVrijeme(int vrijeme) { this.vrijeme = vrijeme; }

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
