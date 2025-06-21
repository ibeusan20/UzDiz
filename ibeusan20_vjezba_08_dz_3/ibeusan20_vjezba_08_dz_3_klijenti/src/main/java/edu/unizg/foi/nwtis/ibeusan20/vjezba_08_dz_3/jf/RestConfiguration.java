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
 *
 * @author ibeusan20
 */
@ApplicationScoped
public class RestConfiguration {
  @Inject
  @ConfigProperty(name = "korisnickoImeBazaPodataka")
  private String korisnickoImeBazaPodataka;
  @Inject
  @ConfigProperty(name = "lozinkaBazaPodataka")
  private String lozinkaBazaPodataka;
  @Inject
  @ConfigProperty(name = "upravljacBazaPodataka")
  private String upravljacBazaPodataka;
  @Inject
  @ConfigProperty(name = "urlBazaPodataka")
  private String urlBazaPodataka;

  public Connection dajVezu() throws Exception {
    Class.forName(this.upravljacBazaPodataka);
    var vezaBazaPodataka = DriverManager.getConnection(this.urlBazaPodataka,
        this.korisnickoImeBazaPodataka, this.lozinkaBazaPodataka);
    return vezaBazaPodataka;
  }  
  
  /*
   * public ServisPartnerKlijent dajServisPartner() { try { URI uri = new
   * URI("http://20.24.5.20:8080/nwtis/api/partner"); return RestClientBuilder.newBuilder()
   * .baseUri(uri) .build(ServisPartnerKlijent.class); } catch (Exception e) { throw new
   * RuntimeException("Greška pri stvaranju REST klijenta ServisPartnerKlijent", e); } }
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
