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
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Interface ServisPartnerKlijent.
 */
@RegisterRestClient(configKey = "klijentPartner")
@Path("api/partner")
public interface ServisPartnerKlijent {

  /**
   * Provjera rada posluzitelja putanja.
   *
   * @return the response
   */
  @HEAD
  @Path("")
  Response provjeraRadaPosluzitelja();
  
  /**
   * Dodaj korisnika putanja.
   *
   * @param korisnik the korisnik
   * @return the response
   */
  @POST
  @Path("korisnik")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  Response dodajKorisnika(Korisnik korisnik);
  
  /**
   * Dohvati korisnika putanja.
   *
   * @param korisnickoIme the korisnicko ime
   * @return the korisnik
   */
  @GET
  @Path("korisnik/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  Korisnik dohvatiKorisnika(@PathParam("id") String korisnickoIme);
  
  
  /**
   * Dohvati sve korisnike putanja.
   *
   * @return the list
   */
  @GET
  @Path("korisnik")
  @Produces(MediaType.APPLICATION_JSON)
  List<Korisnik> dohvatiSveKorisnike();

  
  /**
   * Dohvati jelovnik putanja.
   *
   * @param korisnik the korisnik
   * @param lozinka the lozinka
   * @return the list
   */
  @GET
  @Path("jelovnik")
  @Produces(MediaType.APPLICATION_JSON)
  List<Jelovnik> dohvatiJelovnik(@HeaderParam("korisnik") String korisnik, @HeaderParam("lozinka") String lozinka);

  /**
   * Dohvati kartu pica putanja.
   *
   * @param korisnik the korisnik
   * @param lozinka the lozinka
   * @return the list
   */
  @GET
  @Path("kartapica")
  @Produces(MediaType.APPLICATION_JSON)
  List<KartaPica> dohvatiKartuPica(@HeaderParam("korisnik") String korisnik, @HeaderParam("lozinka") String lozinka);

  /**
   * Spava putanja.
   *
   * @param vrijeme the vrijeme
   * @return the response
   */
  @GET
  @Path("spava")
  Response spava(@QueryParam("vrijeme") int vrijeme);
  
  /**
   * Status putanja.
   *
   * @param id the id
   * @return the response
   */
  @HEAD
  @Path("status/{id}")
  Response status(@PathParam("id") int id);

  /**
   * Pauza putanja.
   *
   * @param id the id
   * @return the response
   */
  @HEAD
  @Path("pauza/{id}")
  Response pauza(@PathParam("id") int id);

  /**
   * Start putanja.
   *
   * @param id the id
   * @return the response
   */
  @HEAD
  @Path("start/{id}")
  Response start(@PathParam("id") int id);


  /**
   * Kraj putanja.
   *
   * @return the response
   */
  @HEAD
  @Path("kraj")
  Response kraj();

}
