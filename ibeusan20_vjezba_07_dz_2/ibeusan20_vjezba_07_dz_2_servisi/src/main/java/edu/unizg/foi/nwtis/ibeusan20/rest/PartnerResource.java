package edu.unizg.foi.nwtis.ibeusan20.rest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.sql.Connection;
import java.util.List;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_07_dz_2.dao.KorisnikDAO;
import edu.unizg.foi.nwtis.podaci.Jelovnik;
import edu.unizg.foi.nwtis.podaci.KartaPica;
import edu.unizg.foi.nwtis.podaci.Korisnik;
import edu.unizg.foi.nwtis.podaci.Narudzba;
import jakarta.inject.Inject;
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
 * REST resurs za upravljanje funkcionalnostima PosluziteljaPartner.
 * Omogućuje upravljanje statusom poslužitelja, radom s korisnicima,
 * jelovnikom, pićem, narudžbama i izradom računa.
 * Također komunicira s poslužiteljem preko socket veza.
 * 
 * Ruta resursa: /api/partner
 * 
 * @author Ivan Beusan
 */
@Path("api/partner")
public class PartnerResource {

  /** Rest konfiguracija. */
  @Inject
  RestConfiguration restConfiguration;

  /** Adresa partnera. */
  @Inject
  @ConfigProperty(name = "adresaPartner")
  String partnerAdresa;
  
  /** Mrežna vrata za rad partnera. */
  @Inject
  @ConfigProperty(name = "mreznaVrataRadPartner")
  String partnerPort;
  
  /** Mrežna vrata za kraj partnera. */
  @Inject
  @ConfigProperty(name = "mreznaVrataKrajPartner")
  String partnerPortKraj;
  
  /** Kod za admin partnera. */
  @Inject
  @ConfigProperty(name = "kodZaAdminPartnera")
  String kodZaAdminPartnera;
  
  /** Kod za kraj. */
  @Inject
  @ConfigProperty(name = "kodZaKraj")
  String kodZaKraj;

