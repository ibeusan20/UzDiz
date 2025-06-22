package edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jf;

import java.io.Serializable;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.GlobalniPodaci;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.core.Response;

@Named("nadzornaKonzolaPartnera")
@SessionScoped
public class NadzornaKonzolaPartnera implements Serializable {

  private static final long serialVersionUID = 1078347890L;

  @Inject
  RestConfiguration restConfiguration;
  
  @Inject
  GlobalniPodaci globalniPodaci;

  private String statusPoruka;
  
  private int id = 1;

  public int getId() {
      return id;
  }

  public void setId(int id) {
      this.id = id;
  }

  public void status() {
    try {
      var servis = restConfiguration.dajServisPartner();
      Response r = servis.status(id);
      statusPoruka = "STATUS: " + r.getStatus();
    } catch (Exception e) {
      statusPoruka = "Greška: " + e.getMessage();
    }
  }

  public void pauza() {
    try {
      globalniPodaci.postaviPauzu(id, true);
      var servis = restConfiguration.dajServisPartner();
      Response r = servis.pauza(id);
      statusPoruka = "PAUZA: " + r.getStatus();
    } catch (Exception e) {
      statusPoruka = "Greška: " + e.getMessage();
    }
  }

  public void start() {
    try {
      globalniPodaci.postaviPauzu(id, false);
      var servis = restConfiguration.dajServisPartner();
      Response r = servis.start(id);
      statusPoruka = "START: " + r.getStatus();
    } catch (Exception e) {
      statusPoruka = "Greška: " + e.getMessage();
    }
  }

  public void kraj() {
    try {
      var servis = restConfiguration.dajServisPartner();
      Response r = servis.kraj();
      statusPoruka = "KRAJ: " + r.getStatus();
    } catch (Exception e) {
      statusPoruka = "Greška: " + e.getMessage();
    }
  }

  public String getStatusPoruka() {
    return statusPoruka;
  }

  public void setStatusPoruka(String statusPoruka) {
    this.statusPoruka = statusPoruka;
  }
}
