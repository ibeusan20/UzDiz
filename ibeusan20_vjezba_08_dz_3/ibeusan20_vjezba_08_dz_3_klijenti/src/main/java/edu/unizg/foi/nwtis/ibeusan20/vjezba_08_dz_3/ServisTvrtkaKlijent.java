package edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3;

import java.util.List;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import edu.unizg.foi.nwtis.podaci.Obracun;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RegisterRestClient(configKey = "klijentTvrtka")
@Path("api/tvrtka")
public interface ServisTvrtkaKlijent {
  
  final String baseUrl = "http://20.24.5.20:8080/nwtis";
  
  @HEAD
  public Response headPosluzitelj();

  @Path("status/{id}")
  @HEAD
  public Response headPosluziteljStatus(@PathParam("id") int id);

  @Path("pauza/{id}")
  @HEAD
  public Response headPosluziteljPauza(@PathParam("id") int id);

  @Path("start/{id}")
  @HEAD
  public Response headPosluziteljStart(@PathParam("id") int id);

  @Path("kraj")
  @HEAD
  public Response headPosluziteljKraj();

  @Path("kraj/info")
  @HEAD
  public Response headPosluziteljKrajInfo();

  @Path("partner")
  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public Response getPartneri();

  @Path("partner/{id}")
  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public Response getPartner(@PathParam("id") int id);
  
  @GET
  @Path("obracun")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Obracun> dohvatiObracune(
    @jakarta.ws.rs.QueryParam("od") String od,
    @jakarta.ws.rs.QueryParam("do") String ddo
  );

  @GET
  @Path("obracun/jelo")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Obracun> dohvatiObracuneJelo(
    @jakarta.ws.rs.QueryParam("od") String od,
    @jakarta.ws.rs.QueryParam("do") String ddo
  );

  @GET
  @Path("obracun/pice")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Obracun> dohvatiObracunePice(
    @jakarta.ws.rs.QueryParam("od") String od,
    @jakarta.ws.rs.QueryParam("do") String ddo
  );
  
  @GET
  @Path("obracun/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response dohvatiObracunePartner(
      @PathParam("id") int id,
      @jakarta.ws.rs.QueryParam("od") long od,
      @jakarta.ws.rs.QueryParam("do") long ddo
  );




  // TODO na isti način definirati za ostale operacije RESTful servisa
}
