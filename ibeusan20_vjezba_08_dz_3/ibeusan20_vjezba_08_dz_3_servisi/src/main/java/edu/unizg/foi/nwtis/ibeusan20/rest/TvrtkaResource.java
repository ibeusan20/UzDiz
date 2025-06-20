package edu.unizg.foi.nwtis.ibeusan20.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.dao.ObracunDAO;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.dao.PartnerDAO;
import edu.unizg.foi.nwtis.podaci.Jelovnik;
import edu.unizg.foi.nwtis.podaci.KartaPica;
import edu.unizg.foi.nwtis.podaci.Obracun;
import edu.unizg.foi.nwtis.podaci.Partner;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
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
 * Klasa TvrtkaResource rest servisa za posluziteljtvrtka.
 */
@Path("api/tvrtka")
public class TvrtkaResource {

  /** Adresa tvrtke. */
  @Inject
  @ConfigProperty(name = "adresa")
  private String tvrtkaAdresa;
  
  /** Mrezna vrata za KRAJ */
  @Inject
  @ConfigProperty(name = "mreznaVrataKraj")
  private String mreznaVrataKraj;
  
  /** Mrezna vrata za registraciju. */
  @Inject
  @ConfigProperty(name = "mreznaVrataRegistracija")
  private String mreznaVrataRegistracija;
  
  /** Mrezna vrata za rad. */
  @Inject
  @ConfigProperty(name = "mreznaVrataRad")
  private String mreznaVrataRad;
  
  /** Kod za admina tvrtke. */
  @Inject
  @ConfigProperty(name = "kodZaAdminTvrtke")
  private String kodZaAdminTvrtke;
  
  /** Kod za kraj. */
  @Inject
  @ConfigProperty(name = "kodZaKraj")
  private String kodZaKraj;

  /** REST konfiguracija */
  @Inject
  RestConfiguration restConfiguration;

  /**
   * Provjerava radi li poslužitelj tvrtke tako da šalje testnu komandu.
   *
   * @return HTTP 200 ako je poslužitelj aktivan, inače HTTP 500
   */
  @HEAD
  @Operation(summary = "Provjera statusa poslužitelja tvrtka")
  @APIResponses(value = {@APIResponse(responseCode = "200", description = "Uspješna operacija"),
      @APIResponse(responseCode = "500", description = "Interna pogreška")})
  @Counted(name = "brojZahtjeva_", description = "Koliko puta je pozvana operacija servisa")
  @Timed(name = "trajanjeMetode_headPosluzitelj", description = "Vrijeme trajanja metode")
  public Response headPosluzitelj() {
    var status = posaljiKomandu("KRAJ xxx");
    if (status != null) {
      return Response.status(Response.Status.OK).build();
    } else {
      return Response.status(Response.Status.CONFLICT).build();
    }
  }

  /**
   * Provjerava status određenog dijela poslužitelja tvrtke.
   *
   * @param id identifikator dijela poslužitelja
   * @return HTTP 200 ako je dio aktivan, HTTP 204 ako nije
   */
  @Path("status/{id}")
  @HEAD
  @Operation(summary = "Provjera statusa dijela poslužitelja tvrtka")
  @APIResponses(value = {
      @APIResponse(responseCode = "200", description = "Dio poslužitelja je aktivan"),
      @APIResponse(responseCode = "204", description = "Dio poslužitelja je u pauzi ili odgovor nije prepoznat")
  })
  @Counted(name = "brojZahtjeva_headPosluziteljStatus",
      description = "Koliko puta je pozvana operacija servisa")
  @Timed(name = "trajanjeMetode_headPosluziteljStatus", description = "Vrijeme trajanja metode")
  public Response headPosluziteljStatus(@PathParam("id") int id) {
    String odgovor = posaljiKomandu("STATUS " + this.kodZaAdminTvrtke + " " + id);
    
    if (odgovor != null && odgovor.trim().startsWith("OK")) {
      if (odgovor.contains("1")) {
        return Response.status(Response.Status.OK).build();
      } else if (odgovor.contains("0")) {
        return Response.status(Response.Status.NO_CONTENT).build();
      }
    }
    
    return Response.status(Response.Status.BAD_REQUEST).build();
  }

