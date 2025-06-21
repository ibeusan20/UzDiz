package edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import edu.unizg.foi.nwtis.podaci.Korisnik;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RegisterRestClient(configKey = "klijentPartner")
@Path("api/partner")
public interface ServisPartnerKlijent {

  @HEAD
  Response provjeraRadaPosluzitelja();
  
  @POST
  @Path("korisnik")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  Response dodajKorisnika(Korisnik korisnik);

}
