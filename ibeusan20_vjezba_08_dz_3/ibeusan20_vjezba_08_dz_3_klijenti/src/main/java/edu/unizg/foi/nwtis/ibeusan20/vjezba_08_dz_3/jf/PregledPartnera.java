package edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jf;

import java.io.Serializable;
import java.util.List;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jpa.pomocnici.PartneriFacade;
import edu.unizg.foi.nwtis.podaci.Partner;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("pregledPartnera")
@SessionScoped
public class PregledPartnera implements Serializable {
  private static final long serialVersionUID = 1983475783478539784L;

  @Inject
  RestConfiguration restConfiguration;

  @Inject
  PartneriFacade partneriFacade;

  private Partner odabraniPartner;

  public List<Partner> getPartneri() {
    return partneriFacade.findAllPartners();
  }

  public String postaviPartnera(Partner partner) {
    this.odabraniPartner = partner;
    return "/detaljiPartnera.xhtml?faces-redirect=true";
  }

  public Partner getOdabraniPartner() {
    return odabraniPartner;
  }
}