  /**
   * Postavlja dio poslužitelja tvrtke u pauzirano stanje.
   *
   * @param id identifikator dijela poslužitelja
   * @return HTTP 200 ako je uspješno, HTTP 204 ako nije
   */
  @Path("pauza/{id}")
  @HEAD
  @Operation(summary = "Postavljanje dijela poslužitelja tvrtka u pauzu")
  @APIResponses(value = {@APIResponse(responseCode = "200", description = "Uspješna operacija"),
      @APIResponse(responseCode = "204", description = "Pogrešna operacija")})
  @Counted(name = "brojZahtjeva_headPosluziteljPauza",
      description = "Koliko puta je pozvana operacija servisa")
  @Timed(name = "trajanjeMetode_headPosluziteljPauza", description = "Vrijeme trajanja metode")
  public Response headPosluziteljPauza(@PathParam("id") int id) {
    var status = posaljiKomandu("PAUZA " + this.kodZaAdminTvrtke + " " + id);
    if (status != null) {
      return Response.status(Response.Status.OK).build();
    } else {
      return Response.status(Response.Status.NO_CONTENT).build();
    }
  }

  /**
   * Postavlja dio poslužitelja tvrtke u aktivno stanje (start).
   *
   * @param id identifikator dijela poslužitelja
   * @return HTTP 200 ako je uspješno, HTTP 204 ako nije
   */
  @Path("start/{id}")
  @HEAD
  @Operation(summary = "Postavljanje dijela poslužitelja tvrtka u rad")
  @APIResponses(value = {@APIResponse(responseCode = "200", description = "Uspješna operacija"),
      @APIResponse(responseCode = "204", description = "Pogrešna operacija")})
  @Counted(name = "brojZahtjeva_headPosluziteljStart",
      description = "Koliko puta je pozvana operacija servisa")
  @Timed(name = "trajanjeMetode_headPosluziteljStart", description = "Vrijeme trajanja metode")
  public Response headPosluziteljStart(@PathParam("id") int id) {
    var status = posaljiKomandu("START " + this.kodZaAdminTvrtke + " " + id);
    if (status != null) {
      return Response.status(Response.Status.OK).build();
    } else {
      return Response.status(Response.Status.NO_CONTENT).build();
    }
  }

  /**
   * Šalje komandu za zaustavljanje poslužitelja tvrtke.
   *
   * @return HTTP 200 ako je uspješno, HTTP 204 ako nije
   */
  @Path("kraj")
  @HEAD
  @Operation(summary = "Zaustavljanje poslužitelja tvrtka")
  @APIResponses(value = {@APIResponse(responseCode = "200", description = "Uspješna operacija"),
      @APIResponse(responseCode = "204", description = "Pogrešna operacija")})
  @Counted(name = "brojZahtjeva_headPosluziteljKraj",
      description = "Koliko puta je pozvana operacija servisa")
  @Timed(name = "trajanjeMetode_headPosluziteljKraj", description = "Vrijeme trajanja metode")
  public Response headPosluziteljKraj() {
    var status = posaljiKomandu("KRAJWS " + this.kodZaKraj);
    if (status != null) {
      return Response.status(Response.Status.OK).build();
    } else {
      return Response.status(Response.Status.NO_CONTENT).build();
    }
  }

