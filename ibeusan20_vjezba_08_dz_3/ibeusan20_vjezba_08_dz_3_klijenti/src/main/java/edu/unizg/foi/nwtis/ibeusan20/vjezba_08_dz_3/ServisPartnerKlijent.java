package edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3;

import java.util.List;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import edu.unizg.foi.nwtis.podaci.Jelovnik;
import edu.unizg.foi.nwtis.podaci.KartaPica;
import edu.unizg.foi.nwtis.podaci.Korisnik;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RegisterRestClient(configKey = "klijentPartner")
@Path("api/partner")
public interface ServisPartnerKlijent {

  @HEAD
  @Path("")
  Response provjeraRadaPosluzitelja();
  
  @POST
  @Path("korisnik")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  Response dodajKorisnika(Korisnik korisnik);
  
  @GET
  @Path("jelovnik")
  @Produces(MediaType.APPLICATION_JSON)
  List<Jelovnik> dohvatiJelovnik(@HeaderParam("korisnik") String korisnik, @HeaderParam("lozinka") String lozinka);

  @GET
  @Path("kartapica")
  @Produces(MediaType.APPLICATION_JSON)
  List<KartaPica> dohvatiKartuPica(@HeaderParam("korisnik") String korisnik, @HeaderParam("lozinka") String lozinka);

  @GET
  @Path("spava")
  Response spava(@QueryParam("vrijeme") int vrijeme);


}
