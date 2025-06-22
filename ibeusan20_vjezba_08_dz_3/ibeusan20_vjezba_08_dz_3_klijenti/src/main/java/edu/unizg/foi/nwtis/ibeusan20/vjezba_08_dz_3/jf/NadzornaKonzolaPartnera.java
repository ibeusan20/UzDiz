package edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jf;

import java.io.Serializable;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.GlobalniPodaci;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.core.Response;

/**
 * Klasa NadzornaKonzolaPartnera za istoimenu .xhtml stranicu.
 */
@Named("nadzornaKonzolaPartnera")
@SessionScoped
public class NadzornaKonzolaPartnera implements Serializable {

  /** Constant serialVersionUID. */
  private static final long serialVersionUID = 1078347890L;

  /**  rest configuration. */
  @Inject
  RestConfiguration restConfiguration;
  
  /**  globalni podaci. */
  @Inject
  GlobalniPodaci globalniPodaci;

  /**  status poruka. */
  private String statusPoruka;
  
  /**  id. */
  private int id = 1;

  /**
   * Dohvaća id.
   *
   * @return the id
   */
  public int getId() {
      return id;
  }

  /**
   * Postavlja id.
   *
   * @param id the new id
   */
  public void setId(int id) {
      this.id = id;
  }

  /**
   * Dohvaćanje statusa.
   */
  public void status() {
    try {
      var servis = restConfiguration.dajServisPartner();
      Response r = servis.status(id);
      statusPoruka = "STATUS: " + r.getStatus();
    } catch (Exception e) {
      statusPoruka = "Greška: " + e.getMessage();
    }
  }

  /**
   * Postavljanej pauze.
   */
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

  /**
   * Postavljanje izlaska iz pauze.
   */
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

  /**
   * Postavljanje kraja rada poslužitelja.
   */
  public void kraj() {
    try {
      var servis = restConfiguration.dajServisPartner();
      Response r = servis.kraj();
      statusPoruka = "KRAJ: " + r.getStatus();
    } catch (Exception e) {
      statusPoruka = "Greška: " + e.getMessage();
    }
  }

  /**
   * Dohvaća poruku statusa.
   *
   * @return the status poruka
   */
  public String getStatusPoruka() {
    return statusPoruka;
  }

  /**
   * Postavlja poruku statusa..
   *
   * @param statusPoruka the new status poruka
   */
  public void setStatusPoruka(String statusPoruka) {
    this.statusPoruka = statusPoruka;
  }
}