  /**
   * Ispisuje informaciju da je poslužitelj zaustavljen.
   *
   * @return HTTP 200 uvijek
   */
  @Path("kraj/info")
  @HEAD
  @Operation(summary = "Informacija o zaustavljanju poslužitelja tvrtka")
  @APIResponses(value = {@APIResponse(responseCode = "200", description = "Uspješna operacija"),
      @APIResponse(responseCode = "204", description = "Pogrešna operacija")})
  @Counted(name = "brojZahtjeva_headPosluziteljKrajInfo",
      description = "Koliko puta je pozvana operacija servisa")
  @Timed(name = "trajanjeMetode_headPosluziteljKrajInfo", description = "Vrijeme trajanja metode")
  public Response headPosluziteljKrajInfo() {
    System.out.println("[REST] headPosluziteljKrajInfo() POZVANA");
    System.out.println("PosluziteljTvrtka je završio rad.");
    return Response.status(Response.Status.OK).build();
  }

  /**
   * Dohvaća sve partnere iz baze podataka.
   *
   * @return HTTP 200 s listom partnera, ili HTTP 500 u slučaju pogreške
   */
  @Path("partner")
  @GET
  @Produces({MediaType.APPLICATION_JSON})
  @Operation(summary = "Dohvat svih partnera")
  @APIResponses(value = {@APIResponse(responseCode = "200", description = "Uspješna operacija"),
      @APIResponse(responseCode = "500", description = "Interna pogreška")})
  @Counted(name = "brojZahtjeva_getPartneri",
      description = "Koliko puta je pozvana operacija servisa")
  @Timed(name = "trajanjeMetode_getPartneri", description = "Vrijeme trajanja metode")
  public Response getPartneri() {
    try (var vezaBP = this.restConfiguration.dajVezu()) {
      var partnerDAO = new PartnerDAO(vezaBP);
      var partneri = partnerDAO.dohvatiSve(true);
      return Response.ok(partneri).status(Response.Status.OK).build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Dohvaća jednog partnera po ID-u iz baze.
   *
   * @param id identifikator partnera
   * @return HTTP 200 s partnerom, 404 ako ne postoji, 500 ako dođe do pogreške
   */
  @Path("partner/{id}")
  @GET
  @Produces({MediaType.APPLICATION_JSON})
  @Operation(summary = "Dohvat jednog partnera")
  @APIResponses(value = {@APIResponse(responseCode = "200", description = "Uspješna operacija"),
      @APIResponse(responseCode = "404", description = "Ne postoji resurs"),
      @APIResponse(responseCode = "500", description = "Interna pogreška")})
  @Counted(name = "brojZahtjeva_getPartner",
      description = "Koliko puta je pozvana operacija servisa")
  @Timed(name = "trajanjeMetode_getPartner", description = "Vrijeme trajanja metode")
  public Response getPartner(@PathParam("id") int id) {
    try (var vezaBP = this.restConfiguration.dajVezu()) {
      var partnerDAO = new PartnerDAO(vezaBP);
      var partner = partnerDAO.dohvati(id, true);
      if (partner != null) {
        return Response.ok(partner).status(Response.Status.OK).build();
      } else {
        return Response.status(Response.Status.NOT_FOUND).build();
      }
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }
  
  /**
   * Dohvaća partnere koji su trenutno registrirani na poslužitelju tvrtka.
   *
   * @return HTTP 200 s listom aktivnih partnera, ili HTTP 500 u slučaju pogreške
   */
  @Path("partner/provjera")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Dohvat partnera koji su trenutno registrirani na poslužitelju PosluziteljTvrtka")
  @APIResponses(value = {
      @APIResponse(responseCode = "200", description = "Uspješno dohvaćeni registrirani partneri"),
      @APIResponse(responseCode = "500", description = "Greška prilikom dohvata")
  })
  @Counted(name = "brojZahtjeva_getPartneriProvjera",
      description = "Broj zahtjeva za dohvat registriranih partnera")
  @Timed(name = "trajanjeMetode_getPartneriProvjera",
      description = "Vrijeme dohvaćanja registriranih partnera")
  public Response getPartneriProvjera() {
    try (var veza = this.restConfiguration.dajVezu()) {
      var partnerDAO = new PartnerDAO(veza);
      var sviPartneri = partnerDAO.dohvatiSve(true);

      try (
        var socket = new Socket(this.tvrtkaAdresa, Integer.parseInt(this.mreznaVrataRegistracija));
        var ulaz = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf8"));
        var izlaz = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf8"))
      ) {
        izlaz.println("POPIS");
        izlaz.flush();
        socket.shutdownOutput();

        var prviRed = ulaz.readLine();
        var drugiRed = ulaz.readLine();

        if (!"OK".equals(prviRed)) {
          return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        var gson = new com.google.gson.Gson();
        List<Map<String, Object>> partneriJson = gson.fromJson(drugiRed, List.class);
        List<Integer> registriraniIdjevi = partneriJson.stream()
            .map(map -> ((Double) map.get("id")).intValue())
            .toList();

        var aktivniPartneri = sviPartneri.stream()
            .filter(p -> registriraniIdjevi.contains(p.id()))
            .toList();

        return Response.ok(aktivniPartneri).build();
      }
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Dodaje partnera u bazu podataka.
   *
   * @param partner objekt partnera koji se dodaje
   * @return HTTP 201 ako je uspješno dodan, 409 ako već postoji, 500 za grešku
   */
  @Path("partner")
  @POST
  @Consumes({MediaType.APPLICATION_JSON})
  @Produces({MediaType.APPLICATION_JSON})
  @Operation(summary = "Postavljanje jednog partnera")
  @APIResponses(
      value = {@APIResponse(responseCode = "201", description = "Uspješna kreiran resurs"),
          @APIResponse(responseCode = "409", description = "Već postoji resurs ili druga pogreška"),
          @APIResponse(responseCode = "500", description = "Interna pogreška")})
  @Counted(name = "brojZahtjeva_postPartner",
      description = "Koliko puta je pozvana operacija servisa")
  @Timed(name = "trajanjeMetode_postPartner", description = "Vrijeme trajanja metode")
  public Response postPartner(Partner partner) {
    try (var vezaBP = this.restConfiguration.dajVezu()) {
      var partnerDAO = new PartnerDAO(vezaBP);
      var status = partnerDAO.dodaj(partner);
      if (status) {
        return Response.status(Response.Status.CREATED).build();
      } else {
        return Response.status(Response.Status.CONFLICT).build();
      }
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Posalje komandu koju primi za slanje te varaća odgovor koji dobije
   *
   * @param komanda komanda za slanje
   * @return the string string koji se vrati
   */
  private String posaljiKomandu(String komanda) {
    try {
      var mreznaUticnica = new Socket(this.tvrtkaAdresa, Integer.parseInt(this.mreznaVrataKraj));
      BufferedReader in =
          new BufferedReader(new InputStreamReader(mreznaUticnica.getInputStream(), "utf8"));
      PrintWriter out =
          new PrintWriter(new OutputStreamWriter(mreznaUticnica.getOutputStream(), "utf8"));
      out.write(komanda + "\n");
      out.flush();
      mreznaUticnica.shutdownOutput();
      var linija = in.readLine();
      mreznaUticnica.shutdownInput();
      mreznaUticnica.close();
      return linija;
    } catch (IOException e) {
    }
    return null;
  }
  
  /**
   * Dodaje više stavki obračuna i šalje ih na poslužitelj tvrtka.
   *
   * @param stavkeObracuna lista obračuna
   * @return HTTP 201 ako je uspješno, 500 u slučaju pogrešaka
   */
  @POST
  @Path("obracun/ws")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Dodavanje novog obračuna i slanje komande PosluziteljTvrtka")
  @APIResponses(value = {
      @APIResponse(responseCode = "201", description = "Obračun dodan i komanda poslana"),
      @APIResponse(responseCode = "500", description = "Greška u dodavanju obračuna")
  })
  @Counted(name = "brojZahtjeva_postObracunWS", description = "Broj POST zahtjeva na /obracun/ws")
  @Timed(name = "trajanjeMetode_postObracunWS", description = "Vrijeme izvršavanja metode postObracunWS")
  public Response postObracunWs(List<Obracun> stavkeObracuna) {
    if (stavkeObracuna == null || stavkeObracuna.isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Obračun je prazan").build();
    }

    try (var veza = this.restConfiguration.dajVezu()) {
      var partnerDAO = new PartnerDAO(veza);
      var obracunDAO = new ObracunDAO(veza);

      int idPartnera = stavkeObracuna.get(0).partner();
      boolean sviIstiPartner = stavkeObracuna.stream().allMatch(o -> o.partner() == idPartnera);
      if (!sviIstiPartner) {
        return Response.status(Response.Status.BAD_REQUEST)
            .entity("Sve stavke moraju biti za istog partnera").build();
      }

      var partner = partnerDAO.dohvati(idPartnera, false);
      if (partner == null) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Nepostojeći partner").build();
      }

      boolean dodan = obracunDAO.dodajSve(stavkeObracuna);
      if (!dodan) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
      }

      var gson = new com.google.gson.Gson();
      var json = gson.toJson(stavkeObracuna);

      try (
        var socket = new Socket(this.tvrtkaAdresa, Integer.parseInt(this.mreznaVrataRad));
        var ulaz = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf8"));
        var izlaz = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf8"))
      ) {
        izlaz.write("OBRAČUN " + idPartnera + " " + partner.sigurnosniKod() + "\n");
        izlaz.write(json + "\n");
        izlaz.flush();

        var odgovor = ulaz.readLine();

        if (!"OK".equalsIgnoreCase(odgovor)) {
          return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.status(Response.Status.CREATED).entity(stavkeObracuna).build();
      }

    } catch (Exception e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  
  /**
   * Dodaje više obračuna u bazu bez slanja poslužitelju.
   *
   * @param obracuni lista obračuna
   * @return HTTP 201 ako je uspješno, 500 ako nije
   */
  @Path("obracun")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Dodavanje više obračuna")
  @APIResponses(value = {
      @APIResponse(responseCode = "201", description = "Obračuni su uspješno pohranjeni"),
      @APIResponse(responseCode = "500", description = "Interna pogreška")
  })
  @Counted(name = "brojZahtjeva_postObracun", description = "Koliko puta je pozvana POST /obracun")
  @Timed(name = "trajanjeMetode_postObracun", description = "Vrijeme trajanja metode postObracun")
  public Response postObracun(List<Obracun> obracuni) {
    try (var vezaBP = this.restConfiguration.dajVezu()) {
      var obracunDAO = new ObracunDAO(vezaBP);
      boolean uspjeh = obracunDAO.dodajSve(obracuni);
      if (uspjeh)
        return Response.status(Response.Status.CREATED).build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
  }
  
  /**
   * Dohvaća sve obračune u opcionalnom vremenskom rasponu.
   *
   * @param vrijemeOd početno vrijeme (epoch), opcionalno
   * @param vrijemeDo završno vrijeme (epoch), opcionalno
   * @return HTTP 200 s listom obračuna ili 500 ako dođe do pogreške
   */
  @Path("obracun")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Dohvat obračuna s opcionalnim vremenskim filtrima")
  @APIResponses(value = {
      @APIResponse(responseCode = "200", description = "Obračuni dohvaćeni"),
      @APIResponse(responseCode = "500", description = "Interna pogreška")
  })
  @Counted(name = "brojZahtjeva_getObracuni", description = "Broj GET /obracun poziva")
  @Timed(name = "trajanjeMetode_getObracuni", description = "Trajanje metode GET /obracun")
  public Response getObracuni(
      @QueryParam("od") Long vrijemeOd,
      @QueryParam("do") Long vrijemeDo
  ) {
    try (var veza = this.restConfiguration.dajVezu()) {
      var obracunDAO = new ObracunDAO(veza);
      var sviObracuni = obracunDAO.dohvatiSve();

      List<Obracun> filtrirano = sviObracuni.stream()
        .filter(o -> (vrijemeOd == null || o.vrijeme() >= vrijemeOd) &&
                     (vrijemeDo == null || o.vrijeme() <= vrijemeDo))
        .toList();

      return Response.ok(filtrirano).build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }
  
  /**
   * Dohvaća obračune koji se odnose samo na jela.
   *
   * @param od početno vrijeme (epoch), opcionalno
   * @param kraj završno vrijeme (epoch), opcionalno
   * @return HTTP 200 s listom jela, ili 500 u slučaju pogreške
   */
  @GET
  @Path("obracun/jelo")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Dohvat obračuna za jela")
  @APIResponses(value = {
      @APIResponse(responseCode = "200", description = "Lista obračuna za jela"),
      @APIResponse(responseCode = "500", description = "Greška kod dohvaćanja")
  })
  public Response getObracuniJelo(@QueryParam("od") Long od, @QueryParam("do") Long kraj) {
    try (var veza = this.restConfiguration.dajVezu()) {
      var dao = new ObracunDAO(veza);
      var lista = dao.dohvatiSve().stream()
          .filter(o -> o.jelo())
          .filter(o -> (od == null || o.vrijeme() >= od) && (kraj == null || o.vrijeme() <= kraj))
          .toList();
      return Response.ok(lista).build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Dohvaća obračune koji se odnose samo na pića.
   *
   * @param od početno vrijeme (epoch), opcionalno
   * @param kraj završno vrijeme (epoch), opcionalno
   * @return HTTP 200 s listom pića, ili 500 u slučaju pogreške
   */
  @GET
  @Path("obracun/pice")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Dohvat obračuna za pića")
  @APIResponses(value = {
      @APIResponse(responseCode = "200", description = "Lista obračuna za pića"),
      @APIResponse(responseCode = "500", description = "Greška kod dohvaćanja")
  })
  public Response getObracuniPice(@QueryParam("od") Long od, @QueryParam("do") Long kraj) {
    try (var veza = this.restConfiguration.dajVezu()) {
      var dao = new ObracunDAO(veza);
      var lista = dao.dohvatiSve().stream()
          .filter(o -> !o.jelo())
          .filter(o -> (od == null || o.vrijeme() >= od) && (kraj == null || o.vrijeme() <= kraj))
          .toList();
      return Response.ok(lista).build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Dohvaća sve obračune za određenog partnera, u opcionalnom vremenskom rasponu.
   *
   * @param id ID partnera
   * @param od početno vrijeme (epoch), opcionalno
   * @param kraj završno vrijeme (epoch), opcionalno
   * @return HTTP 200 s listom obračuna, ili 500 u slučaju pogreške
   */
  @GET
  @Path("obracun/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Dohvat obračuna za određenog partnera")
  @APIResponses(value = {
      @APIResponse(responseCode = "200", description = "Lista obračuna za partnera"),
      @APIResponse(responseCode = "500", description = "Greška kod dohvaćanja")
  })
  public Response getObracuniZaPartnera(@PathParam("id") int id, @QueryParam("od") Long od, @QueryParam("do") Long kraj) {
    try (var veza = this.restConfiguration.dajVezu()) {
      var dao = new ObracunDAO(veza);
      var lista = dao.dohvatiSve().stream()
          .filter(o -> o.partner() == id)
          .filter(o -> (od == null || o.vrijeme() >= od) && (kraj == null || o.vrijeme() <= kraj))
          .toList();
      return Response.ok(lista).build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  
  /**
   * Dohvaća sve jelovnike svih registriranih partnera.
   *
   * @return HTTP 200 s listom jelovnika ili 500 u slučaju greške
   */
  @Path("jelovnik")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Dohvat svih jelovnika registriranih partnera")
  @APIResponses(value = {
      @APIResponse(responseCode = "200", description = "Uspješan dohvat jelovnika"),
      @APIResponse(responseCode = "500", description = "Greška pri dohvaćanju jelovnika")
  })
  @Counted(name = "brojZahtjeva_getJelovnici", description = "Broj GET /jelovnik poziva")
  @Timed(name = "trajanjeMetode_getJelovnici", description = "Trajanje GET /jelovnik metode")
  public Response getJelovnik() {
    try (var vezaBP = this.restConfiguration.dajVezu()) {
      var partnerDAO = new PartnerDAO(vezaBP);
      var partneri = partnerDAO.dohvatiSve(false);
      var tvrtkaAdresa = this.tvrtkaAdresa;
      var mreznaVrataRad = Integer.parseInt(this.mreznaVrataRad);

      var gson = new com.google.gson.Gson();
      var sviJelovnici = new ArrayList<>();
      var mapaJelovnika = new HashMap<String, java.util.List<Jelovnik>>();

      for (Partner p : partneri) {
        try (Socket s = new Socket(tvrtkaAdresa, mreznaVrataRad)) {
          var out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), "utf8"));
          var in = new BufferedReader(new InputStreamReader(s.getInputStream(), "utf8"));
          out.write("JELOVNIK " + p.id() + " " + p.sigurnosniKod() + "\n");
          out.flush();
          s.shutdownOutput();

          if (!"OK".equals(in.readLine())) continue;
          String json = in.readLine();
          Jelovnik[] jelovnici = gson.fromJson(json, Jelovnik[].class);
          for (Jelovnik j : jelovnici) {
            String tipKuhinje = j.id().substring(0, 2); // note to self "MK" iz "MK1"
            mapaJelovnika.computeIfAbsent(tipKuhinje, k -> new ArrayList<>()).add(j);
          }
          s.shutdownInput();
        } catch (Exception e) {
        }
      }
      return Response.ok(mapaJelovnika).build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }
  
  /**
   * Dohvaća jelovnik određenog partnera.
   *
   * @param id ID partnera
   * @return HTTP 200 s JSON jelovnikom, 404 ako partner ne postoji, ili 500 za ostale greške
   */
  @Path("jelovnik/{id}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Dohvat jelovnika partnera po ID-u")
  @APIResponses(value = {
      @APIResponse(responseCode = "200", description = "Uspješan dohvat jelovnika"),
      @APIResponse(responseCode = "404", description = "Partner nije pronađen"),
      @APIResponse(responseCode = "500", description = "Greška prilikom dohvaćanja jelovnika")
  })
  @Counted(name = "brojZahtjeva_getJelovnik", description = "Koliko puta je pozvana metoda /jelovnik/{id}")
  @Timed(name = "trajanjeMetode_getJelovnik", description = "Vrijeme trajanja metode /jelovnik/{id}")
  public Response getJelovnikPartnera(@PathParam("id") int id) {
    try (var veza = restConfiguration.dajVezu()) {
      var partnerDAO = new PartnerDAO(veza);
      var partner = partnerDAO.dohvati(id, false);
      if (partner == null) {
        return Response.status(Response.Status.NOT_FOUND).build();
      }

      String adresa = this.tvrtkaAdresa;
      int port = Integer.parseInt(this.mreznaVrataRad);
      String sigKod = partner.sigurnosniKod();

      try (Socket socket = new Socket(adresa, port);
           PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf8"));
           BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf8"))) {

        String komanda = "JELOVNIK " + id + " " + sigKod;
        out.write(komanda + "\n");
        out.flush();
        socket.shutdownOutput();

        String odgovor = in.readLine();
        if (!"OK".equals(odgovor)) {
          return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        String json = in.readLine();
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
      }

    } catch (Exception e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Dohvaća kartu pića svih registriranih partnera.
   *
   * @return HTTP 200 s listom pića ili 500 u slučaju greške
   */
  @Path("kartapica")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Dohvat karte pića registriranih partnera")
  @APIResponses(value = {
      @APIResponse(responseCode = "200", description = "Uspješan dohvat karte pića"),
      @APIResponse(responseCode = "500", description = "Greška pri dohvaćanju podataka")
  })
  @Counted(name = "brojZahtjeva_getKartaPica", description = "Broj GET /kartapica poziva")
  @Timed(name = "trajanjeMetode_getKartaPica", description = "Trajanje GET /kartapica metode")
  public Response getKartaPica() {
    try (var vezaBP = this.restConfiguration.dajVezu()) {
      var partnerDAO = new PartnerDAO(vezaBP);
      var partneri = partnerDAO.dohvatiSve(false);
      var tvrtkaAdresa = this.tvrtkaAdresa;
      var mreznaVrataRad = Integer.parseInt(this.mreznaVrataRad);

      var gson = new com.google.gson.Gson();
      var svePice = new java.util.ArrayList<>();

      for (Partner p : partneri) {
        try (Socket s = new Socket(tvrtkaAdresa, mreznaVrataRad)) {
          var out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), "utf8"));
          var in = new BufferedReader(new InputStreamReader(s.getInputStream(), "utf8"));
          out.write("KARTAPIĆA " + p.id() + " " + p.sigurnosniKod() + "\n");
          out.flush();
          s.shutdownOutput();
          if (!"OK".equals(in.readLine())) continue;
          String json = in.readLine();
          KartaPica[] pica = gson.fromJson(json, KartaPica[].class);
          svePice.addAll(java.util.List.of(pica));
          s.shutdownInput();
        } catch (Exception e) {
        }
      }
      return Response.ok(svePice).build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }
  
  /**
   * Šalje komandu poslužitelju da spava određeni broj milisekundi.
   *
   * @param trajanje broj milisekundi spavanja
   * @return HTTP 200 ako je uspješno, 400 za nevaljan parametar, 500 u slučaju greške
   */
  @GET
  @Path("spava")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Zatraži od PosluziteljTvrtka da spava određeni broj milisekundi")
  @APIResponses(value = {
      @APIResponse(responseCode = "200", description = "Poslužitelj je uspješno prešao u stanje spavanja"),
      @APIResponse(responseCode = "400", description = "Parametar 'vrijeme' nije valjan"),
      @APIResponse(responseCode = "500", description = "Greška u komunikaciji s poslužiteljem")
  })
  @Counted(name = "brojZahtjeva_getSpava", description = "Broj GET zahtjeva na /spava")
  @Timed(name = "trajanjeMetode_getSpava", description = "Vrijeme izvršavanja metode getSpava")
  public Response getSpava(@QueryParam("vrijeme") Long trajanje) {
    if (trajanje == null || trajanje < 0) {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("Parametar 'vrijeme' mora biti pozitivan broj").build();
    }
    try (
      var socket = new Socket(this.tvrtkaAdresa, Integer.parseInt(this.mreznaVrataKraj));
      var ulaz = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf8"));
      var izlaz = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf8"))
    ) {
      izlaz.write("SPAVA " + this.kodZaAdminTvrtke + " " + trajanje + "\n");
      izlaz.flush();
      var odgovor = ulaz.readLine();

      if (!"OK".equalsIgnoreCase(odgovor)) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity("Poslužitelj nije prihvatio SPAVA komandu").build();
      }
      return Response.status(Response.Status.OK).build();

    } catch (Exception e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("Greška prilikom slanja SPAVA komande").build();
    }
  }
  
  @Inject
  @RestClient
  TvrtkaInfoKlijent tvrtkaInfoKlijent;
  
  
  @RegisterRestClient(configKey = "klijentTvrtkaInfo")
  @Path("kraj")
  public interface TvrtkaInfoKlijent {

    @GET
    @Path("info")
    @Produces("application/json")
    Response dohvatiInfoZaKraj();
  }
}
