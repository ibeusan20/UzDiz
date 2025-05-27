package edu.unizg.foi.nwtis.ibeusan20.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_07_dz_2.dao.ObracunDAO;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_07_dz_2.dao.PartnerDAO;
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
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("api/tvrtka")
public class TvrtkaResource {

  @Inject
  @ConfigProperty(name = "adresa")
  private String tvrtkaAdresa;
  @Inject
  @ConfigProperty(name = "mreznaVrataKraj")
  private String mreznaVrataKraj;
  @Inject
  @ConfigProperty(name = "mreznaVrataRegistracija")
  private String mreznaVrataRegistracija;
  @Inject
  @ConfigProperty(name = "mreznaVrataRad")
  private String mreznaVrataRad;
  @Inject
  @ConfigProperty(name = "kodZaAdminTvrtke")
  private String kodZaAdminTvrtke;
  @Inject
  @ConfigProperty(name = "kodZaKraj")
  private String kodZaKraj;

  @Inject
  RestConfiguration restConfiguration;

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

  @Path("status/{id}")
  @HEAD
  @Operation(summary = "Provjera statusa dijela poslužitelja tvrtka")
  @APIResponses(value = {@APIResponse(responseCode = "200", description = "Uspješna operacija"),
      @APIResponse(responseCode = "204", description = "Pogrešna operacija")})
  @Counted(name = "brojZahtjeva_eadPosluziteljStatus",
      description = "Koliko puta je pozvana operacija servisa")
  @Timed(name = "trajanjeMetode_eadPosluziteljStatus", description = "Vrijeme trajanja metode")
  public Response headPosluziteljStatus(@PathParam("id") int id) {
    var status = posaljiKomandu("STATUS " + this.kodZaAdminTvrtke + " " + id);
    if (status != null) {
      return Response.status(Response.Status.OK).build();
    } else {
      return Response.status(Response.Status.NO_CONTENT).build();
    }
  }

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

  @Path("kraj")
  @HEAD
  @Operation(summary = "Zaustavljanje poslužitelja tvrtka")
  @APIResponses(value = {@APIResponse(responseCode = "200", description = "Uspješna operacija"),
      @APIResponse(responseCode = "204", description = "Pogrešna operacija")})
  @Counted(name = "brojZahtjeva_headPosluziteljKraj",
      description = "Koliko puta je pozvana operacija servisa")
  @Timed(name = "trajanjeMetode_headPosluziteljKraj", description = "Vrijeme trajanja metode")
  public Response headPosluziteljKraj() {
    var status = posaljiKomandu("KRAJ " + this.kodZaKraj);
    if (status != null) {
      return Response.status(Response.Status.OK).build();
    } else {
      return Response.status(Response.Status.NO_CONTENT).build();
    }
  }

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
        System.out.println("[DEBUG] Slanje komande: POPIS");
        izlaz.println("POPIS");
        izlaz.flush();
        socket.shutdownOutput();

        var prviRed = ulaz.readLine();
        var drugiRed = ulaz.readLine();
        System.out.println("[DEBUG] POPIS status: " + prviRed);
        System.out.println("[DEBUG] POPIS JSON: " + drugiRed);

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
      e.printStackTrace(System.err);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @Path("partner")
  @POST
  @Consumes({MediaType.APPLICATION_JSON})
  @Produces({MediaType.APPLICATION_JSON})
  @Operation(summary = "Dohvat jednog partnera")
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
      System.out.println(komanda + " -> " + linija);
      mreznaUticnica.shutdownInput();
      mreznaUticnica.close();
      return linija;
    } catch (IOException e) {
    }
    return null;
  }
  
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
      var sviJelovnici = new java.util.ArrayList<>();

      for (Partner p : partneri) {
        try (Socket s = new Socket(tvrtkaAdresa, mreznaVrataRad)) {
          var out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), "utf8"));
          var in = new BufferedReader(new InputStreamReader(s.getInputStream(), "utf8"));
          System.out.println("[DEBUG] Pozivam partnera " + p.id() + " s kodom: " + p.sigurnosniKod() + " na socketu " + p.mreznaVrata());
          out.write("JELOVNIK " + p.id() + " " + p.sigurnosniKod() + "\n");
          out.flush();
          s.shutdownOutput();
          if (!"OK".equals(in.readLine())) continue;
          String json = in.readLine();
          Jelovnik[] jelovnici = gson.fromJson(json, Jelovnik[].class);
          sviJelovnici.addAll(java.util.List.of(jelovnici));
          s.shutdownInput();
        } catch (Exception e) {
          System.out.println("[WARN] Neuspješan partner ID: " + p.id());
        }
      }
      return Response.ok(sviJelovnici).build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }
  
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
          System.out.println("[WARN] Neuspješan partner ID: " + p.id());
        }
      }
      return Response.ok(svePice).build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }


}
