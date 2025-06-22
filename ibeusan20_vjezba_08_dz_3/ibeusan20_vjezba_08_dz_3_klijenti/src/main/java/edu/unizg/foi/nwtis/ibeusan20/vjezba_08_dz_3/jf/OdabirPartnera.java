package edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.dao.PartnerDAO;
import edu.unizg.foi.nwtis.podaci.Partner;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;


/**
 * Klasa OdabirPartnera za istoimenu .xhtml stranicu.
 */
@RequestScoped
@Named("odabirParnera")
public class OdabirPartnera implements Serializable {

  /** Constant serialVersionUID. */
  private static final long serialVersionUID = -524581462819739622L;

  /**  prijava korisnika. */
  @Inject
  PrijavaKorisnika prijavaKorisnika;

  /**  rest configuration. */
  @Inject
  RestConfiguration restConfiguration;

  /**  partneri. */
  private List<Partner> partneri = new ArrayList<>();

  /**  partner. */
  private int partner;

  /**
   * Dohvaća partnera.
   *
   * @return the partner
   */
  public int getPartner() {
    return partner;
  }

  /**
   * Postavlja partnera.
   *
   * @param partner the new partner
   */
  public void setPartner(int partner) {
    this.partner = partner;
  }

  /**
   * Dohvaća partnere.
   *
   * @return the partneri
   */
  public List<Partner> getPartneri() {
    return partneri;
  }

  /**
   * Ucitaj partnere.
   */
  @PostConstruct
  public void ucitajPartnere() {
    try (var vezaBP = this.restConfiguration.dajVezu()) {
      var partnerDAO = new PartnerDAO(vezaBP);
      this.partneri = partnerDAO.dohvatiSve(true);
    } catch (Exception e) {
    }
  }

  /**
   * Odabir partnera.
   *
   * @return the string redirekcija
   */
  public String odaberiPartnera() {
    if (this.partner > 0) {
      Optional<Partner> partnerO = this.partneri.stream()
          .filter((p) -> p.id() == this.partner).findFirst();
      if (partnerO.isPresent()) {
        this.prijavaKorisnika.setOdabraniPartner(partnerO.get());
        this.prijavaKorisnika.setPartnerOdabran(true);
      } else {
        this.prijavaKorisnika.setPartnerOdabran(false);
      }
      if (prijavaKorisnika.isImaAktivnuNarudzbu()) {
        prijavaKorisnika.setPartnerOdabran(false);
        return "/index.xhtml?faces-redirect=true";
      }

    } else {
      this.prijavaKorisnika.setPartnerOdabran(false);
    }
    return "/index.xhtml?faces-redirect=true";
  }

}
