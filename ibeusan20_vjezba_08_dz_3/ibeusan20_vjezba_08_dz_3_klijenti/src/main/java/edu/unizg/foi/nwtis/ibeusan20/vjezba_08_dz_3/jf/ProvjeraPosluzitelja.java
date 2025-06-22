package edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jf;

import java.io.Serializable;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.ServisPartnerKlijent;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.Response;

@Named("provjeraPosluzitelja")
@RequestScoped
public class ProvjeraPosluzitelja implements Serializable {
  private static final long serialVersionUID = 10236859826L;
  
  @Inject
  RestConfiguration restConfiguration;

  private String statusPoruka;

  public String getStatusPoruka() {
    return statusPoruka;
  }

  public void provjeriRaduPosluzitelja() {
    try {
      ServisPartnerKlijent klijent = restConfiguration.dajServisPartner();
      Response odgovor = klijent.provjeraRadaPosluzitelja();
      if (odgovor.getStatus() == 200) {
        statusPoruka = "Poslužitelj partnera radi ispravno.";
      } else {
        statusPoruka = "Poslužitelj partnera ne radi! Status: " + odgovor.getStatus();
      }
    } catch (ProcessingException e) {
      statusPoruka = "Greška u povezivanju s poslužiteljem!";
    }
  }
}