  /**
   * Provjerava je li poslužitelj partner aktivan.
   * 
   * @return HTTP 200 ako je aktivan, 500 ako nije.
   */
  @HEAD
  @Operation(summary = "Provjerava da li radi PosluziteljPartner")
  @APIResponse(responseCode = "200", description = "Poslužitelj je aktivan")
  @APIResponse(responseCode = "500", description = "Poslužitelj nije dostupan")
  public Response headPartner() {
    try {
      var odgovor = posaljiKomandu("STATUS\n");
      if (odgovor != null) {
        return Response.status(Response.Status.OK).build();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
  }

  /**
   * Dohvaća status dijela poslužitelja.
   * 
   * @param id ID dijela poslužitelja.
   * @return HTTP 200 ako je aktivan, 204 ako nije pronađen.
   */
  @HEAD
  @Path("status/{id}")
  @Operation(summary = "Vraća status dijela poslužitelja PosluziteljPartner")
  @APIResponses(
      value = {@APIResponse(responseCode = "200", description = "Dio poslužitelja je aktivan"),
          @APIResponse(responseCode = "204", description = "Nepostojeći dio poslužitelja")})
  public Response headStatus(@PathParam("id") String id) {
    var odgovor = posaljiKomanduNaKraj("STATUS " + this.kodZaAdminPartnera + " " + id);
    if (odgovor != null && odgovor.startsWith("OK")) {
      return Response.status(Response.Status.OK).build();
    }
    return Response.status(Response.Status.NO_CONTENT).build();
  }

  /**
   * Pauzira rad dijela poslužitelja.
   * 
   * @param id ID dijela poslužitelja.
   * @return HTTP 200 ako je pauziran, 204 ako nije pronađen.
   */
  @HEAD
  @Path("pauza/{id}")
  @Operation(summary = "Pauzira rad dijela poslužitelja osim kontrola")
  @APIResponses(value = {@APIResponse(responseCode = "200", description = "Dio uspješno pauziran"),
      @APIResponse(responseCode = "204", description = "Pogrešan ID dijela")})
  public Response headPauza(@PathParam("id") String id) {
    var odgovor = posaljiKomanduNaKraj("PAUZA " + this.kodZaAdminPartnera + " " + id);
    if (odgovor != null && odgovor.startsWith("OK")) {
      return Response.status(Response.Status.OK).build();
    }
    return Response.status(Response.Status.NO_CONTENT).build();
  }

  /**
   * Pokreće rad dijela poslužitelja.
   * 
   * @param id ID dijela poslužitelja.
   * @return HTTP 200 ako je pokrenut, 204 ako nije pronađen.
   */
  @HEAD
  @Path("start/{id}")
  @Operation(summary = "Pokreće rad dijela poslužitelja osim kontrola")
  @APIResponses(value = {@APIResponse(responseCode = "200", description = "Dio uspješno pokrenut"),
      @APIResponse(responseCode = "204", description = "Pogrešan ID dijela")})
  public Response headStart(@PathParam("id") String id) {
    var odgovor = posaljiKomanduNaKraj("START " + this.kodZaAdminPartnera + " " + id);
    if (odgovor != null && odgovor.startsWith("OK")) {
      return Response.status(Response.Status.OK).build();
    }
    return Response.status(Response.Status.NO_CONTENT).build();
  }

  /**
   * Zaustavlja poslužitelj partnera.
   * 
   * @return HTTP 200 ako je poslužitelj ugašen, 204 ako nije.
   */
  @HEAD
  @Path("kraj")
  @Operation(summary = "Šalje komandu za kraj rada poslužitelja")
  @APIResponses(value = {@APIResponse(responseCode = "200", description = "Poslužitelj se gasi"),
      @APIResponse(responseCode = "204", description = "Neuspjelo gašenje")})
  public Response headKraj() {
    var odgovor = posaljiKomanduNaKraj("KRAJ " + this.kodZaKraj);
    if (odgovor != null && ((String) odgovor).startsWith("OK")) {
      return Response.status(Response.Status.OK).build();
    }
    return Response.status(Response.Status.NO_CONTENT).build();
  }
  
  /**
   * Vraća jelovnik nakon provjere autentikacije korisnika.
   * 
   * @param korisnik korisničko ime.
   * @param lozinka korisnička lozinka.
   * @return HTTP 200 i JSON jelovnik ili odgovarajući kod greške.
   */
  @GET
  @Path("jelovnik")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Vraća jelovnik")
  @APIResponses(
      value = {@APIResponse(responseCode = "200", description = "Uspješan dohvat jelovnika"),
          @APIResponse(responseCode = "401", description = "Neautoriziran pristup"),
          @APIResponse(responseCode = "500", description = "Greška prilikom komunikacije")})
  public Response getJelovnik(@HeaderParam("korisnik") String korisnik,
      @HeaderParam("lozinka") String lozinka) {
    if (!autentificirajKorisnika(korisnik, lozinka)) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    var odgovor = posaljiKomandu("JELOVNIK " + korisnik);
    if (odgovor != null && odgovor.startsWith("OK\n")) {
      String json = odgovor.substring(3).trim();
      try {
          Gson gson = new Gson();
          Type tipListe = new TypeToken<List<Jelovnik>>() {}.getType();
          List<Jelovnik> jelovnici = gson.fromJson(json, tipListe);
          return Response.ok(jelovnici).build();
      } catch (Exception e) {
          return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Greška pri parsiranju JSON-a").build();
      }
  }
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
  }

  /**
   * Vraća kartu pića nakon autentikacije korisnika.
   * 
   * @param korisnik korisničko ime.
   * @param lozinka korisnička lozinka.
   * @return HTTP 200 i JSON lista pića ili odgovarajući kod greške.
   */
  @GET
  @Path("kartapica")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Vraća kartu pića")
  @APIResponses(
      value = {@APIResponse(responseCode = "200", description = "Uspješan dohvat karte pića"),
          @APIResponse(responseCode = "401", description = "Neautoriziran pristup"),
          @APIResponse(responseCode = "500", description = "Greška prilikom komunikacije")})
  public Response getKartaPica(@HeaderParam("korisnik") String korisnik,
      @HeaderParam("lozinka") String lozinka) {
    if (!autentificirajKorisnika(korisnik, lozinka)) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    var odgovor = posaljiKomandu("KARTAPIĆA " + korisnik);
    if (odgovor != null && odgovor.startsWith("OK\n")) {
      String json = odgovor.substring(3).trim();
      try {
          Gson gson = new Gson();
          Type tipListe = new TypeToken<List<KartaPica>>() {}.getType();
          List<KartaPica> kartaPica = gson.fromJson(json, tipListe);
          return Response.ok(kartaPica).build();
      } catch (Exception e) {
          return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Greška pri parsiranju JSON-a").build();
      }
  }
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
  }
  
  /**
   * Dohvaća stavke otvorene narudžbe korisnika.
   * 
   * @param korisnik korisničko ime.
   * @param lozinka korisnička lozinka.
   * @return HTTP 200 s JSON stavkama ili odgovarajući kod greške.
   */
  @GET
  @Path("narudzba")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Vraća stavke otvorene narudžbe korisnika")
  @APIResponses({
      @APIResponse(responseCode = "200", description = "Stavke narudžbe vraćene"),
      @APIResponse(responseCode = "401", description = "Neautoriziran pristup"),
      @APIResponse(responseCode = "500", description = "Ne postoji otvorena narudžba / greška")
  })
  public Response getNarudzba(@HeaderParam("korisnik") String korisnik,
                              @HeaderParam("lozinka") String lozinka) {
      if (!autentificirajKorisnika(korisnik, lozinka)) {
          return Response.status(Response.Status.UNAUTHORIZED).build();
      }
      String komanda = "STANJE " + korisnik;
      String odgovor = posaljiKomandu(komanda);
      if (odgovor == null) {
          return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                         .entity("Nije moguće dohvatiti stanje narudžbe").build();
      }
      if (odgovor.startsWith("ERROR 43")) {
          return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                         .entity("Nema otvorene narudžbe za korisnika").build();
      }
      if (odgovor.startsWith("OK")) {
        try {
            String json = odgovor.substring(2).trim();
            Gson gson = new Gson();
            Type tipListe = new TypeToken<List<Narudzba>>() {}.getType();
            List<Narudzba> narudzbe = gson.fromJson(json, tipListe);
            return Response.ok(narudzbe).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Greška u parsiranju odgovora").build();
        }
    }
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                     .entity("Neočekivan odgovor poslužitelja").build();
  }

  /**
   * Dodaje novu narudžbu za korisnika.
   * 
   * @param korisnik korisničko ime.
   * @param lozinka korisnička lozinka.
   * @return HTTP 201 ako je dodano, 409 ako već postoji, 401/500 za greške.
   */
  @POST
  @Path("narudzba")
  @Operation(summary = "Dodaje novu narudžbu korisniku")
  @APIResponses(value = {@APIResponse(responseCode = "201", description = "Narudžba dodana"),
      @APIResponse(responseCode = "401", description = "Neautoriziran pristup"),
      @APIResponse(responseCode = "409", description = "Narudžba već postoji"),
      @APIResponse(responseCode = "500", description = "Greška prilikom komunikacije")})
  public Response postNarudzba(@HeaderParam("korisnik") String korisnik,
      @HeaderParam("lozinka") String lozinka) {
    if (!autentificirajKorisnika(korisnik, lozinka)) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    String odgovor = posaljiKomandu("NARUDŽBA " + korisnik);
    if (odgovor == null) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    } else if (odgovor.startsWith("ERROR 44")) {
      return Response.status(Response.Status.CONFLICT).entity("Narudžba već postoji").build();
    } else if (odgovor.startsWith("OK")) {
      return Response.status(Response.Status.CREATED).build();
    }
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .entity("Neuspješno dodavanje narudžbe").build();
  }
  
  /**
   * Dodaje novo jelo u narudžbu.
   * 
   * @param korisnik korisničko ime.
   * @param lozinka korisnička lozinka.
   * @param narudzba objekt narudžbe.
   * @return HTTP 201 ako je dodano, 409 ako nema narudžbe, 401/500 za greške.
   */
  @POST
  @Path("jelo")
  @Consumes(MediaType.APPLICATION_JSON)
  @Operation(summary = "Dodaje novo jelo u narudžbu")
  @APIResponses(value = {
      @APIResponse(responseCode = "201", description = "Jelo dodano u narudžbu"),
      @APIResponse(responseCode = "401", description = "Neautoriziran pristup"),
      @APIResponse(responseCode = "409", description = "Ne postoji otvorena narudžba"),
      @APIResponse(responseCode = "500", description = "Greška prilikom komunikacije")
  })
  public Response postJelo(@HeaderParam("korisnik") String korisnik,
                           @HeaderParam("lozinka") String lozinka,
                           Narudzba narudzba) {
      if (!autentificirajKorisnika(korisnik, lozinka)) {
          return Response.status(Response.Status.UNAUTHORIZED).build();
      }
      String komanda = String.format("JELO %s %s %.1f", korisnik, narudzba.id(), narudzba.kolicina());
      String odgovor = posaljiKomandu(komanda);

      if (odgovor == null) {
          return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
      } else if (odgovor.startsWith("ERROR 48")) {
          return Response.status(Response.Status.CONFLICT).entity("Nema otvorene narudžbe").build();
      } else if (odgovor.startsWith("OK")) {
          return Response.status(Response.Status.CREATED).build();
      }
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Greška u dodavanju jela").build();
  }

  /**
   * Dodaje novo piće u narudžbu.
   * 
   * @param korisnik korisničko ime.
   * @param lozinka korisnička lozinka.
   * @param narudzba objekt narudžbe.
   * @return HTTP 201 ako je dodano, 409 ako nema narudžbe, 401/500 za greške.
   */
  @POST
  @Path("pice")
  @Consumes(MediaType.APPLICATION_JSON)
  @Operation(summary = "Dodaje novo piće u narudžbu")
  @APIResponses(value = {
      @APIResponse(responseCode = "201", description = "Piće dodano u narudžbu"),
      @APIResponse(responseCode = "401", description = "Neautoriziran pristup"),
      @APIResponse(responseCode = "409", description = "Ne postoji otvorena narudžba"),
      @APIResponse(responseCode = "500", description = "Greška prilikom komunikacije")
  })
  public Response postPice(@HeaderParam("korisnik") String korisnik,
                           @HeaderParam("lozinka") String lozinka,
                           Narudzba narudzba) {
      if (!autentificirajKorisnika(korisnik, lozinka)) {
          return Response.status(Response.Status.UNAUTHORIZED).build();
      }
      String komanda = String.format("PIĆE %s %s %.1f", korisnik, narudzba.id(), narudzba.kolicina());
      String odgovor = posaljiKomandu(komanda);
      if (odgovor == null) {
          return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
      } else if (odgovor.startsWith("ERROR 44")) {
          return Response.status(Response.Status.CONFLICT).entity("Nema otvorene narudžbe").build();
      } else if (odgovor.startsWith("OK")) {
          return Response.status(Response.Status.CREATED).build();
      }
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Greška u dodavanju pića").build();
  }
  
  /**
   * Generira račun za otvorenu narudžbu korisnika.
   * 
   * @param korisnik korisničko ime.
   * @param lozinka korisnička lozinka.
   * @return HTTP 201 ako je generirano, 409/401/500 za greške.
   */
  @POST
  @Path("racun")
  @Operation(summary = "Zahtijeva račun za otvorenu narudžbu korisnika")
  @APIResponses(value = {
      @APIResponse(responseCode = "201", description = "Račun uspješno generiran"),
      @APIResponse(responseCode = "401", description = "Neautoriziran pristup"),
      @APIResponse(responseCode = "409", description = "Nema otvorene narudžbe"),
      @APIResponse(responseCode = "500", description = "Greška prilikom komunikacije")
  })
  public Response postRacun(
      @HeaderParam("korisnik") String korisnik,
      @HeaderParam("lozinka") String lozinka
  ) {
      if (!autentificirajKorisnika(korisnik, lozinka)) {
          return Response.status(Response.Status.UNAUTHORIZED).build();
      }
      String odgovor = posaljiKomandu("RAČUN " + korisnik);
      if (odgovor == null) {
          return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                         .entity("Greška prilikom komunikacije").build();
      } else if (odgovor.startsWith("ERROR 44")) {
          return Response.status(Response.Status.CONFLICT)
                         .entity("Nema otvorene narudžbe").build();
      } else if (odgovor.startsWith("OK")) {
          return Response.status(Response.Status.CREATED)
              .entity("Račun uspješno kreiran").build();
      }
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                     .entity("Neuspješno slanje zahtjeva za račun").build();
  }
  
  /**
   * Vraća sve korisnike iz baze.
   * 
   * @return HTTP 200 s listom korisnika ili 500 za grešku.
   */
  @GET
  @Path("korisnik")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Vraća korisnike")
  @APIResponses({
      @APIResponse(responseCode = "200", description = "Lista korisnika vraćena"),
      @APIResponse(responseCode = "500", description = "Greška u komunikaciji")
  })
  public Response getKorisnici() {
    try (var vezaBP = this.restConfiguration.dajVezu()) {
      var korisnikDAO = new KorisnikDAO(vezaBP);
      var korisnici = korisnikDAO.dohvatiSve();
      return Response.ok(korisnici).status(Response.Status.OK).build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Vraća podatke korisnika po ID-u.
   * 
   * @param id identifikator korisnika.
   * @return HTTP 200 ako je pronađen, 404/500 ako nije ili je greška.
   */
  @GET
  @Path("korisnik/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Vraća korisnika prema ID")
  @APIResponses({
      @APIResponse(responseCode = "200", description = "Korisnik pronađen"),
      @APIResponse(responseCode = "404", description = "Korisnik nije pronađen"),
      @APIResponse(responseCode = "500", description = "Greška u komunikaciji")
  })
  public Response getKorisnik(@PathParam("id") String id) {
      try (var vezaBP = this.restConfiguration.dajVezu()) {
        var korisnikDAO = new KorisnikDAO(vezaBP);
        var korisnik = korisnikDAO.dohvati(id, null, false);
        if (korisnik == null) {
          return Response.status(Response.Status.NOT_FOUND)
                         .entity("Korisnik s ID '" + id + "' nije pronađen")
                         .build();
      }
        return Response.ok(korisnik).status(Response.Status.OK).build();
      } catch (Exception e) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
      }
  }

  /**
   * Dodaje novog korisnika u bazu.
   * 
   * @param korisnik objekt korisnika.
   * @return HTTP 201 ako je dodano, 409/500 za greške.
   */
  @POST
  @Path("korisnik")
  @Consumes(MediaType.APPLICATION_JSON)
  @Operation(summary = "Dodaje novog korisnika")
  @APIResponses({
      @APIResponse(responseCode = "201", description = "Korisnik uspješno dodan"),
      @APIResponse(responseCode = "409", description = "Korisnik već postoji"),
      @APIResponse(responseCode = "500", description = "Greška u komunikaciji")
  })
  public Response postKorisnik(Korisnik korisnik) {
      try (var vezaBP = this.restConfiguration.dajVezu()) {
          var korisnikDAO = new KorisnikDAO(vezaBP);

          boolean postoji = korisnikDAO.dohvati(korisnik.korisnik(), null, false) != null;
          if (postoji) {
              return Response.status(Response.Status.CONFLICT).entity("Korisnik već postoji").build();
          }
          boolean dodan = korisnikDAO.dodaj(korisnik);
          if (dodan) {
              return Response.status(Response.Status.CREATED).build();
          } else {
              return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Greška u dodavanju korisnika").build();
          }
      } catch (Exception e) {
          e.printStackTrace();
          return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Greška u komunikaciji s bazom").build();
      }
  }
  
  /**
   * Poslužitelj spava određeno vrijeme u milisekundama.
   * 
   * @param vrijeme vrijeme spavanja u milisekundama.
   * @return HTTP 200 ako je uspješno, 500 za greške.
   */
  @GET
  @Path("spava")
  @Operation(summary = "Poslužitelj spava zadano vrijeme")
  @APIResponses({
      @APIResponse(responseCode = "200", description = "Uspješno izvršeno spavanje"),
      @APIResponse(responseCode = "500", description = "Greška prilikom slanja zahtjeva za spavanje")
  })
  public Response getSpava(@QueryParam("vrijeme") long vrijeme) {
      if (vrijeme <= 0) {
          return Response.status(Response.Status.BAD_REQUEST)
                         .entity("Vrijeme mora biti pozitivno").build();
      }

      String komanda = "SPAVA " + this.kodZaAdminPartnera + " " + vrijeme;
      String odgovor = posaljiKomanduNaKraj(komanda);

      if (odgovor != null && odgovor.startsWith("OK")) {
          return Response.status(Response.Status.OK)
              .entity("Poslužitelj je uspješno spavao " + vrijeme + " ms").build();
      } else {
          return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                         .entity("Neuspješan zahtjev za spavanjem").build();
      }
  }


  /**
   * Pomoćna metoda za slanje komandi na port za rad.
   * 
   * @param komanda tekst komande
   * @return odgovor poslužitelja ili null
   */
  private String posaljiKomandu(String komanda) {
    try {
      var socket = new Socket(this.partnerAdresa, Integer.parseInt(this.partnerPort));
      BufferedReader ulaz =
          new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf8"));
      PrintWriter izlaz = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf8"));
      System.out.println("[DEBUG] Šaljem komandu - " + komanda);
      izlaz.println(komanda);
      izlaz.flush();
      socket.shutdownOutput();
      String linija1 = ulaz.readLine();
      String linija2 = ulaz.readLine();
      socket.shutdownInput();
      System.out.println(komanda + "->" + linija1 + " " + linija2);
      socket.close();
      if (linija1 == null) {
        return null;
      } else if (linija1.startsWith("OK")) {
        return linija2 != null ? "OK\n" + linija2 : "OK\n";
      } else {
        return linija1;
      }
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Pomoćna metoda za slanje komandi na administrativni port.
   * 
   * @param komanda tekst komande
   * @return odgovor poslužitelja ili null
   */
  private String posaljiKomanduNaKraj(String komanda) {
    try {
      var socket = new Socket(this.partnerAdresa, Integer.parseInt(this.partnerPortKraj));
      BufferedReader ulaz =
          new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf8"));
      PrintWriter izlaz = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf8"));
      System.out.println("[DEBUG] Šaljem komandu na kraj - " + komanda);
      izlaz.println(komanda);
      izlaz.flush();
      socket.shutdownOutput();
      var linija = ulaz.readLine();
      System.out.println(komanda + "->" + linija);
      socket.shutdownInput();
      socket.close();
      return linija;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Provjerava korisničke podatke na temelju korisničkog imena i lozinke.
   * 
   * @param korisnik korisničko ime
   * @param lozinka lozinka korisnika
   * @return true ako je korisnik autentificiran, inače false
   */
  private boolean autentificirajKorisnika(String korisnik, String lozinka) {
    try (Connection con = restConfiguration.dajVezu()) {
      KorisnikDAO dao = new KorisnikDAO(con);
      return dao.dohvati(korisnik, lozinka, true) != null;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
}
