package edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jf;

import java.io.Serializable;
import java.util.List;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jpa.pomocnici.PartneriFacade;
import edu.unizg.foi.nwtis.podaci.Partner;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Klasa PregledPartnera za istoimenu .xhtml stranicu.
 */
@Named("pregledPartnera")
@SessionScoped
public class PregledPartnera implements Serializable {
  
  /**  Constant serialVersionUID. */
  private static final long serialVersionUID = 1983475783478539784L;

  /**  rest configuration. */
  @Inject
  RestConfiguration restConfiguration;

  /**  partneri facade. */
  @Inject
  PartneriFacade partneriFacade;

  /**  odabrani partner. */
  private Partner odabraniPartner;

  /**
   * Dohvaća partnere.
   *
   * @return the partneri
   */
  public List<Partner> getPartneri() {
    return partneriFacade.findAllPartners();
  }

  /**
   * Postavlja partnera.
   *
   * @param partner the partner
   * @return the string
   */
  public String postaviPartnera(Partner partner) {
    this.odabraniPartner = partner;
    return "/detaljiPartnera.xhtml?faces-redirect=true";
  }

  /**
   * Dohvaća odabranog partnera.
   *
   * @return the odabrani partner
   */
  public Partner getOdabraniPartner() {
    return odabraniPartner;
  }
}
