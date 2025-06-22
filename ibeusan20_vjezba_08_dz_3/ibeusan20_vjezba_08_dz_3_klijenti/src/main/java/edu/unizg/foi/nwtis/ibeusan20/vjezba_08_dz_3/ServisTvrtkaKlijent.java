package edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3;

import java.util.List;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import edu.unizg.foi.nwtis.podaci.Obracun;
import edu.unizg.foi.nwtis.podaci.Partner;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Interface ServisTvrtkaKlijent.
 */
@RegisterRestClient(configKey = "klijentTvrtka")
@Path("api/tvrtka")
public interface ServisTvrtkaKlijent {
  
  /** Bazični URL */
  final String baseUrl = "http://20.24.5.20:8080/nwtis";
  
  /**
   * Head posluzitelj putanja.
   *
   * @return the response
   */
  @HEAD
  public Response headPosluzitelj();

  /**
   * Head posluzitelj status putanja.
   *
   * @param id the id
   * @return the response
   */
  @Path("status/{id}")
  @HEAD
  public Response headPosluziteljStatus(@PathParam("id") int id);

  /**
   * Head posluzitelj pauza putanja.
   *
   * @param id the id
   * @return the response
   */
  @Path("pauza/{id}")
  @HEAD
  public Response headPosluziteljPauza(@PathParam("id") int id);

  /**
   * Head posluzitelj start putanja.
   *
   * @param id the id
   * @return the response
   */
  @Path("start/{id}")
  @HEAD
  public Response headPosluziteljStart(@PathParam("id") int id);

  /**
   * Head posluzitelj kraj putanja.
   *
   * @return the response
   */
  @Path("kraj")
  @HEAD
  public Response headPosluziteljKraj();

  /**
   * Head posluzitelj kraj info putanja.
   *
   * @return the response
   */
  @Path("kraj/info")
  @HEAD
  public Response headPosluziteljKrajInfo();

  /**
   * Gets the partneri putanja.
   *
   * @return the partneri
   */
  @Path("partner")
  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public Response getPartneri();

  /**
   * Gets the partner putanja.
   *
   * @param id the id
   * @return the partner
   */
  @Path("partner/{id}")
  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public Response getPartner(@PathParam("id") int id);
  
  /**
   * Dohvati obracune putanja.
   *
   * @param od the od
   * @param ddo the ddo
   * @return the list
   */
  @GET
  @Path("obracun")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Obracun> dohvatiObracune(
    @QueryParam("od") String od,
    @QueryParam("do") String ddo
  );

  /**
   * Dohvati obracune jelo putanja.
   *
   * @param od the od
   * @param ddo the ddo
   * @return the list
   */
  @GET
  @Path("obracun/jelo")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Obracun> dohvatiObracuneJelo(
    @QueryParam("od") String od,
    @QueryParam("do") String ddo
  );

  /**
   * Dohvati obracune pice putanja.
   *
   * @param od the od
   * @param ddo the ddo
   * @return the list
   */
  @GET
  @Path("obracun/pice")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Obracun> dohvatiObracunePice(
    @QueryParam("od") String od,
    @QueryParam("do") String ddo
  );
  
  /**
   * Dohvati obracune partner putanja.
   *
   * @param id the id
   * @param od the od
   * @param ddo the ddo
   * @return the response
   */
  @GET
  @Path("obracun/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response dohvatiObracunePartner(
      @PathParam("id") int id,
      @QueryParam("od") long od,
      @QueryParam("do") long ddo
  );
  
  /**
   * Dodaj partnera putanja.
   *
   * @param partner the partner
   * @return the response
   */
  @POST
  @Path("partner")
  @Produces(MediaType.APPLICATION_JSON)
  public Response dodajPartnera(Partner partner);

  /**
   * Aktiviraj spavanje putanja.
   *
   * @param vrijeme the vrijeme
   * @return the response
   */
  @GET
  @Path("spava")
  public Response aktivirajSpavanje(@QueryParam("vrijeme") int vrijeme);

}
