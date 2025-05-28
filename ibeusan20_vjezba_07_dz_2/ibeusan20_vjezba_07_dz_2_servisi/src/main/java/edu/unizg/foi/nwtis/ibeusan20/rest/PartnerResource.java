package edu.unizg.foi.nwtis.ibeusan20.rest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_07_dz_2.dao.KorisnikDAO;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("api/partner")
public class PartnerResource {

  @Inject
  RestConfiguration restConfiguration;

  @Inject
  @ConfigProperty(name = "adresaPartner")
  String partnerAdresa;
  @Inject
  @ConfigProperty(name = "mreznaVrataRadPartner")
  String partnerPort;
  @Inject
  @ConfigProperty(name = "mreznaVrataKrajPartner")
  String partnerPortKraj;
  @Inject
  @ConfigProperty(name = "kodZaAdminPartnera")
  String kodZaAdminPartnera;
  @Inject
  @ConfigProperty(name = "kodZaKraj")
  String kodZaKraj;

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

  ///////////////////////////////////////////////////////////////////////////////////////
  @GET
  @Path("jelovnik")
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
      return Response.ok(json).build();
    }
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
  }

  @GET
  @Path("kartapica")
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
      return Response.ok(json).build();
    }
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
  }

  // @Path("narudzba")
  // @GET
  // @Produces({MediaType.APPLICATION_JSON})
  // @Operation(summary = "Vraća stavke otvorene narudžbe korisnika")
  // @APIResponses(value = {
  // @APIResponse(responseCode = "200", description = "Narudžba dohvaćena"),
  // @APIResponse(responseCode = "401", description = "Neautoriziran pristup"),
  // @APIResponse(responseCode = "500", description = "Greška prilikom komunikacije")
  // })
  // public Response getNarudzba(@HeaderParam("korisnik") String korisnik, @HeaderParam("lozinka")
  // String lozinka) {
  // if (!autentificirajKorisnika(korisnik, lozinka)) {
  // return Response.status(Response.Status.UNAUTHORIZED).build();
  // }
  // String odgovor = posaljiKomandu("NARUDŽBA " + korisnik);
  // if (odgovor != null && odgovor.startsWith("OK\n")) {
  // String json = odgovor.substring(3).trim();
  // return Response.ok(json).build();
  // }
  // return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
  // }

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
