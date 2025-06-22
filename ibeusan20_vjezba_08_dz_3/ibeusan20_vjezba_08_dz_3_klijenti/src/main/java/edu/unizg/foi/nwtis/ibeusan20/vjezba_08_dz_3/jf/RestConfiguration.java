/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.jf;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.Duration;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.ServisPartnerKlijent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Klasa RestConfiguration za istoimenu .xhtml stranicu.
 *
 * @author ibeusan20
 */
@ApplicationScoped
public class RestConfiguration {
  
  /** The korisnicko ime baza podataka. */
  @Inject
  @ConfigProperty(name = "korisnickoImeBazaPodataka")
  private String korisnickoImeBazaPodataka;
  
  /** The lozinka baza podataka. */
  @Inject
  @ConfigProperty(name = "lozinkaBazaPodataka")
  private String lozinkaBazaPodataka;
  
  /** The upravljac baza podataka. */
  @Inject
  @ConfigProperty(name = "upravljacBazaPodataka")
  private String upravljacBazaPodataka;
  
  /** The url baza podataka. */
  @Inject
  @ConfigProperty(name = "urlBazaPodataka")
  private String urlBazaPodataka;

  /**
   * Daj vezu.
   *
   * @return the connection
   * @throws Exception the exception
   */
  public Connection dajVezu() throws Exception {
    Class.forName(this.upravljacBazaPodataka);
    var vezaBazaPodataka = DriverManager.getConnection(this.urlBazaPodataka,
        this.korisnickoImeBazaPodataka, this.lozinkaBazaPodataka);
    return vezaBazaPodataka;
  }  
  
  
  /**
   * Daj servis partner.
   *
   * @return the servis partner klijent
   */
  public ServisPartnerKlijent dajServisPartner() {
    try {
      URI uri = new URI("http://20.24.5.20:8080/nwtis/v1");
      System.out.println("BASE URI: " + uri);
      return RestClientBuilder.newBuilder()
          .baseUri(uri)
          .build(ServisPartnerKlijent.class);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
